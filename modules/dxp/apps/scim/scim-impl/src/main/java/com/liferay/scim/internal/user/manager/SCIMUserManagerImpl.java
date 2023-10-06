/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.internal.user.manager;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.scim.configuration.SCIMClientOAuth2ApplicationConfiguration;
import com.liferay.scim.user.SCIMUser;
import com.liferay.scim.user.manager.SCIMUserManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = SCIMUserManager.class)
public class SCIMUserManagerImpl implements SCIMUserManager {

	@Override
	public SCIMUser addOrUpdateUser(SCIMUser scimUser) throws PortalException {
		Company company = _companyLocalService.getCompany(
			scimUser.getCompanyId());

		SCIMClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
				_getSCIMClientOAuth2ApplicationConfiguration(
					company.getCompanyId());

		User user = _fetchUser(
			scimClientOAuth2ApplicationConfiguration, scimUser);

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(scimUser.getBirthday());

		int birthdayMonth = birthdayCal.get(Calendar.MONTH);
		int birthdayDay = birthdayCal.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCal.get(Calendar.YEAR);

		if (user == null) {
			user = _userLocalService.addUser(
				scimUser.getCreatorUserId(), scimUser.getCompanyId(), scimUser.isAutoPassword(), scimUser.getPassword(),
				scimUser.getPassword(), scimUser.isAutoScreenName(),
				scimUser.getScreenName(), scimUser.getEmailAddress(),
				scimUser.getLocale(), scimUser.getFirstName(),
				scimUser.getMiddleName(), scimUser.getLastName(),  0, 0,
				scimUser.isMale(), birthdayMonth,
				birthdayDay, birthdayYear, StringPool.BLANK,
				UserConstants.TYPE_REGULAR, scimUser.getGroupIds(),
				scimUser.getOrganizationIds(), scimUser.getRoleIds(),
				scimUser.getUserGroupIds(), scimUser.isSendEmail(),
				new ServiceContext());

			user.setExternalReferenceCode(scimUser.getExternalReferenceCode());

			user = _userLocalService.updateUser(user);

			user = _userLocalService.updateEmailAddressVerified(
				user.getUserId(), true);

			_saveSCIMClientId(
				user,
				scimClientOAuth2ApplicationConfiguration.applicationName());
		}
		else {
			String scimClientId = _getSCIMClientId(user);

			if (Validator.isNotNull(scimClientId) && !Objects.equals(scimClientId, scimClientOAuth2ApplicationConfiguration.applicationName())) {
				throw new PortalException("User is already linked to another scim client");
			}

			Contact contact = user.getContact();

			user = _userLocalService.updateUser(
				user.getUserId(), scimUser.getPassword(), StringPool.BLANK,
				StringPool.BLANK, false, user.getReminderQueryQuestion(),
				user.getReminderQueryAnswer(), user.getScreenName(),
				scimUser.getEmailAddress(), false, null, user.getLanguageId(),
				user.getTimeZoneId(), user.getGreeting(), user.getComments(),
				scimUser.getFirstName(), scimUser.getMiddleName(),
				scimUser.getLastName(), 0, 0, scimUser.isMale(), birthdayMonth,
				birthdayDay, birthdayYear, contact.getSmsSn(),
				contact.getFacebookSn(), contact.getJabberSn(),
				contact.getSkypeSn(), contact.getTwitterSn(),
				scimUser.getJobTitle(), user.getGroupIds(),
				user.getOrganizationIds(), user.getRoleIds(), null,
				user.getUserGroupIds(), new ServiceContext());

			if (!Objects.equals(user.getExternalReferenceCode(), scimUser.getExternalReferenceCode())) {
				user.setExternalReferenceCode(
					scimUser.getExternalReferenceCode());

				user = _userLocalService.updateUser(user);
			}

			if (Validator.isNull(scimClientId)) {
				_saveSCIMClientId(
					user,
					scimClientOAuth2ApplicationConfiguration.applicationName());
			}
		}

		return _toSCIMUser(user);
	}

	@Override
	public void deleteUser(SCIMUser scimUser) throws PortalException {
		User user = _fetchUser(
			_getSCIMClientOAuth2ApplicationConfiguration(
				scimUser.getCompanyId()),
			scimUser);

		_userLocalService.deleteUser(user.getUserId());
	}

