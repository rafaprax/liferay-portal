/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.FaroController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_MAIN)
@Component(
	property = {
		"jaxrs.application=true",
		"osgi.http.whiteboard.filter.dispatcher=FORWARD",
		"osgi.http.whiteboard.filter.dispatcher=REQUEST"
	},
	service = Application.class
)
public class MainApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_blockedKeywordsController);
		controllers.add(_channelController);
		controllers.add(_definitionsController);
		controllers.add(_issueController);
		controllers.add(_mainController);
		controllers.add(_notificationController);
		controllers.add(_oAuth2Controller);
		controllers.add(_preferencesController);
		controllers.add(_projectController);
		controllers.add(_reportController);
		controllers.add(_userController);

		return controllers;
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.BlockedKeywordsController)"
	)
	private FaroController _blockedKeywordsController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ChannelController)"
	)
	private FaroController _channelController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.DefinitionsController)"
	)
	private FaroController _definitionsController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.IssueController)"
	)
	private FaroController _issueController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.MainController)"
	)
	private Object _mainController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.NotificationController)"
	)
	private FaroController _notificationController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.OAuth2Controller)"
	)
	private FaroController _oAuth2Controller;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.PreferencesController)"
	)
	private FaroController _preferencesController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ProjectController)"
	)
	private FaroController _projectController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ReportController)"
	)
	private FaroController _reportController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.UserController)"
	)
	private FaroController _userController;

}