/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.manager;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountWebKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
public abstract class BaseCurrentAccountEntryManagerStore {

	public AccountEntry getAccountEntryFromHttpSession(long groupId) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			return null;
		}

		long currentAccountEntryId = GetterUtil.getLong(
			httpSession.getAttribute(_getKey(groupId)));

		return accountEntryLocalService.fetchAccountEntry(
			currentAccountEntryId);
	}

	public AccountEntry getAccountEntryFromPortalPreferences(
		long groupId, long userId) {

		com.liferay.portal.kernel.model.PortalPreferences
			modelPortalPreferences =
				portalPreferencesLocalService.fetchPortalPreferences(
					userId, PortletKeys.PREFS_OWNER_TYPE_USER);

		if (modelPortalPreferences == null) {
			return null;
		}

		PortalPreferences portalPreferences =
			portalPreferenceValueLocalService.getPortalPreferences(
				modelPortalPreferences, false);

		long accountEntryId = GetterUtil.getLong(
			portalPreferences.getValue(
				AccountEntry.class.getName(), _getKey(groupId)));

		if (accountEntryId > 0) {
			return accountEntryLocalService.fetchAccountEntry(accountEntryId);
		}

		return null;
	}

	public AccountEntry getCurrentAccountEntry(long groupId, long userId)
		throws PortalException {

		AccountEntry accountEntry = getAccountEntryFromHttpSession(groupId);

		if (accountEntry == null) {
			accountEntry = getAccountEntryFromPortalPreferences(
				groupId, userId);
		}

		return accountEntry;
	}

	public void saveInHttpSession(long accountEntryId, long groupId) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			return;
		}

		httpSession.setAttribute(_getKey(groupId), accountEntryId);
	}

	public void saveInPortalPreferences(
		long accountEntryId, long groupId, long userId) {

		PortalPreferences portalPreferences = _getPortalPreferences(userId);

		String key = _getKey(groupId);

		long currentAccountEntryId = GetterUtil.getLong(
			portalPreferences.getValue(
				AccountEntry.class.getName(), key,
				String.valueOf(AccountConstants.ACCOUNT_ENTRY_ID_GUEST)));

		if (currentAccountEntryId == accountEntryId) {
			return;
		}

		portalPreferences.setValue(
			AccountEntry.class.getName(), key, String.valueOf(accountEntryId));

		portalPreferencesLocalService.updatePreferences(
			userId, PortletKeys.PREFS_OWNER_TYPE_USER, portalPreferences);
	}

	public void setCurrentAccountEntryManagerStore(
		long accountEntryId, long groupId, long userId) {

		saveInHttpSession(accountEntryId, groupId);
		saveInPortalPreferences(accountEntryId, groupId, userId);
	}

	@Reference
	protected AccountEntryLocalService accountEntryLocalService;

	@Reference
	protected PortalPreferencesLocalService portalPreferencesLocalService;

	@Reference
	protected PortalPreferenceValueLocalService
		portalPreferenceValueLocalService;

	@Reference
	protected PortletPreferencesFactory portletPreferencesFactory;

	private String _getKey(long groupId) {
		return AccountWebKeys.CURRENT_ACCOUNT_ENTRY_ID + groupId;
	}

	private PortalPreferences _getPortalPreferences(long userId) {

		// LPS-156201

		try {
			return portletPreferencesFactory.getPortalPreferences(userId, true);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCurrentAccountEntryManagerStore.class);

}