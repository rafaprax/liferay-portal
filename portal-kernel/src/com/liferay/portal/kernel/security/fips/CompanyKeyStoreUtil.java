/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Key;
import java.security.KeyStore;

import java.util.Arrays;

/**
 * Manages the FIPS-compliant KeyStore for company encryption keys. In FIPS
 * mode, company keys are stored in a PKCS12 KeyStore file on the filesystem
 * instead of as raw Base64 strings in the database.
 *
 * @author Liferay
 */
public class CompanyKeyStoreUtil {

	public static final String ALIAS_PREFIX = "company-key-";

	public static boolean containsAlias(String alias) {
		try {
			KeyStore keyStore = _getKeyStore();

			return keyStore.containsAlias(alias);
		}
		catch (Exception exception) {
			_log.error("Unable to check alias in company KeyStore", exception);

			return false;
		}
	}

	public static String generateAlias(long companyId) {
		return ALIAS_PREFIX + companyId;
	}

	public static Key getKey(String alias) {
		char[] password = null;

		try {
			KeyStore keyStore = _getKeyStore();

			password = _getKeyStorePassword();

			return keyStore.getKey(alias, password);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to retrieve key from company KeyStore for alias: " +
					alias,
				exception);

			return null;
		}
		finally {
			if (password != null) {
				Arrays.fill(password, '\0');
			}
		}
	}

	public static boolean isKeyStoreAlias(String value) {
		return (value != null) && value.startsWith(ALIAS_PREFIX);
	}

	public static void removeKey(String alias) {
		try {
			KeyStore keyStore = _getKeyStore();

			if (keyStore.containsAlias(alias)) {
				keyStore.deleteEntry(alias);

				_saveKeyStore(keyStore);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Removed key from company KeyStore with alias: " +
							alias);
				}
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to remove key from company KeyStore for alias: " +
					alias,
				exception);
		}
	}

	public static void setKey(String alias, Key key) {
		char[] password = null;

		try {
			KeyStore keyStore = _getKeyStore();

			password = _getKeyStorePassword();

			keyStore.setKeyEntry(alias, key, password, null);

			_saveKeyStore(keyStore);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Stored company encryption key in KeyStore with alias: " +
						alias);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to store key in company KeyStore for alias: " + alias,
				exception);
		}
		finally {
			if (password != null) {
				Arrays.fill(password, '\0');
			}
		}
	}

	private static KeyStore _getKeyStore() throws Exception {
		if (_keyStore != null) {
			return _keyStore;
		}

		synchronized (CompanyKeyStoreUtil.class) {
			if (_keyStore != null) {
				return _keyStore;
			}

			String keyStoreType = GetterUtil.getString(
				PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEYSTORE_TYPE),
				"PKCS12");

			KeyStore keyStore = KeyStore.getInstance(keyStoreType);

			File keyStoreFile = _getKeyStoreFile();

			if (keyStoreFile.exists()) {
				char[] password = _getKeyStorePassword();

				try (FileInputStream fileInputStream =
						new FileInputStream(keyStoreFile)) {

					keyStore.load(fileInputStream, password);
				}
				finally {
					Arrays.fill(password, '\0');
				}
			}
			else {
				keyStore.load(null, null);

				_saveKeyStore(keyStore);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Created new company KeyStore at: " +
							keyStoreFile.getAbsolutePath());
				}
			}

			_keyStore = keyStore;

			return _keyStore;
		}
	}

	private static File _getKeyStoreFile() {
		String path = GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEYSTORE_PATH));

		if (Validator.isNull(path)) {
			String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

			path = liferayHome + "/data/company-keystore.p12";
		}
		else {
			String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

			path = path.replace("${liferay.home}", liferayHome);
		}

		return new File(path);
	}

	private static char[] _getKeyStorePassword() {
		String password = GetterUtil.getString(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_KEYSTORE_PASSWORD));

		if (Validator.isNull(password) ||
			password.equals(_DEFAULT_KEYSTORE_PASSWORD)) {

			if (FIPSModeUtil.isFIPSModeEnabled()) {
				throw new IllegalStateException(
					"FIPS mode requires an explicit " +
						"company.encryption.keystore.password to be set in " +
							"portal-ext.properties. The default password is " +
								"not allowed in FIPS mode.");
			}

			return _DEFAULT_KEYSTORE_PASSWORD.toCharArray();
		}

		return password.toCharArray();
	}

	private static void _saveKeyStore(KeyStore keyStore) throws Exception {
		File keyStoreFile = _getKeyStoreFile();

		File parentDir = keyStoreFile.getParentFile();

		if ((parentDir != null) && !parentDir.exists()) {
			parentDir.mkdirs();
		}

		char[] password = _getKeyStorePassword();

		try (FileOutputStream fileOutputStream =
				new FileOutputStream(keyStoreFile)) {

			keyStore.store(fileOutputStream, password);
		}
		finally {
			Arrays.fill(password, '\0');
		}

		if (FIPSModeUtil.isFIPSModeEnabled()) {
			keyStoreFile.setReadable(false, false);
			keyStoreFile.setReadable(true, true);
			keyStoreFile.setWritable(false, false);
			keyStoreFile.setWritable(true, true);
		}
	}

	private static final String _DEFAULT_KEYSTORE_PASSWORD =
		"changeit-company-keystore";

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyKeyStoreUtil.class);

	private static volatile KeyStore _keyStore;

}
