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

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionTracker;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(immediate = true, service = DDMExpressionFactory.class)
public class DDMExpressionFactoryImpl implements DDMExpressionFactory {

	@Override
	public <T> DDMExpression<T> createExpression(
			CreateExpressionRequest createExpressionRequest)
		throws DDMExpressionException {

		DDMExpressionImpl<T> ddmExpression = new DDMExpressionImpl<>(
			createExpressionRequest.getExpression());

		ddmExpression.setDDMExpressionActionHandler(
			createExpressionRequest.getDDMExpressionActionHandler());
		ddmExpression.setDDMExpressionFieldAccessor(
			createExpressionRequest.getDDMExpressionFieldAccessor());
		ddmExpression.setDDMExpressionFunctions(getDDMExpressionFunctions());
		ddmExpression.setDDMExpressionObserver(
			createExpressionRequest.getDDMExpressionObserver());
		ddmExpression.setDDMExpressionParameterAccessor(
			createExpressionRequest.getDDMExpressionParameterAccessor());

		return ddmExpression;
	}

	public void setDDMExpressionFunctionTracker(
		DDMExpressionFunctionTracker ddmExpressionFunctionTracker) {

		this.ddmExpressionFunctionTracker = ddmExpressionFunctionTracker;
	}

	protected Map<String, DDMExpressionFunction> getDDMExpressionFunctions() {
		if (ddmExpressionFunctionTracker == null) {
			return Collections.emptyMap();
		}

		return ddmExpressionFunctionTracker.getDDMExpressionFunctions();
	}

	@Reference
	protected DDMExpressionFunctionTracker ddmExpressionFunctionTracker;

}