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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.functions;

import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyRequest;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Leonardo Barros
 */
public class SetPropertyFunctionTest extends PowerMockito {

	@Test
	public void testApply() {
		SetPropertyFunction setPropertyFunction = new SetMultipleFunction();

		DefaultDDMExpressionObserver defaultDDMExpressionObserver =
			new DefaultDDMExpressionObserver();

		DefaultDDMExpressionObserver spy = spy(defaultDDMExpressionObserver);

		Boolean result = setPropertyFunction.apply(spy, "field", true);

		ArgumentCaptor<UpdateFieldPropertyRequest> argumentCaptor =
			ArgumentCaptor.forClass(UpdateFieldPropertyRequest.class);

		Mockito.verify(
			spy, Mockito.times(1)
		).updateFieldProperty(
			argumentCaptor.capture()
		);

		Assert.assertTrue(result);

		UpdateFieldPropertyRequest updateFieldPropertyRequest =
			argumentCaptor.getValue();

		Assert.assertEquals("field", updateFieldPropertyRequest.getField());
		Assert.assertEquals(
			"multiple", updateFieldPropertyRequest.getProperty());
		Assert.assertTrue(updateFieldPropertyRequest.getValue());
	}

	@Test
	public void testIsObservable() {
		SetPropertyFunction setPropertyFunction = new SetDataTypeFunction();

		Assert.assertTrue(setPropertyFunction.isObservable());
	}

	@Test
	public void testNullObserver() {
		SetPropertyFunction setPropertyFunction = new SetEnabledFunction();

		Assert.assertFalse(setPropertyFunction.apply(null, "field", true));
	}

}