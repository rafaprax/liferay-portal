/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMDataProvider;
import com.liferay.dynamic.data.mapping.service.base.DDMDataProviderServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.service.ServiceContext;

import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Leonardo Barros
 */
public class DDMDataProviderServiceImpl extends DDMDataProviderServiceBaseImpl {

	@Override
	public DDMDataProvider addDataProvider(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String definition,
			ServiceContext serviceContext)
		throws PortalException {

		return ddmDataProviderLocalService.addDataProvider(
			getUserId(), groupId, nameMap, descriptionMap, definition,
			serviceContext);
	}

}