/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.charon.integration.internal.user.manager;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.scim.charon.integration.internal.util.ModelConverterUtil;
import com.liferay.scim.user.SCIMUser;
import com.liferay.scim.user.manager.SCIMUserManager;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.GroupsGetResponse;
import org.wso2.charon3.core.objects.plainobjects.UsersGetResponse;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

/**
 * @author Rafael Praxedes
 */
@Component(service = UserManager.class)
public class UserManagerImpl implements UserManager {

	@Override
	public Group createGroup(
			Group group, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, ConflictException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User createMe(User user, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, ConflictException,
			   ForbiddenException, NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User createUser(User user, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, ConflictException,
			   ForbiddenException {

		return _addOrUpdateUser(user);
	}

	@Override
	public void deleteGroup(String id)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public void deleteMe(String userName)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public void deleteUser(String userId)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {
	}

	@Override
	public Group getGroup(String id, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User getMe(String userName, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User getUser(String id, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException {

		SCIMUser scimUser = _scimUserManager.fetchUser(
			CompanyThreadLocal.getCompanyId(), Long.parseLong(id));

		if (scimUser == null) {
			throw new NotFoundException("No user found with id : " + id);
		}

		try {
			return ModelConverterUtil.toUser(scimUser);
		}
		catch (Exception exception) {
			throw new CharonException(
				"Unable to convert SCIM User " + id, exception);
		}
	}

	@Override
	public GroupsGetResponse listGroupsWithPost(
			SearchRequest searchRequest,
			Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public UsersGetResponse listUsersWithPost(
			SearchRequest searchRequest,
			Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotImplementedException {

		return null;
	}

	@Override
	public Group updateGroup(
			Group oldGroup, Group newGroup,
			Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User updateMe(
			User updatedUser, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User updateUser(User user, Map<String, Boolean> requiredAttributes)
		throws BadRequestException, CharonException, NotFoundException,
			   NotImplementedException {

		return _addOrUpdateUser(user);
	}

	private User _addOrUpdateUser(User user) throws CharonException {
		try {
			Company company = _companyLocalService.getCompany(
				CompanyThreadLocal.getCompanyId());

			SCIMUser scimUser = _scimUserManager.addOrUpdateUser(
				ModelConverterUtil.toSCIMUser(
					company.getCompanyId(), company.getLocale(), user));

			return ModelConverterUtil.toUser(scimUser);
		}
		catch (Exception exception) {
			throw new CharonException(
				"Unable to provisioning an portal user for " +
					user.getDisplayName(),
				exception);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private SCIMUserManager _scimUserManager;

}