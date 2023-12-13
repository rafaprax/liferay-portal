/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.controller.FaroControllerRegistry;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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

		controllers.add(_blockedKeywordsFaroController);
		controllers.add(_channelFaroController);
		controllers.add(_definitionsFaroController);
		controllers.add(_issueFaroController);
		controllers.add(_notificationFaroController);
		controllers.add(_oAuth2FaroController);
		controllers.add(_preferencesFaroController);
		controllers.add(_projectFaroController);
		controllers.add(_reportFaroController);
		controllers.add(_userFaroController);
		controllers.add(this);

		return controllers;
	}

	@GET
	@Path("/{groupId}/entities")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<FaroResultsDisplay> search(
			@PathParam("groupId") long groupId,
			@QueryParam("faroSearchContexts") FaroParam<List<FaroSearchContext>>
				faroSearchContextsFaroParam)
		throws Exception {

		return _faroControllerRegistry.search(
			groupId, faroSearchContextsFaroParam.getValue());
	}

	@Path("/{groupId}/entities/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<FaroResultsDisplay> searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("faroSearchContexts") FaroParam<List<FaroSearchContext>>
				faroSearchContextsFaroParam)
		throws Exception {

		return search(groupId, faroSearchContextsFaroParam);
	}

	@Path("/{groupId}/engine")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(StringPool.BLANK)
	public void setEngineURL(
		@FormParam("contactsEngineURL") String contactsEngineURL) {

		if (Validator.isNotNull(contactsEngineURL)) {
			contactsEngineClient.setEngineURL(contactsEngineURL);
		}
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile ContactsEngineClient contactsEngineClient;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.BlockedKeywordsFaroController)"
	)
	private FaroController _blockedKeywordsFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ChannelFaroController)"
	)
	private FaroController _channelFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.DefinitionsFaroController)"
	)
	private FaroController _definitionsFaroController;

	@Reference
	private FaroControllerRegistry _faroControllerRegistry;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.IssueFaroController)"
	)
	private FaroController _issueFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.NotificationFaroController)"
	)
	private FaroController _notificationFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.OAuth2FaroController)"
	)
	private FaroController _oAuth2FaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.PreferencesFaroController)"
	)
	private FaroController _preferencesFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ProjectFaroController)"
	)
	private FaroController _projectFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.ReportFaroController)"
	)
	private FaroController _reportFaroController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.main.UserFaroController)"
	)
	private FaroController _userFaroController;

}