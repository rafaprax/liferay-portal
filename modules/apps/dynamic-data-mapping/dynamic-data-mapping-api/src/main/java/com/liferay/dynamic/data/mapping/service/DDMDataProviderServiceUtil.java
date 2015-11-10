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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the remote service utility for DDMDataProvider. This utility wraps
 * {@link com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderService
 * @see com.liferay.dynamic.data.mapping.service.base.DDMDataProviderServiceBaseImpl
 * @see com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderServiceImpl
 * @generated
 */
@ProviderType
public class DDMDataProviderServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.dynamic.data.mapping.model.DDMDataProvider addDataProvider(
		long groupId,
		java.util.Map<java.util.Locale, java.lang.String> nameMap,
		java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		java.lang.String definition,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .addDataProvider(groupId, nameMap, descriptionMap,
			definition, serviceContext);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static java.lang.String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static DDMDataProviderService getService() {
		return _serviceTracker.getService();
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	@Deprecated
	public void setService(DDMDataProviderService service) {
	}

	private static ServiceTracker<DDMDataProviderService, DDMDataProviderService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DDMDataProviderServiceUtil.class);

		_serviceTracker = new ServiceTracker<DDMDataProviderService, DDMDataProviderService>(bundle.getBundleContext(),
				DDMDataProviderService.class, null);

		_serviceTracker.open();
	}
}