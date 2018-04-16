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

package com.liferay.dynamic.data.mapping.type.checkbox.multiple.internal;

import com.liferay.dynamic.data.mapping.util.DDMFormThreadLocal;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marcela Cunha
 */
public class CheckboxMultipleDDMFormFieldValueRequestParameterRetrieverTest {

	@Before
	public void setUp() {
		_checkboxMultipleDDMFormFieldValueRequestParameterRetriever =
			new CheckboxMultipleDDMFormFieldValueRequestParameterRetriever();

		_checkboxMultipleDDMFormFieldValueRequestParameterRetriever.jsonFactory =
			_jsonFactory;
	}

	@Test
	public void testCompletedSubmission() {
		DDMFormThreadLocal.setFormSubmission(true);

		String expectedResult = createJSONArray("Option 2").toString();

		HttpServletRequest httpServletRequest = createHttpServletRequest(
			"Option 2");

		String defaultDDMFormFieldParameterValue = createJSONArray(
			"Option 1").toString();

		String actualResult =
			_checkboxMultipleDDMFormFieldValueRequestParameterRetriever.get(
				httpServletRequest, _CHECKBOX_MULTIPLE_SUBMISSION,
				defaultDDMFormFieldParameterValue);

		Assert.assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testEmptySubmission() {
		DDMFormThreadLocal.setFormSubmission(true);

		String expectedResult = "[]";

		HttpServletRequest httpServletRequest = createHttpServletRequest();

		String defaultDDMFormFieldParameterValue = createJSONArray(
			"Option 1").toString();

		String actualResult =
			_checkboxMultipleDDMFormFieldValueRequestParameterRetriever.get(
				httpServletRequest, _CHECKBOX_MULTIPLE_SUBMISSION,
				defaultDDMFormFieldParameterValue);

		Assert.assertEquals(expectedResult, actualResult);
	}

	protected HttpServletRequest createHttpServletRequest(String... strings) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			_CHECKBOX_MULTIPLE_SUBMISSION, strings);

		return mockHttpServletRequest;
	}

	protected JSONArray createJSONArray(String... strings) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (String string : strings) {
			jsonArray.put(string);
		}

		return jsonArray;
	}

	private static final String _CHECKBOX_MULTIPLE_SUBMISSION =
		"checkBoxSubmissionResult";

	private CheckboxMultipleDDMFormFieldValueRequestParameterRetriever
		_checkboxMultipleDDMFormFieldValueRequestParameterRetriever;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}