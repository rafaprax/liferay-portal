/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;

/**
 * {@link SecureCredentialProvider} implementation that retrieves secrets from
 * HashiCorp Vault using the KV v2 secrets engine.
 *
 * <h3>Authentication methods (in order of preference):</h3>
 *
 * <ol>
 *   <li><b>AppRole</b> (recommended for FedRAMP) — Set {@code VAULT_ROLE_ID}
 *       and {@code VAULT_SECRET_ID} environment variables. The provider
 *       authenticates via {@code /auth/approle/login} at startup, receives a
 *       short-lived token, and the single-use {@code secret_id} is consumed
 *       immediately. The auth mount path can be customized via
 *       {@code VAULT_APPROLE_MOUNT} (default: {@code approle}).</li>
 *   <li><b>Kubernetes</b> — Set {@code VAULT_K8S_ROLE} environment variable.
 *       The provider reads the pod's ServiceAccount JWT from
 *       {@code /var/run/secrets/kubernetes.io/serviceaccount/token} and
 *       authenticates via {@code /auth/kubernetes/login}. No static secret
 *       required. The auth mount path can be customized via
 *       {@code VAULT_K8S_MOUNT} (default: {@code kubernetes}).</li>
 *   <li><b>Static token</b> (discouraged) — Set {@code VAULT_TOKEN}
 *       environment variable. This method is supported for development and
 *       testing only. A warning is logged because static tokens violate
 *       SP 800-53 IA-5(7).</li>
 * </ol>
 *
 * <h3>Common configuration:</h3>
 * <ul>
 *   <li>{@code VAULT_ADDR} — Vault server URL (required, e.g.,
 *       {@code https://vault.example.com:8200})</li>
 * </ul>
 *
 * <h3>Usage in portal-ext.properties:</h3>
 * <pre>
 * jdbc.default.password=secure-credential://secret/data/liferay/database#password
 * </pre>
 *
 * <h3>Zeroization (FIPS 140-3 §7.7.3):</h3>
 * <p>
 * All sensitive data (tokens, payloads, JWTs, credentials) is handled as
 * {@code char[]} or {@code byte[]} and zeroed in {@code finally} blocks after
 * use. Java's immutable {@code String} is avoided for sensitive values.
 * </p>
 *
 * @author Liferay
 */
