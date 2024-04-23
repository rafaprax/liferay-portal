/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.portal.kernel.json.JSONArray;

import java.util.HashMap;

/**
 * @author Renan Vasconcelos
 */
public interface SiteInitializerModelImporter<T> {

	public T importModels(
			JSONArray jsonArray, long scopeGroupId,
			HashMap<String, Object> parameterMap, long userId)
		throws Exception;

}