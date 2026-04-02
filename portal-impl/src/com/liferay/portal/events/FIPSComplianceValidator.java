/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates that all portal cryptographic configuration is FIPS 140-2/140-3
 * compliant when FIPS mode is enabled. Runs during global startup and fails
 * fast with clear error messages if any misconfiguration is detected.
 *
 * @author Liferay
 */
public class FIPSComplianceValidator extends SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		if (!FIPSModeUtil.isFIPSModeEnabled()) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"FIPS mode is enabled. Validating cryptographic " +
					"configuration...");
		}

		List<String> violations = new ArrayList<>();

		_validateCompanyEncryptionAlgorithm(violations);
		_validateCompanyEncryptionKeySize(violations);
		_validateCompanyKeyStore(violations);
		_validatePasswordEncryptionAlgorithm(violations);
		_validateAuthMacAlgorithm(violations);
		_validateSamlKeyStoreType(violations);

		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();

			sb.append(
				"FIPS compliance validation failed. The following " +
					"configuration issues were detected:\n");

			for (int i = 0; i < violations.size(); i++) {
				sb.append("\n  ");
				sb.append(i + 1);
				sb.append(". ");
				sb.append(violations.get(i));
			}

			sb.append(
				"\n\nPlease update your portal-ext.properties to use " +
					"FIPS-approved values or disable FIPS mode by setting " +
						"portal.security.fips.mode.enabled=false");

			_log.error(sb.toString());

			throw new ActionException(sb.toString());
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"FIPS compliance validation passed. All cryptographic " +
					"configuration is compliant.");
		}
	}

	private void _validateAuthMacAlgorithm(List<String> violations) {
		String algorithm = GetterUtil.getString(
			PropsUtil.get(PropsKeys.AUTH_MAC_ALGORITHM));

		if (!_FIPS_APPROVED_MAC_ALGORITHMS.contains(
				algorithm.toUpperCase())) {

			violations.add(
				"auth.mac.algorithm=" + algorithm +
					" is not FIPS-approved. Use one of: " +
						_FIPS_APPROVED_MAC_ALGORITHMS);
		}
	}

	private void _validateCompanyEncryptionAlgorithm(
		List<String> violations) {

		String algorithm = GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_ALGORITHM));

		if (!_FIPS_APPROVED_ENCRYPTION_ALGORITHMS.contains(
				algorithm.toUpperCase())) {

			violations.add(
				"company.encryption.algorithm=" + algorithm +
					" is not FIPS-approved. Use one of: " +
						_FIPS_APPROVED_ENCRYPTION_ALGORITHMS);
		}
	}

	private void _validateCompanyEncryptionKeySize(List<String> violations) {
		int keySize = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEY_SIZE));

		if (!_FIPS_APPROVED_KEY_SIZES.contains(keySize)) {
			violations.add(
				"company.encryption.key.size=" + keySize +
					" is not FIPS-approved. Use one of: " +
						_FIPS_APPROVED_KEY_SIZES);
		}
	}

	private void _validateCompanyKeyStore(List<String> violations) {
		String keyStoreType = GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEYSTORE_TYPE));

		if (keyStoreType.isEmpty()) {
			violations.add(
				"company.encryption.keystore.type is not set. FIPS mode " +
					"requires a PKCS12 or BCFKS KeyStore for company key " +
						"storage.");

			return;
		}

		String upperKeyStoreType = keyStoreType.toUpperCase();

		if (!_FIPS_APPROVED_KEYSTORE_TYPES.contains(upperKeyStoreType)) {
			violations.add(
				"company.encryption.keystore.type=" + keyStoreType +
					" is not FIPS-approved. Use PKCS12 or BCFKS.");
		}
	}

	private void _validatePasswordEncryptionAlgorithm(
		List<String> violations) {

		String algorithm = GetterUtil.getString(
			PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM));

		String upperAlgorithm = algorithm.toUpperCase();

		boolean approved = false;

		for (String prefix : _FIPS_APPROVED_PASSWORD_ALGORITHM_PREFIXES) {
			if (upperAlgorithm.startsWith(prefix)) {
				approved = true;

				break;
			}
		}

		if (!approved) {
			violations.add(
				"passwords.encryption.algorithm=" + algorithm +
					" is not FIPS-approved. Use PBKDF2WithHmacSHA256, " +
						"PBKDF2WithHmacSHA384, or PBKDF2WithHmacSHA512.");
		}
	}

	private void _validateSamlKeyStoreType(List<String> violations) {
		String keyStoreType = GetterUtil.getString(
			PropsUtil.get(_SAML_KEYSTORE_TYPE_KEY));

		if (keyStoreType.isEmpty()) {
			return;
		}

		String upperKeyStoreType = keyStoreType.toUpperCase();

		if (!_FIPS_APPROVED_KEYSTORE_TYPES.contains(upperKeyStoreType)) {
			violations.add(
				"saml.keystore.type=" + keyStoreType +
					" is not FIPS-approved. Use one of: " +
						_FIPS_APPROVED_KEYSTORE_TYPES);
		}
	}

	private static final Set<String> _FIPS_APPROVED_ENCRYPTION_ALGORITHMS =
		new HashSet<>(Arrays.asList("AES"));

	private static final Set<String> _FIPS_APPROVED_KEYSTORE_TYPES =
		new HashSet<>(Arrays.asList("BCFKS", "PKCS12"));

	private static final Set<Integer> _FIPS_APPROVED_KEY_SIZES =
		new HashSet<>(Arrays.asList(128, 192, 256));

	private static final Set<String> _FIPS_APPROVED_MAC_ALGORITHMS =
		new HashSet<>(
			Arrays.asList(
				"HMACSHA256", "HMACSHA384", "HMACSHA512", "HMACSHA3-256",
				"HMACSHA3-384", "HMACSHA3-512"));

	private static final String[] _FIPS_APPROVED_PASSWORD_ALGORITHM_PREFIXES =
		{"PBKDF2WITHHMACSHA256", "PBKDF2WITHHMACSHA384",
			"PBKDF2WITHHMACSHA512"};

	private static final Log _log = LogFactoryUtil.getLog(
		FIPSComplianceValidator.class);

	private static final String _SAML_KEYSTORE_TYPE_KEY =
		"saml.keystore.type";

}
