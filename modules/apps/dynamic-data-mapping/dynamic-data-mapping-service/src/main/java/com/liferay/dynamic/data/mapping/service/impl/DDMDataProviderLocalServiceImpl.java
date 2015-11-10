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

import com.liferay.dynamic.data.mapping.exception.DataProviderNameException;
import com.liferay.dynamic.data.mapping.model.DDMDataProvider;
import com.liferay.dynamic.data.mapping.service.base.DDMDataProviderLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Leonardo Barros
 */
public class DDMDataProviderLocalServiceImpl
	extends DDMDataProviderLocalServiceBaseImpl {

	public DDMDataProvider addDataProvider(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String definition,
			ServiceContext serviceContext)
		throws PortalException {

		User user = userPersistence.findByPrimaryKey(userId);

		validateName(nameMap);

		long dataProviderId = counterLocalService.increment();

		DDMDataProvider ddmDataProvider = ddmDataProviderPersistence.create(
			dataProviderId);

		ddmDataProvider.setUuid(serviceContext.getUuid());
		ddmDataProvider.setGroupId(groupId);
		ddmDataProvider.setCompanyId(user.getCompanyId());
		ddmDataProvider.setUserId(user.getUserId());
		ddmDataProvider.setUserName(user.getFullName());
		ddmDataProvider.setDefinition(definition);
		ddmDataProvider.setDescriptionMap(descriptionMap);
		ddmDataProvider.setNameMap(nameMap);

		ddmDataProviderPersistence.update(ddmDataProvider);

		return ddmDataProvider;
	}

	@Override
	public List<DDMDataProvider> search(
		long companyId, long[] groupIds, String keywords, int start, int end,
		OrderByComparator<DDMDataProvider> orderByComparator) {

		return ddmDataProviderFinder.findByKeywords(
			companyId, groupIds, keywords, start, end, orderByComparator);
	}

	@Override
	public List<DDMDataProvider> search(
		long companyId, long[] groupIds, String name, String description,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMDataProvider> orderByComparator) {

		return ddmDataProviderFinder.findByC_G_N_D(
			companyId, groupIds, name, description, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public int searchCount(long companyId, long[] groupIds, String keywords) {
		return ddmDataProviderFinder.countByKeywords(
			companyId, groupIds, keywords);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, String name, String description,
		boolean andOperator) {

		return ddmDataProviderFinder.countByC_G_N_D(
			companyId, groupIds, name, description, andOperator);
	}

	protected void validateName(Map<Locale, String> nameMap)
		throws PortalException {

		String name = nameMap.get(LocaleUtil.getSiteDefault());

		if (Validator.isNull(name)) {
			throw new DataProviderNameException();
		}
	}

}