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
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.DDMFormViewFormInstanceRecordDisplayContext;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rodrigo Paulino
 */
@RunWith(Arquillian.class)
public class DDMFormViewFormInstanceRecordDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public void setUpOptional() {
		_request = new MockHttpServletRequest();

		_themeDisplay = new ThemeDisplay();

		_request.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_ddmFormViewFormInstanceRecordDisplayContext =
			new DDMFormViewFormInstanceRecordDisplayContext(
				_request, new MockHttpServletResponse(),
				DDMFormInstanceRecordLocalServiceUtil.getService(),
				DDMFormInstanceVersionLocalServiceUtil.getService(),
				_ddmFormRenderer);
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

		DDMFormInstanceRecord ddmFormInstanceRecord =
			addOneLocaleFormInstanceRecord(
				ddmFormInstance, LocaleUtil.US, "test", "yWzWCQhC", "Text",
				TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
				WorkflowConstants.STATUS_APPROVED, true,
				WorkflowConstants.ACTION_PUBLISH);

		((MockHttpServletRequest)_request).addParameter("formInstanceRecordId",
			String.valueOf(ddmFormInstanceRecord.getFormInstanceRecordId()));

		String ddmFormHTML =
			_ddmFormViewFormInstanceRecordDisplayContext.getDDMFormHTML(null);

		String expectedDDMFormHtml = read(
			"ddm-form-view-form-instance-record-display-context-ddm-form-html");

		expectedDDMFormHtml = StringUtil.replace(
			expectedDDMFormHtml, "[containerId]",
			_ddmFormViewFormInstanceRecordDisplayContext.getContainerId());

		Assert.assertEquals(expectedDDMFormHtml, ddmFormHTML);

		((MockHttpServletRequest)_request).removeParameter(
			"formInstanceRecordId");

		DDMFormInstanceLocalServiceUtil.deleteFormInstance(
			ddmFormInstance.getFormInstanceId());

		_themeDisplay.setPathThemeImages("");
	}

	@Override
	protected void tearDownOptional() {
	}

	@Inject
	private DDMFormRenderer _ddmFormRenderer;

	private DDMFormViewFormInstanceRecordDisplayContext
		_ddmFormViewFormInstanceRecordDisplayContext;
	private HttpServletRequest _request;
	private ThemeDisplay _themeDisplay;

}