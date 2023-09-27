/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.apple.internal.util;

import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.push.notifications.constants.PushNotificationsDestinationNames;

import com.liferay.push.notifications.sender.apple.internal.messaging.ApplePushNotificationsResponseMessageListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Bruno Farache
 */
public class ApplePushNotificationsMessagingConfiguratorUtil {

	private static final ServiceRegistration<MessageListener>
		_serviceRegistration;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ApplePushNotificationsMessagingConfiguratorUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			MessageListener.class,
			new ApplePushNotificationsResponseMessageListener(),
			MapUtil.singletonDictionary(
				"destination.name",
				PushNotificationsDestinationNames.PUSH_NOTIFICATION_RESPONSE));
	}

}