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

import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.petra.string.StringPool;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leonardo Barros
 */
public class GetValueFunctionTest {

	@Before
	public void setUp() throws Exception {
		_getValueFunction = new GetValueFunction();
	}

	@Test
	public void testArray() {
		String[] expected = {"test1", "test2"};

		DefaultDDMExpressionObserver defaultDDMExpressionObserver =
			new DefaultDDMExpressionObserver();

		GetFieldPropertyResponse.Builder builder =
			GetFieldPropertyResponse.Builder.newBuilder(expected);

		defaultDDMExpressionObserver.setGetFieldPropertyResponseSupplier(
			() -> builder.build());

		Object result = _getValueFunction.apply(
			defaultDDMExpressionObserver, "field");

		Assert.assertEquals(expected, result);
	}

	@Test
	public void testIsObservable() {
		Assert.assertTrue(_getValueFunction.isObservable());
	}

	@Test
	public void testNullObserver() {
		Object result = _getValueFunction.apply(null, "field");

		Assert.assertEquals(StringPool.BLANK, result);
	}

	@Test
	public void testSingleValue() {
		DefaultDDMExpressionObserver defaultDDMExpressionObserver =
			new DefaultDDMExpressionObserver();

		GetFieldPropertyResponse.Builder builder =
			GetFieldPropertyResponse.Builder.newBuilder(new BigDecimal(10));

		defaultDDMExpressionObserver.setGetFieldPropertyResponseSupplier(
			() -> builder.build());

		Object result = _getValueFunction.apply(
			defaultDDMExpressionObserver, "field");

		Assert.assertEquals(new BigDecimal(10), result);
	}

	private GetValueFunction _getValueFunction;

}