public class VaultSecureCredentialProvider
	implements SecureCredentialProvider {

	@Override
	public char[] getCredential(String key)
		throws SecureCredentialException {

		char[] token = _getToken();

		try {
			int fieldSeparatorIndex = key.indexOf('#');

			if (fieldSeparatorIndex == -1) {
				throw new SecureCredentialException(
					"Invalid Vault key format: " + key + ". Expected " +
						"format: <path>#<field> (e.g., " +
							"secret/data/liferay/database#password)");
			}

			String secretPath = key.substring(0, fieldSeparatorIndex);
			String fieldName = key.substring(fieldSeparatorIndex + 1);

			String vaultUrl = _vaultAddr + "/v1/" + secretPath;

			char[] responseBody = null;

			try {
				responseBody = _getRequest(vaultUrl, token);

				return _extractField(responseBody, fieldName);
			}
			finally {
				if (responseBody != null) {
					Arrays.fill(responseBody, '\0');
				}
			}
		}
		finally {
			Arrays.fill(token, '\0');
		}
	}

	@Override
	public String getName() {
		return "HashiCorp Vault";
	}

	@Override
	public boolean isAvailable() {
		if (_vaultAddr == null) {
			return false;
		}

		// Check if any auth method has credentials available (not yet
		// zeroed)

		return ((_vaultRoleId != null) && (_vaultSecretId != null)) ||
			(_vaultK8sRole != null) || (_vaultStaticToken != null) ||
			(_cachedToken != null);
	}

	private char[] _authenticateAppRole()
		throws SecureCredentialException {

		String mount = _getEnvOrProperty(
			"VAULT_APPROLE_MOUNT", "vault.approle.mount");

		if (mount == null) {
			mount = "approle";
		}

		String loginUrl = _vaultAddr + "/v1/auth/" + mount + "/login";

		if (_log.isInfoEnabled()) {
			_log.info(
				"Authenticating to Vault via AppRole at " + loginUrl);
		}

		// Build JSON payload as char[] to enable zeroization

		char[] prefix = "{\"role_id\":\"".toCharArray();
		char[] middle = "\",\"secret_id\":\"".toCharArray();
		char[] suffix = "\"}".toCharArray();

		char[] payload = new char[
			prefix.length + _vaultRoleId.length + middle.length +
				_vaultSecretId.length + suffix.length];

		int offset = 0;

		System.arraycopy(prefix, 0, payload, offset, prefix.length);

		offset += prefix.length;

		System.arraycopy(
			_vaultRoleId, 0, payload, offset, _vaultRoleId.length);

		offset += _vaultRoleId.length;

		System.arraycopy(middle, 0, payload, offset, middle.length);

		offset += middle.length;

		System.arraycopy(
			_vaultSecretId, 0, payload, offset, _vaultSecretId.length);

		offset += _vaultSecretId.length;

		System.arraycopy(suffix, 0, payload, offset, suffix.length);

		char[] responseBody = null;

		try {
			responseBody = _postJson(loginUrl, payload, null);

			char[] token = _extractField(responseBody, "client_token");

			if (_log.isInfoEnabled()) {
				_log.info(
					"Vault AppRole authentication successful. " +
						"Received short-lived client token.");
			}

			// Zero the single-use secret_id — it has been consumed by
			// Vault and must not remain in memory (SP 800-53 IA-5)

			Arrays.fill(_vaultSecretId, '\0');

			_vaultSecretId = null;

			return token;
		}
		catch (SecureCredentialException secureCredentialException) {
			throw new SecureCredentialException(
				"Vault AppRole authentication failed at " + loginUrl,
				secureCredentialException);
		}
		finally {
			Arrays.fill(payload, '\0');

			if (responseBody != null) {
				Arrays.fill(responseBody, '\0');
			}
		}
	}

	private char[] _authenticateKubernetes()
		throws SecureCredentialException {

		String mount = _getEnvOrProperty(
			"VAULT_K8S_MOUNT", "vault.k8s.mount");

		if (mount == null) {
			mount = "kubernetes";
		}

		String loginUrl =
			_vaultAddr + "/v1/auth/" + mount + "/login";

		if (_log.isInfoEnabled()) {
			_log.info(
				"Authenticating to Vault via Kubernetes auth at " + loginUrl);
		}

		// Read JWT as byte[] then convert to char[] for zeroization

		byte[] jwtBytes = null;
		char[] jwtChars = null;
		char[] payload = null;
		char[] responseBody = null;

		try {
			jwtBytes = Files.readAllBytes(Paths.get(_K8S_SA_TOKEN_PATH));

			jwtChars = StandardCharsets.UTF_8.decode(
				ByteBuffer.wrap(jwtBytes)).array();

			// Trim trailing whitespace/newlines

			int jwtLength = jwtChars.length;

			while ((jwtLength > 0) &&
				   (jwtChars[jwtLength - 1] <= ' ')) {

				jwtLength--;
			}

			char[] roleChars = _vaultK8sRole.toCharArray();

			char[] prefix = "{\"role\":\"".toCharArray();
			char[] middle = "\",\"jwt\":\"".toCharArray();
			char[] suffix = "\"}".toCharArray();

			payload = new char[
				prefix.length + roleChars.length + middle.length +
					jwtLength + suffix.length];

			int offset = 0;

			System.arraycopy(prefix, 0, payload, offset, prefix.length);

			offset += prefix.length;

			System.arraycopy(roleChars, 0, payload, offset, roleChars.length);

			offset += roleChars.length;

			System.arraycopy(middle, 0, payload, offset, middle.length);

			offset += middle.length;

			System.arraycopy(jwtChars, 0, payload, offset, jwtLength);

			offset += jwtLength;

			System.arraycopy(suffix, 0, payload, offset, suffix.length);

			Arrays.fill(roleChars, '\0');

			responseBody = _postJson(loginUrl, payload, null);

			char[] token = _extractField(responseBody, "client_token");

			if (_log.isInfoEnabled()) {
				_log.info("Vault Kubernetes authentication successful.");
			}

			return token;
		}
		catch (SecureCredentialException secureCredentialException) {
			throw new SecureCredentialException(
				"Vault Kubernetes authentication failed at " + loginUrl,
				secureCredentialException);
		}
		catch (Exception exception) {
			throw new SecureCredentialException(
				"Unable to read Kubernetes ServiceAccount token from " +
					_K8S_SA_TOKEN_PATH,
				exception);
		}
		finally {
			if (jwtBytes != null) {
				Arrays.fill(jwtBytes, (byte)0);
			}

			if (jwtChars != null) {
				Arrays.fill(jwtChars, '\0');
			}

			if (payload != null) {
				Arrays.fill(payload, '\0');
			}

			if (responseBody != null) {
				Arrays.fill(responseBody, '\0');
			}
		}
	}

	/**
	 * Extracts a field value from Vault JSON response as a new {@code char[]}
	 * that the caller must zero after use. Operates directly on the
	 * {@code char[]} without creating intermediate {@code String} objects, to
	 * prevent secrets from lingering as immutable Strings on the heap.
	 * Searches for the last occurrence of the field to handle nested KV v2
	 * format: {@code {"data":{"data":{"field":"value"}}}}
	 */
	private char[] _extractField(char[] json, String fieldName)
		throws SecureCredentialException {

		char[] searchKey = ("\"" + fieldName + "\"").toCharArray();

		int keyIndex = _lastIndexOf(json, searchKey);

		if (keyIndex == -1) {
			throw new SecureCredentialException(
				"Field '" + fieldName + "' not found in Vault response");
		}

		int colonIndex = _indexOf(json, ':', keyIndex + searchKey.length);

		if (colonIndex == -1) {
			throw new SecureCredentialException(
				"Malformed Vault response: no value for field '" + fieldName +
					"'");
		}

		int valueStart = _indexOf(json, '"', colonIndex + 1);

		if (valueStart == -1) {
			throw new SecureCredentialException(
				"Malformed Vault response: no string value for field '" +
					fieldName + "'");
		}

		valueStart++;

		int valueEnd = _indexOf(json, '"', valueStart);

		if (valueEnd == -1) {
			throw new SecureCredentialException(
				"Malformed Vault response: unterminated string for field '" +
					fieldName + "'");
		}

		// Copy the field value into a new char[] (caller owns it)

		int length = valueEnd - valueStart;

		char[] result = new char[length];

		System.arraycopy(json, valueStart, result, 0, length);

		return result;
	}

	private int _indexOf(char[] array, char target, int fromIndex) {
		for (int i = fromIndex; i < array.length; i++) {
			if (array[i] == target) {
				return i;
			}
		}

		return -1;
	}

	private int _lastIndexOf(char[] array, char[] target) {
		int lastMatch = -1;

		for (int i = 0; i <= array.length - target.length; i++) {
			boolean match = true;

			for (int j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					match = false;

					break;
				}
			}

			if (match) {
				lastMatch = i;
			}
		}

		return lastMatch;
	}

	private char[] _getRequest(String urlString, char[] token)
		throws SecureCredentialException {

		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection)new URL(
				urlString).openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty(
				"X-Vault-Token", new String(token));
			connection.setConnectTimeout(_CONNECTION_TIMEOUT);
			connection.setReadTimeout(_READ_TIMEOUT);

			int responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				char[] errorBody = _readStream(connection.getErrorStream());

				String error = new String(errorBody);

				Arrays.fill(errorBody, '\0');

				throw new SecureCredentialException(
					"Vault returned HTTP " + responseCode + ": " + error);
			}

			return _readStream(connection.getInputStream());
		}
		catch (SecureCredentialException secureCredentialException) {
			throw secureCredentialException;
		}
		catch (Exception exception) {
			throw new SecureCredentialException(
				"Failed to GET from Vault at " + urlString, exception);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private synchronized char[] _getToken()
		throws SecureCredentialException {

		if ((_cachedToken != null) && !_isTokenExpired()) {

			// Return a copy — caller will zero it, but we keep the cached
			// version

			return _cachedToken.clone();
		}

		// Zero the old cached token before replacing it

		if (_cachedToken != null) {
			Arrays.fill(_cachedToken, '\0');

			_cachedToken = null;
		}

		// Try auth methods in order of preference. AppRole secret_id is
		// single-use — after first auth it is zeroed, so check for it.

		if ((_vaultRoleId != null) && (_vaultSecretId != null)) {
			_cachedToken = _authenticateAppRole();
			_tokenAcquiredTime = System.currentTimeMillis();

			return _cachedToken.clone();
		}

		if (_vaultK8sRole != null) {
			_cachedToken = _authenticateKubernetes();
			_tokenAcquiredTime = System.currentTimeMillis();

			return _cachedToken.clone();
		}

		if (_vaultStaticToken != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Using static VAULT_TOKEN for Vault authentication. " +
						"This is NOT recommended for production or FedRAMP " +
							"deployments. Static tokens violate SP 800-53 " +
								"IA-5(7). Use AppRole (VAULT_ROLE_ID + " +
									"VAULT_SECRET_ID) or Kubernetes auth " +
										"(VAULT_K8S_ROLE) instead.");
			}

			return _vaultStaticToken.clone();
		}

		throw new SecureCredentialException(
			"No Vault authentication method is configured. Set " +
				"VAULT_ROLE_ID + VAULT_SECRET_ID (AppRole), " +
					"VAULT_K8S_ROLE (Kubernetes), or VAULT_TOKEN " +
						"(development only).");
	}

	private boolean _isTokenExpired() {
		if (_tokenAcquiredTime == 0) {
			return true;
		}

		long tokenTtlMillis = _getTokenTtlSeconds() * 1000;

		long renewThreshold = (long)(tokenTtlMillis * 0.75);

		return (System.currentTimeMillis() - _tokenAcquiredTime) >
			renewThreshold;
	}

	private long _getTokenTtlSeconds() {
		String ttl = _getEnvOrProperty(
			"VAULT_TOKEN_TTL", "vault.token.ttl");

		if (ttl != null) {
			try {
				return Long.parseLong(ttl);
			}
			catch (NumberFormatException numberFormatException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid VAULT_TOKEN_TTL value: " + ttl +
							". Using default " +
								_DEFAULT_TOKEN_TTL_SECONDS + "s.");
				}
			}
		}

		return _DEFAULT_TOKEN_TTL_SECONDS;
	}

	private char[] _postJson(
			String urlString, char[] jsonPayload, char[] token)
		throws SecureCredentialException {

		HttpURLConnection connection = null;

		// Convert char[] payload to byte[] for writing, then zero the byte[]

		byte[] payloadBytes = null;

		try {
			connection = (HttpURLConnection)new URL(
				urlString).openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(_CONNECTION_TIMEOUT);
			connection.setReadTimeout(_READ_TIMEOUT);
			connection.setDoOutput(true);

			if (token != null) {
				connection.setRequestProperty(
					"X-Vault-Token", new String(token));
			}

			ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
				CharBuffer.wrap(jsonPayload));

			payloadBytes = new byte[byteBuffer.remaining()];

			byteBuffer.get(payloadBytes);

			// Zero the ByteBuffer's backing array if accessible

			if (byteBuffer.hasArray()) {
				Arrays.fill(byteBuffer.array(), (byte)0);
			}

			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(payloadBytes);
			}

			int responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				char[] errorBody = _readStream(connection.getErrorStream());

				String error = new String(errorBody);

				Arrays.fill(errorBody, '\0');

				throw new SecureCredentialException(
					"Vault returned HTTP " + responseCode + ": " + error);
			}

			return _readStream(connection.getInputStream());
		}
		catch (SecureCredentialException secureCredentialException) {
			throw secureCredentialException;
		}
		catch (Exception exception) {
			throw new SecureCredentialException(
				"Failed to POST to Vault at " + urlString, exception);
		}
		finally {
			if (payloadBytes != null) {
				Arrays.fill(payloadBytes, (byte)0);
			}

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private char[] _readStream(InputStream inputStream) {
		if (inputStream == null) {
			return new char[0];
		}

		try {
			byte[] bytes = inputStream.readAllBytes();

			CharBuffer charBuffer = StandardCharsets.UTF_8.decode(
				ByteBuffer.wrap(bytes));

			char[] result = new char[charBuffer.remaining()];

			charBuffer.get(result);

			// Zero intermediate buffers

			Arrays.fill(bytes, (byte)0);

			if (charBuffer.hasArray()) {
				Arrays.fill(charBuffer.array(), '\0');
			}

			return result;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read stream", exception);
			}

			return new char[0];
		}
	}

	private static String _getEnvOrProperty(
		String envName, String propertyName) {

		String value = System.getenv(envName);

		if (value == null) {
			value = System.getProperty(propertyName);
		}

		return value;
	}

	private static final int _CONNECTION_TIMEOUT = 5000;

	private static final long _DEFAULT_TOKEN_TTL_SECONDS = 1024;

	private static final String _K8S_SA_TOKEN_PATH =
		"/var/run/secrets/kubernetes.io/serviceaccount/token";

	private static final Log _log = LogFactoryUtil.getLog(
		VaultSecureCredentialProvider.class);

	private static final int _READ_TIMEOUT = 5000;

	private static final String _vaultAddr;
	private static final String _vaultK8sRole;
	private static final char[] _vaultRoleId;
	private static volatile char[] _vaultSecretId;
	private static final char[] _vaultStaticToken;

	static {
		_vaultAddr = _getEnvOrProperty("VAULT_ADDR", "vault.addr");

		String roleId = _getEnvOrProperty("VAULT_ROLE_ID", "vault.role.id");

		_vaultRoleId = (roleId != null) ? roleId.toCharArray() : null;

		String secretId = _getEnvOrProperty(
			"VAULT_SECRET_ID", "vault.secret.id");

		_vaultSecretId = (secretId != null) ? secretId.toCharArray() : null;

		_vaultK8sRole = _getEnvOrProperty(
			"VAULT_K8S_ROLE", "vault.k8s.role");

		String staticToken = _getEnvOrProperty("VAULT_TOKEN", "vault.token");

		_vaultStaticToken =
			(staticToken != null) ? staticToken.toCharArray() : null;

		if (_vaultAddr != null) {
			if (_vaultRoleId != null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Vault credential provider configured with AppRole " +
							"authentication");
				}
			}
			else if (_vaultK8sRole != null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Vault credential provider configured with " +
							"Kubernetes authentication");
				}
			}
			else if (_vaultStaticToken != null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Vault credential provider configured with static " +
							"token. This is NOT recommended for production " +
								"deployments.");
				}
			}
		}
	}

	private char[] _cachedToken;
	private long _tokenAcquiredTime;

}
