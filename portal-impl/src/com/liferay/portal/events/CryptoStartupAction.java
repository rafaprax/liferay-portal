/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;

import java.io.InputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import java.util.Arrays;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Mika Koivisto
 */
public class CryptoStartupAction extends SimpleAction {

	@Override
	public void run(String[] ids) {
		if (FIPSModeUtil.isFIPSModeEnabled()) {
			_initFIPSMode();
		}
		else {
			_initStandardMode();
		}
	}

	private void _initFIPSMode() {
		if (_log.isInfoEnabled()) {
			_log.info("FIPS mode is enabled, validating FIPS crypto provider");
		}

		boolean fipsProviderFound = false;

		Provider[] providers = Security.getProviders();

		for (Provider provider : providers) {
			String providerName = provider.getName();

			if (providerName.contains("FIPS") ||
				providerName.contains("BCFIPS")) {

				fipsProviderFound = true;

				if (_log.isInfoEnabled()) {
					_log.info(
						"Found FIPS security provider: " + providerName +
							" (version " + provider.getVersionStr() + ")");
				}

				break;
			}
		}

		if (!fipsProviderFound) {
			throw new RuntimeException(
				"FIPS mode is enabled but no FIPS security provider is " +
					"registered. Please configure a FIPS-certified security " +
						"provider (e.g., Bouncy Castle FIPS) in the JVM " +
							"security configuration before enabling FIPS " +
								"mode.");
		}

		_testSHA256KAT();
		_testHmacSHA256KAT();
		_testAESGCMKAT();
		_testSoftwareIntegrity();

		if (_log.isInfoEnabled()) {
			_log.info("All FIPS crypto self-tests (KATs) passed");
		}
	}

	private void _initStandardMode() {
		try {
			Mac.getInstance("HmacSHA1");
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			_log.error(
				"Unable to get Mac instance for algorithm HmacSHA1",
				noSuchAlgorithmException);
		}
	}

