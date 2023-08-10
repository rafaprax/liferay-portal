/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration.admin.service;

import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.PublicKeyUse;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	property = Constants.SERVICE_PID + "=com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration",
	service = ManagedServiceFactory.class
)
public class OAuth2InAssertionManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		Dictionary<String, ?> properties = _configurationPidsProperties.remove(
			pid);

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		if (companyId == CompanyConstants.SYSTEM) {
			_rebuild();
		}
		else {
			_rebuild(companyId);
		}
	}

	@Override
	public String getName() {
		return StringPool.BLANK;
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties)
		throws ConfigurationException {

		Dictionary<String, ?> oldProperties = _configurationPidsProperties.put(
			pid, properties);

		long companyId = GetterUtil.getLong(
			properties.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId == CompanyConstants.SYSTEM) {
			_rebuild();

			return;
		}

		if (oldProperties != null) {
			long oldCompanyId = GetterUtil.getLong(
				oldProperties.get("companyId"));

			if (oldCompanyId == CompanyConstants.SYSTEM) {
				_rebuild();

				return;
			}

			if (oldCompanyId != companyId) {
				_rebuild(oldCompanyId);
			}
		}

		_rebuild(companyId);
	}



	private <U, V> void _addDefaults(Map<U, V> map, Map<U, V> defaultsMap) {
		if (defaultsMap != null) {
			defaultsMap.forEach(map::putIfAbsent);
		}
	}

	private void _rebuild() {
		_rebuild(CompanyConstants.SYSTEM);

		for (Long key : OAuth2ConfigurationUtil.getJWSSignatureVerifierCompanyIds()) {
			if (key == CompanyConstants.SYSTEM) {
				continue;
			}

			_rebuild(key);
		}
	}

	private void _rebuild(long companyId) {
		Map<String, Map<String, JwsSignatureVerifier>> jwsSignatureVerifiers =
			new HashMap<>();
		Map<String, String> userAuthTypes = new HashMap<>();

		for (Dictionary<String, ?> properties :
				_configurationPidsProperties.values()) {

			if (companyId != GetterUtil.getLong(properties.get("companyId"))) {
				continue;
			}

			OAuth2InAssertionConfiguration oAuth2InAssertionConfiguration =
				ConfigurableUtil.createConfigurable(
					OAuth2InAssertionConfiguration.class, properties);

			String issuer = oAuth2InAssertionConfiguration.issuer();

			if (jwsSignatureVerifiers.containsKey(issuer)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Duplicate issuer name ", issuer, " will be ",
							"discarded. Check your OAuth configuration."));
				}

				continue;
			}

			jwsSignatureVerifiers.put(issuer, new HashMap<>());

			userAuthTypes.put(
				issuer, oAuth2InAssertionConfiguration.userAuthType());

			Map<String, JwsSignatureVerifier> kidsJWSSignatureVerifiers =
				jwsSignatureVerifiers.get(issuer);

			JsonWebKeys jsonWebKeys = JwkUtils.readJwkSet(
				oAuth2InAssertionConfiguration.signatureJSONWebKeySet());

			for (JsonWebKey jsonWebKey : jsonWebKeys.getKeys()) {
				PublicKeyUse publicKeyUse = jsonWebKey.getPublicKeyUse();

				if ((publicKeyUse != null) &&
					(publicKeyUse.compareTo(PublicKeyUse.ENCRYPT) == 0)) {

					if (_log.isInfoEnabled()) {
						_log.info("Encryption key " + jsonWebKey.getKeyId());
					}

					continue;
				}

				if (kidsJWSSignatureVerifiers.containsKey(
						jsonWebKey.getKeyId())) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Duplicate assertion signature key ",
								jsonWebKey.getKeyId(),
								" will be discarded. Check your OAuth ",
								"configuration."));
					}

					continue;
				}

				kidsJWSSignatureVerifiers.put(
					jsonWebKey.getKeyId(),
					JwsUtils.getSignatureVerifier(jsonWebKey));
			}
		}

		if (companyId != CompanyConstants.SYSTEM) {
			_addDefaults(
				jwsSignatureVerifiers,
				OAuth2ConfigurationUtil.getJWSSignatureVerifiers(CompanyConstants.SYSTEM));
			_addDefaults(
				userAuthTypes, OAuth2ConfigurationUtil.getUserAuthTypes(CompanyConstants.SYSTEM));
		}

		OAuth2ConfigurationUtil.addJWSSignatureVerifiers(companyId, jwsSignatureVerifiers);
		OAuth2ConfigurationUtil.addUserAuthTypes(companyId, userAuthTypes);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2InAssertionManagedServiceFactory.class);

	private final Map<String, Dictionary<String, ?>>
		_configurationPidsProperties = Collections.synchronizedMap(
			new LinkedHashMap<>());
}