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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.expression;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionObserver;
import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldContextKey;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.helper.DDMFormEvaluatorFormValuesHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
* @author Rafael Praxedes
*/
public class DDMFormEvaluatorExpressionObserver
	implements DDMExpressionObserver {

	public DDMFormEvaluatorExpressionObserver(
		DDMFormEvaluatorFormValuesHelper ddmFormEvaluatorFormValuesHelper,
		Map<DDMFormFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges) {

		_ddmFormEvaluatorFormValuesHelper = ddmFormEvaluatorFormValuesHelper;
		_ddmFormFieldsPropertyChanges = ddmFormFieldsPropertyChanges;
	}

	public void updateFieldProperty(
		DDMFormFieldContextKey ddmFormFieldContextKey,
		Map<String, Object> properties) {

		Map<String, Object> ddmFormFieldProperties =
			_ddmFormFieldsPropertyChanges.get(ddmFormFieldContextKey);

		if (ddmFormFieldProperties == null) {
			ddmFormFieldProperties = new HashMap<>();

			_ddmFormFieldsPropertyChanges.put(
				ddmFormFieldContextKey, ddmFormFieldProperties);
		}

		ddmFormFieldProperties.putAll(properties);
	}

	public void updateFieldProperty(
		String fieldName, Map<String, Object> properties) {

		Set<DDMFormFieldContextKey> ddmFormFieldContextKeySet =
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldContextKeySet(
				fieldName);

		for (DDMFormFieldContextKey ddmFormFieldContextKey :
				ddmFormFieldContextKeySet) {

			updateFieldProperty(ddmFormFieldContextKey, properties);
		}
	}

	@Override
	public UpdateFieldPropertyResponse updateFieldProperty(
		UpdateFieldPropertyRequest updateFieldPropertyRequest) {

		updateFieldProperty(
			updateFieldPropertyRequest.getField(),
			updateFieldPropertyRequest.getProperties());

		UpdateFieldPropertyResponse.Builder builder =
			UpdateFieldPropertyResponse.Builder.newBuilder(true);

		return builder.build();
	}

	private final DDMFormEvaluatorFormValuesHelper
		_ddmFormEvaluatorFormValuesHelper;
	private final Map<DDMFormFieldContextKey, Map<String, Object>>
		_ddmFormFieldsPropertyChanges;

}