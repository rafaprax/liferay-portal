/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.charon.integration.internal.jaxrs.application;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.scim.user.SCIMUser;
import com.liferay.scim.user.manager.SCIMUserManager;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.SCIM.Application)",
		"osgi.jaxrs.resource=true"
	},
	service = UserResource.class
)
public class UserResourceImpl implements UserResource {

	@Override
	public Response createUser(
		String attribute, String excludedAttributes, String resourceString) {

		return _buildResponse(
			_userResourceManager.create(
				resourceString, _userManager, attribute, excludedAttributes));
	}

	@Override
	public Response deleteUser(String id) {
		return _buildResponse(_userResourceManager.delete(id, _userManager));
	}

	@Override
	public Response getUser(
		String id, String attribute, String excludedAttributes) {

		return _buildResponse(
			_userResourceManager.get(
				id, _userManager, attribute, excludedAttributes));
	}

	@Override
	public Response getUser(
		String attribute, String excludedAttributes, String filter,
		int startIndex, int count, String sortBy, String sortOrder,
		String domainName) {

		return _buildResponse(
			_userResourceManager.listWithGET(
				_userManager, filter, startIndex, count, sortBy, sortOrder,
				domainName, attribute, excludedAttributes));
	}

	@Override
	public Response getUsersByPost(String resourceString) {
		return _buildResponse(
			_userResourceManager.listWithPOST(resourceString, _userManager));
	}

	@Override
	public Response updateUser(
		String id, String attribute, String excludedAttributes,
		String resourceString) {

		SCIMUser scimUser = _scimUserManager.fetchUser(
			CompanyThreadLocal.getCompanyId(), Long.parseLong(id));

		if (scimUser != null) {
			return _buildResponse(
				_userResourceManager.updateWithPUT(
					id, resourceString, _userManager, attribute,
					excludedAttributes));
		}

		return createUser(attribute, excludedAttributes, resourceString);
	}

	private Response _buildResponse(SCIMResponse scimResponse) {
		Response.ResponseBuilder responseBuilder = Response.status(
			scimResponse.getResponseStatus());

		Map<String, String> httpHeaders = scimResponse.getHeaderParamMap();

		if (MapUtil.isNotEmpty(httpHeaders)) {
			for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
				responseBuilder.header(entry.getKey(), entry.getValue());
			}
		}

		if (scimResponse.getResponseMessage() != null) {
			responseBuilder.entity(scimResponse.getResponseMessage());
		}

		return responseBuilder.build();
	}

	@Reference
	private SCIMUserManager _scimUserManager;

	@Reference
	private UserManager _userManager;

	private final UserResourceManager _userResourceManager =
		new UserResourceManager();

}