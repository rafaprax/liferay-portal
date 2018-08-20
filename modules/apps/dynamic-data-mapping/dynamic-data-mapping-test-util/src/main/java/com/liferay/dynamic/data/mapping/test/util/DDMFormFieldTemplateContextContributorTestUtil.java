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

package com.liferay.dynamic.data.mapping.test.util;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;

import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class DDMFormFieldTemplateContextContributorTestUtil {

	public static Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor) {

		DDMFormFieldTemplateContextContributorGetRequest.Builder builder =
			DDMFormFieldTemplateContextContributorGetRequest.Builder.newBuilder(
			).withReadOnly(
				ddmFormFieldRenderingContext.isReadOnly() ||
				ddmFormField.isReadOnly()
			).withValue(
				ddmFormFieldRenderingContext.getValue()
			).withDDMFormField(
				ddmFormField
			).withRequest(
				ddmFormFieldRenderingContext.getHttpServletRequest()
			).withResponse(
				ddmFormFieldRenderingContext.getHttpServletResponse()
			);

		Map<String, Object> properties =
			ddmFormFieldRenderingContext.getProperties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			builder = builder.withProperty(entry.getKey(), entry.getValue());
		}

		DDMFormFieldTemplateContextContributorGetResponse
			ddmFormFieldTemplateContextContributorGetResponse =
				ddmFormFieldTemplateContextContributor.get(builder.build());

		return ddmFormFieldTemplateContextContributorGetResponse.
			getParameters();
	}

}