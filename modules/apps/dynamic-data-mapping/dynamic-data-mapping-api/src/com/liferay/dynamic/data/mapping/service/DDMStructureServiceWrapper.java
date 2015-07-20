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

package com.liferay.dynamic.data.mapping.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DDMStructureService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureService
 * @generated
 */
@ProviderType
public class DDMStructureServiceWrapper implements DDMStructureService,
	ServiceWrapper<DDMStructureService> {
	public DDMStructureServiceWrapper(DDMStructureService ddmStructureService) {
		_ddmStructureService = ddmStructureService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _ddmStructureService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_ddmStructureService.setBeanIdentifier(beanIdentifier);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public DDMStructureService getWrappedDDMStructureService() {
		return _ddmStructureService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedDDMStructureService(
		DDMStructureService ddmStructureService) {
		_ddmStructureService = ddmStructureService;
	}

	@Override
	public DDMStructureService getWrappedService() {
		return _ddmStructureService;
	}

	@Override
	public void setWrappedService(DDMStructureService ddmStructureService) {
		_ddmStructureService = ddmStructureService;
	}

	private DDMStructureService _ddmStructureService;
}