/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.DigesterUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tomas Polesovsky
 */
@Component(
	property = "type=" + PasswordEncryptor.TYPE_DEFAULT,
	service = PasswordEncryptor.class
)
public class DefaultPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		if (FIPSModeUtil.isFIPSModeEnabled() && _log.isWarnEnabled()) {
			_log.warn(
				"Verifying legacy " + algorithm + " hash in FIPS mode. " +
					"Password will be upgraded to PBKDF2 on next " +
						"successful login.");
		}

		return DigesterUtil.digest(algorithm, plainTextPassword);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultPasswordEncryptor.class);

}