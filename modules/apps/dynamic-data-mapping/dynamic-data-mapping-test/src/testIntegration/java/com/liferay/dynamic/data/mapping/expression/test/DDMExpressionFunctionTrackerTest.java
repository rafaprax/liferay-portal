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

package com.liferay.dynamic.data.mapping.expression.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionTracker;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
* @author Rafael Praxedes
*/
@RunWith(Arquillian.class)
public class DDMExpressionFunctionTrackerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Test
	public void test() {
		DDMExpressionFunction ddmExpressionFunction1 =
			_ddmExpressionFunctionTracker.getDDMExpressionFunction(
				"setRequired");

		DDMExpressionFunction ddmExpressionFunction2 =
			_ddmExpressionFunctionTracker.getDDMExpressionFunction(
				"setRequired");

		Assert.assertNotEquals(ddmExpressionFunction1, ddmExpressionFunction2);
	}

	@Inject(type = DDMExpressionFunctionTracker.class)
	private DDMExpressionFunctionTracker _ddmExpressionFunctionTracker;

}