/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Discovers and manages {@link SecureCredentialProvider} implementations via
 * {@link ServiceLoader}. Provides a single entry point for resolving
 * credentials in FIPS mode.
 *
 * <p>
 * <b>Zeroization:</b> All methods returning {@code char[]} transfer ownership
 * to the caller. The caller must zero the array after use via
 * {@code Arrays.fill(credential, '\0')} per FIPS 140-3 §7.7.3.
 * </p>
 *
 * @author Liferay
 */
public class SecureCredentialProviderFactory {

	/**
	 * Returns the first available {@link SecureCredentialProvider}, or
	 * {@code null} if none is configured.
	 *
	 * @return an available provider, or {@code null}
	 */
	public static SecureCredentialProvider getProvider() {
		for (SecureCredentialProvider provider : _providers) {
			if (provider.isAvailable()) {
				return provider;
			}
		}

		return null;
	}

	/**
	 * Returns all registered {@link SecureCredentialProvider} instances,
	 * regardless of availability.
	 *
	 * @return list of registered providers
	 */
	public static List<SecureCredentialProvider> getProviders() {
		return new ArrayList<>(_providers);
	}

	/**
	 * Resolves a credential by querying all available providers in order.
	 * Returns the first non-null result as a {@code char[]} that the caller
	 * must zero after use.
	 *
	 * @param key the credential identifier
	 * @return the resolved credential as a mutable char array, or {@code null}
	 *         if no provider can resolve it
	 * @throws SecureCredentialException if a provider fails during resolution
	 */
	public static char[] resolveCredential(String key)
		throws SecureCredentialException {

		for (SecureCredentialProvider provider : _providers) {
			if (!provider.isAvailable()) {
				continue;
			}

			char[] credential = provider.getCredential(key);

			if (credential != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Credential for " + key + " resolved by provider: " +
							provider.getName());
				}

				return credential;
			}
		}

		return null;
	}

	private SecureCredentialProviderFactory() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecureCredentialProviderFactory.class);

	private static final List<SecureCredentialProvider> _providers;

	static {
		_providers = new ArrayList<>();

		ServiceLoader<SecureCredentialProvider> serviceLoader =
			ServiceLoader.load(SecureCredentialProvider.class);

		for (SecureCredentialProvider provider : serviceLoader) {
			_providers.add(provider);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Registered secure credential provider: " +
						provider.getName() + " (available=" +
							provider.isAvailable() + ")");
			}
		}
	}

}
