/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * SPI for resolving sensitive credentials from external secrets managers
 * (e.g., HashiCorp Vault, AWS Secrets Manager) instead of reading them from
 * plaintext configuration files.
 *
 * <p>
 * Implementations are discovered via {@link java.util.ServiceLoader}. When FIPS
 * mode is enabled, {@link SecureCredentialProviderFactory} queries registered
 * providers to resolve JDBC passwords and other sensitive properties.
 * </p>
 *
 * <p>
 * <b>Zeroization contract:</b> Credentials are returned as {@code char[]} so
 * that callers can zero the array after use via
 * {@code Arrays.fill(credential, '\0')}. This satisfies FIPS 140-3 §7.7.3.
 * Implementations must not retain references to the returned array.
 * </p>
 *
 * @author Liferay
 * @see SecureCredentialProviderFactory
 */
public interface SecureCredentialProvider {

	/**
	 * Returns the credential value for the given key, or {@code null} if this
	 * provider does not manage the requested credential.
	 *
	 * <p>
	 * The caller is responsible for zeroing the returned {@code char[]} after
	 * use.
	 * </p>
	 *
	 * @param key the credential identifier (e.g., a Vault path, a Secrets
	 *        Manager ARN, or a property name like "jdbc.default.password")
	 * @return the resolved credential as a mutable char array, or {@code null}
	 * @throws SecureCredentialException if the provider manages this key but
	 *         fails to retrieve the credential
	 */
	public char[] getCredential(String key) throws SecureCredentialException;

	/**
	 * Returns the display name of this provider (e.g., "HashiCorp Vault",
	 * "AWS Secrets Manager").
	 *
	 * @return the provider name
	 */
	public String getName();

	/**
	 * Returns {@code true} if this provider is properly configured and
	 * available.
	 *
	 * @return {@code true} if the provider is available
	 */
	public boolean isAvailable();

}