	@Override
	public SCIMUser fetchUser(long companyId, long userId)  {
		User user = _userLocalService.fetchUserById(userId);

		if (user == null) {
			return null;
		}

		SCIMClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
			_getSCIMClientOAuth2ApplicationConfiguration(
				companyId);

		if(!Objects.equals(
			_getSCIMClientId(user),
			scimClientOAuth2ApplicationConfiguration.applicationName())) {

			return null;
		}

		return _toSCIMUser(user);
	}

	private SCIMUser _toSCIMUser(User user)  {
		SCIMUser scimUser = new SCIMUser();

		try {
			scimUser.setActive(user.isActive());
			scimUser.setBirthday(user.getBirthday());
			scimUser.setCompanyId(user.getCompanyId());
			scimUser.setFirstName(user.getFirstName());
			scimUser.setEmailAddress(user.getEmailAddress());
			scimUser.setExternalReferenceCode(user.getExternalReferenceCode());
			scimUser.setId(String.valueOf(user.getUserId()));
			scimUser.setJobTitle(user.getJobTitle());
			scimUser.setLastName(user.getLastName());
			scimUser.setLocale(user.getLocale());
			scimUser.setMale(user.isMale());
			scimUser.setMiddleName(user.getMiddleName());
			scimUser.setScreenName(user.getScreenName());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to convert the User to a SCIMUser model",
				portalException);
		}

		return scimUser;
	}


	private void _saveSCIMClientId(User user, String scimClientId)
		throws PortalException {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			expandoTable = _expandoTableLocalService.addTable(
				user.getCompanyId(), User.class.getName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "scimClientId");

		if (expandoColumn == null) {
			expandoColumn = _expandoColumnLocalService.addColumn(
				expandoTable.getTableId(), "scimClientId",
				ExpandoColumnConstants.STRING);
		}

		_expandoValueLocalService.addValue(
			user.getCompanyId(), User.class.getName(),
			ExpandoTableConstants.DEFAULT_TABLE_NAME, expandoColumn.getName(),
			user.getUserId(), scimClientId);
	}

	private String _getSCIMClientId(User user) {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			return StringPool.BLANK;
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "scimClientId");

		if (expandoColumn == null) {
			return StringPool.BLANK;
		}

		ExpandoValue expandoValue = _expandoValueLocalService.getValue(
			expandoTable.getTableId(), expandoColumn.getColumnId(),
			user.getUserId());

		if (expandoValue == null) {
			return StringPool.BLANK;
		}

		return expandoValue.getData();
	}

	private User _fetchUser(
		SCIMClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration,
		SCIMUser scimUser) {

		User portalUser = _userLocalService.fetchUserByExternalReferenceCode(
			scimUser.getExternalReferenceCode(), scimUser.getCompanyId());

		if (portalUser != null) {
			return portalUser;
		}

		if (Objects.equals(
				scimClientOAuth2ApplicationConfiguration.matcherField(),
				_USER_SYNC_MATCHER_FIELD_EA)) {

			return _userLocalService.fetchUserByEmailAddress(
				scimUser.getCompanyId(), scimUser.getEmailAddress());
		}
		else if (Objects.equals(
					scimClientOAuth2ApplicationConfiguration.matcherField(),
					_USER_SYNC_MATCHER_FIELD_UN)) {

			return _userLocalService.fetchUserByScreenName(
				scimUser.getCompanyId(), scimUser.getScreenName());
		}

		return null;
	}

	private SCIMClientOAuth2ApplicationConfiguration
			_getSCIMClientOAuth2ApplicationConfiguration(long companyId) {

		Configuration[] configurations = null;

		try {
			configurations = _configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s*)", Constants.SERVICE_PID,
					SCIMClientOAuth2ApplicationConfiguration.class.getName()));

			if (ArrayUtil.isEmpty(configurations)) {
				return null;
			}

			for (Configuration configuration : configurations) {
				HashMap<String, Object> properties =
					HashMapBuilder.<String, Object>putAll(
						configuration.getProperties()
					).build();

				long configurationCompanyId =
					ConfigurationFactoryUtil.getCompanyId(
						_companyLocalService, properties);

				if (configurationCompanyId == companyId) {
					return ConfigurableUtil.createConfigurable(
						SCIMClientOAuth2ApplicationConfiguration.class,
						properties);
				}
			}
		}
		catch (Exception exception) {
			_log.debug(
				"Unable to get the SCIMClientOAuth2ApplicationConfiguration " +
					"for the companyId " + companyId,
				exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SCIMUserManagerImpl.class);

	private static final String _USER_SYNC_MATCHER_FIELD_EA = "emailAddress";

	private static final String _USER_SYNC_MATCHER_FIELD_UN = "userName";

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private UserLocalService _userLocalService;

}