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

package com.liferay.dynamic.data.mapping.expression.internal;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Barros
 */
@RunWith(PowerMockRunner.class)
public class DDMExpressionFunctionTrackerImplTest extends PowerMockito {

	@Test
	public void testActivate() {
		DDMExpressionFunctionTrackerImpl ddmExpressionFunctionTracker =
			new DDMExpressionFunctionTrackerImpl();

		Assert.assertNotNull(
			ddmExpressionFunctionTracker.
				ddmExpressionFunctionComponentFactoryMap);
	}

	@Test
	public void testDeactivate() {
		DDMExpressionFunctionTrackerImpl ddmExpressionFunctionTracker =
			new DDMExpressionFunctionTrackerImpl();

		ddmExpressionFunctionTracker.ddmExpressionFunctionComponentFactoryMap =
			spy(new HashMap<String, ComponentFactory>());

		ddmExpressionFunctionTracker.deactivate();

		Mockito.verify(
			ddmExpressionFunctionTracker.
				ddmExpressionFunctionComponentFactoryMap,
			Mockito.times(1)
		).clear();
	}

	@Test
	public void testGetDDMExpressionFunction() {
		DDMExpressionFunctionTrackerImpl ddmExpressionFunctionTracker =
			new DDMExpressionFunctionTrackerImpl();

		ddmExpressionFunctionTracker.ddmExpressionFunctionComponentFactoryMap =
			spy(new HashMap<String, ComponentFactory>());

		ddmExpressionFunctionTracker.getDDMExpressionFunction("function");

		Mockito.verify(
			ddmExpressionFunctionTracker.
				ddmExpressionFunctionComponentFactoryMap,
			Mockito.times(1)
		).get(
			"function"
		);
	}

	@Test
	public void testGetDDMExpressionFunctions() {
		DDMExpressionFunctionTrackerImpl ddmExpressionFunctionTracker =
			new DDMExpressionFunctionTrackerImpl();

		Map<String, ComponentFactory> spy = spy(
			new HashMap<String, ComponentFactory>());

		ddmExpressionFunctionTracker.ddmExpressionFunctionComponentFactoryMap =
			spy;

		DDMExpressionFunction ddmExpressionFunction1 = mock(
			DDMExpressionFunction.class);

		spy.put("function1", mockComponentFactory(ddmExpressionFunction1));

		DDMExpressionFunction ddmExpressionFunction2 = mock(
			DDMExpressionFunction.class);

		spy.put("function2", mockComponentFactory(ddmExpressionFunction2));

		Map<String, DDMExpressionFunction> ddmExpressionFunctions =
			ddmExpressionFunctionTracker.getDDMExpressionFunctions();

		Assert.assertEquals(
			ddmExpressionFunction1, ddmExpressionFunctions.get("function1"));

		Assert.assertEquals(
			ddmExpressionFunction2, ddmExpressionFunctions.get("function2"));

		InOrder inOrder = Mockito.inOrder(spy);

		inOrder.verify(
			spy, Mockito.times(1)
		).keySet();

		inOrder.verify(
			spy, Mockito.times(2)
		).get(
			Matchers.anyString()
		);
	}

	protected ComponentFactory mockComponentFactory(
		DDMExpressionFunction ddmExpressionFunction) {

		ComponentInstance componentInstance = mock(ComponentInstance.class);

		when(
			componentInstance.getInstance()
		).thenReturn(
			ddmExpressionFunction
		);

		ComponentFactory componentFactory = mock(ComponentFactory.class);

		when(
			componentFactory.newInstance(Matchers.any())
		).thenReturn(
			componentInstance
		);

		return componentFactory;
	}

}