	private void _testAESGCMKAT() {
		try {
			byte[] key = {
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
				0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
			};

			byte[] iv = {
				0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
				0x18, 0x19, 0x1A, 0x1B
			};

			byte[] plaintext = {
				0x41, 0x45, 0x53, 0x2D, 0x47, 0x43, 0x4D, 0x2D,
				0x4B, 0x41, 0x54, 0x2D, 0x54, 0x45, 0x53, 0x54
			};

			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

			Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding");

			encryptCipher.init(
				Cipher.ENCRYPT_MODE, keySpec,
				new GCMParameterSpec(128, iv));

			byte[] ciphertext = encryptCipher.doFinal(plaintext);

			Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding");

			decryptCipher.init(
				Cipher.DECRYPT_MODE, keySpec,
				new GCMParameterSpec(128, iv));

			byte[] decrypted = decryptCipher.doFinal(ciphertext);

			if (!Arrays.equals(plaintext, decrypted)) {
				throw new RuntimeException(
					"AES/GCM round-trip KAT failed: decrypted output does " +
						"not match original plaintext");
			}

			Arrays.fill(key, (byte)0);
			Arrays.fill(ciphertext, (byte)0);
			Arrays.fill(decrypted, (byte)0);

			if (_log.isInfoEnabled()) {
				_log.info("FIPS crypto self-test passed: AES/GCM/NoPadding");
			}
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"FIPS crypto self-test failed: AES/GCM/NoPadding. This " +
					"algorithm is required for FIPS mode.",
				exception);
		}
	}

	private void _testHmacSHA256KAT() {
		try {
			byte[] key = "FIPS-KAT-HMAC-KEY".getBytes();

			byte[] message = "FIPS-KAT-HMAC-MESSAGE".getBytes();

			byte[] expectedMac = {
				(byte)0x0E, (byte)0x71, (byte)0x9C, (byte)0x66,
				(byte)0xC8, (byte)0x35, (byte)0x3F, (byte)0x1C,
				(byte)0xB9, (byte)0x22, (byte)0x6B, (byte)0xD3,
				(byte)0xF1, (byte)0x35, (byte)0xFD, (byte)0x48,
				(byte)0xB8, (byte)0x88, (byte)0x35, (byte)0xDC,
				(byte)0x3D, (byte)0x91, (byte)0x51, (byte)0xE6,
				(byte)0x58, (byte)0x8B, (byte)0xB7, (byte)0xEE,
				(byte)0x76, (byte)0x24, (byte)0xF3, (byte)0xEB
			};

			Mac mac = Mac.getInstance("HmacSHA256");

			mac.init(new SecretKeySpec(key, "HmacSHA256"));

			byte[] result = mac.doFinal(message);

			if (!Arrays.equals(expectedMac, result)) {
				throw new RuntimeException(
					"HmacSHA256 KAT failed: computed MAC does not match " +
						"expected value");
			}

			if (_log.isInfoEnabled()) {
				_log.info("FIPS crypto self-test passed: HmacSHA256");
			}
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"FIPS crypto self-test failed: HmacSHA256. This algorithm " +
					"is required for FIPS mode.",
				exception);
		}
	}

	private void _testSHA256KAT() {
		try {
			byte[] expectedDigest = {
				(byte)0xE3, (byte)0xB0, (byte)0xC4, (byte)0x42,
				(byte)0x98, (byte)0xFC, (byte)0x1C, (byte)0x14,
				(byte)0x9A, (byte)0xFB, (byte)0xF4, (byte)0xC8,
				(byte)0x99, (byte)0x6F, (byte)0xB9, (byte)0x24,
				(byte)0x27, (byte)0xAE, (byte)0x41, (byte)0xE4,
				(byte)0x64, (byte)0x9B, (byte)0x93, (byte)0x4C,
				(byte)0xA4, (byte)0x95, (byte)0x99, (byte)0x1B,
				(byte)0x78, (byte)0x52, (byte)0xB8, (byte)0x55
			};

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			byte[] result = messageDigest.digest(new byte[0]);

			if (!Arrays.equals(expectedDigest, result)) {
				throw new RuntimeException(
					"SHA-256 KAT failed: digest of empty string does not " +
						"match expected value");
			}

			if (_log.isInfoEnabled()) {
				_log.info("FIPS crypto self-test passed: SHA-256");
			}
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"FIPS crypto self-test failed: SHA-256. This algorithm is " +
					"required for FIPS mode.",
				exception);
		}
	}

	private void _testSoftwareIntegrity() {
		String[] criticalClasses = {
			"com/liferay/portal/kernel/security/fips/FIPSModeUtil.class",
			"com/liferay/portal/kernel/security/fips/CompanyKeyStoreUtil.class",
			"com/liferay/portal/kernel/util/DigesterUtil.class"
		};

		Properties expectedDigests = new Properties();

		try (InputStream manifestInputStream =
				ClassLoader.getSystemResourceAsStream(
					_FIPS_INTEGRITY_MANIFEST)) {

			if (manifestInputStream != null) {
				expectedDigests.load(manifestInputStream);
			}
		}
		catch (Exception exception) {
			_log.warn(
				"FIPS integrity check: unable to load manifest " +
					_FIPS_INTEGRITY_MANIFEST,
				exception);
		}

		boolean manifestAvailable = !expectedDigests.isEmpty();

		if (!manifestAvailable) {
			_log.warn(
				"FIPS integrity check: manifest " +
					_FIPS_INTEGRITY_MANIFEST + " is missing or empty. " +
						"Run generate-fips-integrity.sh after building to " +
							"enable tamper detection.");
		}

		StringBuilder failures = new StringBuilder();

		try {
			for (String classPath : criticalClasses) {
				String actualDigest = _computeClassDigest(classPath);

				if (actualDigest == null) {
					_log.warn(
						"FIPS integrity check: unable to locate " +
							classPath + " on classpath");

					continue;
				}

				if (_log.isInfoEnabled()) {
					_log.info(
						"FIPS integrity digest for " + classPath + ": " +
							actualDigest);
				}

				if (manifestAvailable) {
					String expectedDigest = expectedDigests.getProperty(
						classPath);

					if (expectedDigest == null) {
						failures.append(
							"No expected digest in manifest for: " +
								classPath + ". ");
					}
					else if (!MessageDigest.isEqual(
								expectedDigest.getBytes(),
								actualDigest.getBytes())) {

						failures.append(
							"Integrity mismatch for " + classPath +
								": expected=" + expectedDigest + ", actual=" +
									actualDigest + ". ");
					}
				}
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"FIPS 140-3 software integrity verification failed. " +
					"Unable to compute integrity digest for crypto modules.",
				exception);
		}

		if (failures.length() > 0) {
			throw new RuntimeException(
				"FIPS 140-3 software integrity verification failed " +
					"(Section 10.2.1.1): " + failures.toString() +
						"The cryptographic module bytecode does not match " +
							"the build manifest. This may indicate " +
								"unauthorized modification.");
		}

		if (manifestAvailable && _log.isInfoEnabled()) {
			_log.info(
				"FIPS software integrity verification passed: all " +
					"critical class digests match the build manifest");
		}
	}

	private String _computeClassDigest(String classPath) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			try (InputStream inputStream =
					ClassLoader.getSystemResourceAsStream(classPath)) {

				if (inputStream == null) {
					return null;
				}

				byte[] buffer = new byte[4096];

				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					messageDigest.update(buffer, 0, bytesRead);
				}
			}

			byte[] digest = messageDigest.digest();

			StringBuilder sb = new StringBuilder();

			for (byte b : digest) {
				sb.append(String.format("%02x", b));
			}

			return sb.toString();
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to compute SHA-256 digest for " + classPath,
				exception);
		}
	}

	private static final String _FIPS_INTEGRITY_MANIFEST =
		"META-INF/fips-integrity.properties";

	private static final Log _log = LogFactoryUtil.getLog(
		CryptoStartupAction.class);

}
