/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.lifecycle;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.util.MapUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stian Sigvartsen
 */
public class JAXRSLifecycleUtil {

	public static void ensureReady(BundleContext bundleContext) {

		_serviceRegistrationDCLSingleton.getSingleton(
			() -> bundleContext.registerService(
				Object.class, new Object(),
				MapUtil.singletonDictionary(
					"liferay.jaxrs.whiteboard.ready", true)));
	}

	private static final DCLSingleton<ServiceRegistration<?>>
		_serviceRegistrationDCLSingleton = new DCLSingleton<>();

}