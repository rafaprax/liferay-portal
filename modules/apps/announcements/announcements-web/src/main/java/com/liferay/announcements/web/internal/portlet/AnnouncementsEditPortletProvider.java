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

package com.liferay.announcements.web.internal.portlet;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.announcements.kernel.model.AnnouncementsEntry",
	service = PortletProvider.class
)
public class AnnouncementsEditPortletProvider
	extends BasePortletProvider implements PortletProvider {

	@Override
	public String getPortletName() {
		return AnnouncementsPortletKeys.ANNOUNCEMENTS;
	}

	@Override
	public List<Action> getSupportedActions() {
		return Arrays.asList(Action.EDIT, Action.VIEW);
	}

}