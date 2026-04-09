/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * Thrown when a {@link SecureCredentialProvider} is unable to retrieve a
 * credential from the external secrets store.
 *
 * @author Liferay
 */
public class SecureCredentialException extends Exception {

	public SecureCredentialException(String message) {
		super(message);
	}

	public SecureCredentialException(String message, Throwable cause) {
		super(message, cause);
	}

}
