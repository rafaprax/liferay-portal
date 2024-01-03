/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryServiceUtil;
import com.liferay.portal.kernel.service.ListTypeLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Alec Sloan
 */
public class OrganizationImporterUtil {

	public static void importOrganizations(
			JSONArray jsonArray, long scopeGroupId, long userId)
		throws PortalException {

		User user = UserLocalServiceUtil.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		for (int i = 0; i < jsonArray.length(); i++) {
			_importOrganization(jsonArray.getJSONObject(i), 0, serviceContext);
		}
	}

	private static void _importOrganization(
			JSONObject jsonObject, long parentOrganizationId,
			ServiceContext serviceContext)
		throws PortalException {

		String name = jsonObject.getString("name");

		Organization organization =
			OrganizationLocalServiceUtil.fetchOrganization(
				serviceContext.getCompanyId(), name);

		if (organization != null) {
			return;
		}

		String twoLetterISOCode = jsonObject.getString("twoLetterISOCode");

		Country country = CountryServiceUtil.getCountryByA2(
			serviceContext.getCompanyId(), twoLetterISOCode);

		organization = OrganizationLocalServiceUtil.addOrganization(
			null, serviceContext.getUserId(), parentOrganizationId, name,
			OrganizationConstants.TYPE_ORGANIZATION, 0, country.getCountryId(),
			ListTypeLocalServiceUtil.getListTypeId(
				serviceContext.getCompanyId(),
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
				ListTypeConstants.ORGANIZATION_STATUS),
			StringPool.BLANK, false, serviceContext);

		JSONArray suborganizationsJSONArray = jsonObject.getJSONArray(
			"suborganizations");

		if (suborganizationsJSONArray != null) {
			for (int i = 0; i < suborganizationsJSONArray.length(); i++) {
				_importOrganization(
					suborganizationsJSONArray.getJSONObject(i),
					organization.getOrganizationId(), serviceContext);
			}
		}
	}

}