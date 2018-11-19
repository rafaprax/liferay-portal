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

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormDisplayContext;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.web.MockRenderResponse;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.portlet.MockRenderRequest;

/**
 * @author Rodrigo Paulino
 */
@RunWith(Arquillian.class)
public class DDMFormDisplayContextTest extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public void setUpOptional() throws Exception {
		_request = new MockRenderRequest();

		RenderResponse response = new MockRenderResponse(
			new MockHttpServletResponse(),
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		_themeDisplay = createThemeDisplay(
			TestPropsValues.getCompanyId(), permissionChecker,
			TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_request.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_request.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, httpServletRequest);

		_ddmFormDisplayContext = new DDMFormDisplayContext(
			_request, response, DDMFormInstanceLocalServiceUtil.getService(),
			DDMFormInstanceRecordVersionLocalServiceUtil.getService(),
			DDMFormInstanceServiceUtil.getService(),
			DDMFormInstanceVersionLocalServiceUtil.getService(),
			_ddmFormRenderer, _ddmFormValuesFactory,
			GroupLocalServiceUtil.getService(),
			WorkflowDefinitionLinkLocalServiceUtil.getService(), _portal);
	}

	@Override
	public void tearDownOptional() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testGetDDMFormHTML() throws Exception {
		_themeDisplay.setPathThemeImages("/images");

		DDMFormInstance ddmFormInstance = addOneFieldOneLocaleFormInstance(
			LocaleUtil.US, "Form", null, "Text", "Text", "text", "string", true,
			false, false, "", "", "", "", "Option", "", "", "", "keyword",
			false, "", new JSONArrayImpl(), new JSONArrayImpl(), "singleline",
			false, true, "", StringPool.BLANK, StringPool.BLANK,
			DDMFormLayoutColumn.FULL, DDMFormLayout.WIZARD_MODE,
			TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		addOneLocaleFormInstanceRecord(
			ddmFormInstance, LocaleUtil.US, "", "cUTaX70n", "Text",
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			WorkflowConstants.STATUS_DRAFT, false,
			WorkflowConstants.ACTION_SAVE_DRAFT);

		PortletPreferences portletPreferences = _request.getPreferences();

		portletPreferences.setValue(
			"formInstanceId",
			String.valueOf(ddmFormInstance.getFormInstanceId()));

		String ddmFormHTML = _ddmFormDisplayContext.getDDMFormHTML();

		String expectedDDMFormHtml = read(
			"ddm-form-display-context-ddm-form-html");

		expectedDDMFormHtml = StringUtil.replace(
			expectedDDMFormHtml, "[containerId]",
			_ddmFormDisplayContext.getContainerId());

		Assert.assertEquals(expectedDDMFormHtml, ddmFormHTML);

		portletPreferences.reset("formInstanceId");

		DDMFormInstanceLocalServiceUtil.deleteFormInstance(
			ddmFormInstance.getFormInstanceId());

		_themeDisplay.setPathThemeImages("");
	}

	private DDMFormDisplayContext _ddmFormDisplayContext;

	@Inject
	private DDMFormRenderer _ddmFormRenderer;

	@Inject
	private DDMFormValuesFactory _ddmFormValuesFactory;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private Portal _portal;

	private RenderRequest _request;
	private ThemeDisplay _themeDisplay;

}