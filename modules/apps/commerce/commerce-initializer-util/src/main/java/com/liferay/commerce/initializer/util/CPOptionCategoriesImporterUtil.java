/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.commerce.initializer.util.internal.CommerceInitializerUtil;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class CPOptionCategoriesImporterUtil {

	public static List<CPOptionCategory> importCPOptionCategories(
			JSONArray jsonArray, long scopeGroupId, long userId)
		throws PortalException {

		User user = UserLocalServiceUtil.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		List<CPOptionCategory> cpOptionCategories = new ArrayList<>(
			jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			CPOptionCategory cpOptionCategory = _importCPOptionCategory(
				jsonArray.getJSONObject(i), i, serviceContext);

			cpOptionCategories.add(cpOptionCategory);
		}

		return cpOptionCategories;
	}

	private static CPOptionCategory _importCPOptionCategory(
			JSONObject jsonObject, double defaultPriority,
			ServiceContext serviceContext)
		throws PortalException {

		String key = jsonObject.getString("key");

		CPOptionCategory cpOptionCategory =
			CPOptionCategoryLocalServiceUtil.fetchCPOptionCategory(
				serviceContext.getCompanyId(), key);

		if (cpOptionCategory != null) {
			return CPOptionCategoryLocalServiceUtil.updateCPOptionCategory(
				cpOptionCategory.getCPOptionCategoryId(),
				_toMap(key, jsonObject, "title"),
				_toMap(null, jsonObject, "description"),
				jsonObject.getDouble("priority", defaultPriority), key);
		}

		return CPOptionCategoryLocalServiceUtil.addCPOptionCategory(
			serviceContext.getUserId(), _toMap(key, jsonObject, "title"),
			_toMap(null, jsonObject, "description"),
			jsonObject.getDouble("priority", defaultPriority), key,
			serviceContext);
	}

	private static Map<Locale, String> _toMap(
		String defaultValue, JSONObject jsonObject, String nodeName) {

		String value = jsonObject.getString(nodeName);

		if (Validator.isBlank(value)) {
			if (Validator.isBlank(defaultValue)) {
				return Collections.emptyMap();
			}

			return Collections.singletonMap(
				LocaleUtil.getSiteDefault(),
				CommerceInitializerUtil.getValue(
					jsonObject, nodeName, defaultValue));
		}

		Map<Locale, String> map = new HashMap<>();

		Map<String, String> valuesMap = ObjectMapperUtil.readValue(
			HashMap.class, value);

		for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
			map.put(
				LocaleUtil.fromLanguageId(entry.getKey()), entry.getValue());
		}

		return map;
	}

}