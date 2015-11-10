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

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMDataProvider;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderPersistence;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMDataProviderFinderBaseImpl extends BasePersistenceImpl<DDMDataProvider> {
	@Override
	public Set<String> getBadColumnNames() {
		return getDDMDataProviderPersistence().getBadColumnNames();
	}

	/**
	 * Returns the d d m data provider persistence.
	 *
	 * @return the d d m data provider persistence
	 */
	public DDMDataProviderPersistence getDDMDataProviderPersistence() {
		return ddmDataProviderPersistence;
	}

	/**
	 * Sets the d d m data provider persistence.
	 *
	 * @param ddmDataProviderPersistence the d d m data provider persistence
	 */
	public void setDDMDataProviderPersistence(
		DDMDataProviderPersistence ddmDataProviderPersistence) {
		this.ddmDataProviderPersistence = ddmDataProviderPersistence;
	}

	@BeanReference(type = DDMDataProviderPersistence.class)
	protected DDMDataProviderPersistence ddmDataProviderPersistence;
}