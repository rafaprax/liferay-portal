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

package com.liferay.portal.workflow.sample.web.internal.portlet;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=sample",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=My Workflow Sample",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=com_liferay_portal_workflow_task_web_portlet_MyWorkflowSamplePortlet",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class MyWorkflowSamplePortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		System.out.println(
			"resolveModuleName 1 = " +
				_npmResolver.resolveModuleName(
					"dynamic-data-mapping-form-field-type/Captcha/Captcha.es"));
		System.out.println(
			"resolveModuleName 3 = " +
				_npmResolver.resolveModuleName(
					"dynamic-data-mapping-form-field-type"));
		System.out.println(
			"resolveModuleName 3 = " +
				_npmResolver.resolveModuleName(
					"commerce-shop-by-diagram-web"));
		System.out.println(
			"resolveModuleName 4 = " +
				_npmResolver.resolveModuleName(
					"portal-workflow-instance-tracker-web"));

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private NPMResolver _npmResolver;

}