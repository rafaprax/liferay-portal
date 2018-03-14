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

package com.liferay.dynamic.data.mapping.type.checkbox.internal;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringPool;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marcellus Tavares
 */
@PrepareForTest({PortalClassLoaderUtil.class, ResourceBundleUtil.class})
@RunWith(PowerMockRunner.class)
public class CheckboxDDMFormFieldValueRequestParameterRetrieverTest {

	@Test
	public void testGetFalseAsValue() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		request.addParameter("ddmFormFieldCheckbox", StringPool.TRUE);

		String parameterValue =
			_checkboxDDMFormFieldValueRequestParameterRetriever.get(
				request, "ddmFormFieldCheckbox", StringPool.FALSE);

		Assert.assertEquals(StringPool.TRUE, parameterValue);
	}

	@Test
	public void testGetTrueAsValue() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		request.addParameter("ddmFormFieldCheckbox", StringPool.TRUE);

		String parameterValue =
			_checkboxDDMFormFieldValueRequestParameterRetriever.get(
				request, "ddmFormFieldCheckbox", StringPool.FALSE);

		Assert.assertEquals(StringPool.TRUE, parameterValue);
	}

	@Test
	public void testGetValueWithNullParameter() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		String parameterValue =
			_checkboxDDMFormFieldValueRequestParameterRetriever.get(
				request, "ddmFormFieldCheckbox", StringPool.TRUE);

		Assert.assertEquals(StringPool.FALSE, parameterValue);
	}

	private final CheckboxDDMFormFieldValueRequestParameterRetriever
		_checkboxDDMFormFieldValueRequestParameterRetriever =
			new CheckboxDDMFormFieldValueRequestParameterRetriever();

}