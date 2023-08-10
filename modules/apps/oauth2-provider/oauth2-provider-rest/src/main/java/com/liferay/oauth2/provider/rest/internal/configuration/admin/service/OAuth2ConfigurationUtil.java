package com.liferay.oauth2.provider.rest.internal.configuration.admin.service;

import com.liferay.portal.kernel.model.CompanyConstants;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class OAuth2ConfigurationUtil {

	public static void addUserAuthTypes(long companyId, Map<String, String> userAuthTypes) {
		_userAuthTypes.put(companyId, userAuthTypes);
	}

	public static Map<String, String>  getUserAuthTypes(long companyId) {
		return _userAuthTypes.get(companyId);
	}

	public static void addJWSSignatureVerifiers(long companyId, Map<String, Map<String, JwsSignatureVerifier>> jwsSignatureVerifiers) {
		_jwsSignatureVerifiers.put(companyId, jwsSignatureVerifiers);
	}

	public static Map<String, Map<String, JwsSignatureVerifier>> getJWSSignatureVerifiers(long companyId) {
		return _jwsSignatureVerifiers.get(companyId);
	}

	public static Set<Long> getJWSSignatureVerifierCompanyIds() {
		return _jwsSignatureVerifiers.keySet();
	}

	private static final Map<Long, Map<String, String>> _userAuthTypes =
		Collections.synchronizedMap(new LinkedHashMap<>());

	private static final Map<Long, Map<String, Map<String, JwsSignatureVerifier>>>
		_jwsSignatureVerifiers = Collections.synchronizedMap(
		new LinkedHashMap<>());


	static {
		_jwsSignatureVerifiers.put(
				CompanyConstants.SYSTEM, Collections.emptyMap());
		_userAuthTypes.put(
				CompanyConstants.SYSTEM, Collections.emptyMap());
	}
}
