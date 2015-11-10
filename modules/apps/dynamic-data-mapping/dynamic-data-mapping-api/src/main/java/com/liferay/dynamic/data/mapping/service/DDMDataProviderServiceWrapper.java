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
 * Provides a wrapper for {@link DDMDataProviderService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderService
 * @generated
 */
@ProviderType
public class DDMDataProviderServiceWrapper implements DDMDataProviderService,
	ServiceWrapper<DDMDataProviderService> {
	public DDMDataProviderServiceWrapper(
		DDMDataProviderService ddmDataProviderService) {
		_ddmDataProviderService = ddmDataProviderService;
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMDataProvider addDataProvider(
		long groupId,
		java.util.Map<java.util.Locale, java.lang.String> nameMap,
		java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		java.lang.String definition,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmDataProviderService.addDataProvider(groupId, nameMap,
			descriptionMap, definition, serviceContext);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _ddmDataProviderService.getOSGiServiceIdentifier();
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public DDMDataProviderService getWrappedDDMDataProviderService() {
		return _ddmDataProviderService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedDDMDataProviderService(
		DDMDataProviderService ddmDataProviderService) {
		_ddmDataProviderService = ddmDataProviderService;
	}

	@Override
	public DDMDataProviderService getWrappedService() {
		return _ddmDataProviderService;
	}

	@Override
	public void setWrappedService(DDMDataProviderService ddmDataProviderService) {
		_ddmDataProviderService = ddmDataProviderService;
	}

	private DDMDataProviderService _ddmDataProviderService;
}