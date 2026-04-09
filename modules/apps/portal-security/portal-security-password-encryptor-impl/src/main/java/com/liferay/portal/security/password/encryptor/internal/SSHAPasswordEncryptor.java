/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	property = "type=" + PasswordEncryptor.TYPE_SSHA,
	service = PasswordEncryptor.class
)
public class SSHAPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		if (FIPSModeUtil.isFIPSModeEnabled() && _log.isWarnEnabled()) {
			_log.warn(
				"Verifying legacy SSHA hash in FIPS mode. Password will " +
					"be upgraded to PBKDF2 on next successful login.");
		}

		if (upgradeHashSecurity) {
			encryptedPassword = null;
		}

		byte[] saltBytes = getSaltBytes(encryptedPassword);

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

			byte[] plainTextPasswordBytes = plainTextPassword.getBytes(
				DigesterUtil.ENCODING);

			byte[] messageDigestBytes = messageDigest.digest(
				ArrayUtil.append(plainTextPasswordBytes, saltBytes));

			return Base64.encode(
				ArrayUtil.append(messageDigestBytes, saltBytes));
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new PwdEncryptorException.InvalidAlgorithm(
				noSuchAlgorithmException.getMessage(),
				noSuchAlgorithmException);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new PwdEncryptorException.UnsupportedEncoding(
				unsupportedEncodingException.getMessage(),
				unsupportedEncodingException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SSHAPasswordEncryptor.class);

	protected byte[] getSaltBytes(String encryptedPassword)
		throws PwdEncryptorException {

		byte[] saltBytes = new byte[8];

		if (Validator.isNull(encryptedPassword)) {
			BigEndianCodec.putLong(saltBytes, 0, SecureRandomUtil.nextLong());
		}
		else {
			try {
				byte[] encryptedPasswordBytes = Base64.decode(
					encryptedPassword);

				System.arraycopy(
					encryptedPasswordBytes, encryptedPasswordBytes.length - 8,
					saltBytes, 0, saltBytes.length);
			}
			catch (Exception exception) {
				throw new PwdEncryptorException.InvalidEncryptedPwd(
					"Unable to extract salt from encrypted password " +
						exception.getMessage(),
					exception);
			}
		}

		return saltBytes;
	}

}