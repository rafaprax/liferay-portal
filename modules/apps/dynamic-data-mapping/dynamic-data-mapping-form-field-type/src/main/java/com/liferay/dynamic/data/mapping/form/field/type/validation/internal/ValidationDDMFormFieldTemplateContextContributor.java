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

package com.liferay.dynamic.data.mapping.form.field.type.validation.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=validation",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		ValidationDDMFormFieldTemplateContextContributor.class
	}
)
public class ValidationDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		return builder.withParameter(
			"value", getValue(value)
		).build();
	}

	protected Map<String, String> getValue(Object value) {
		Map<String, String> valueMap = new HashMap<>();

		if (value != null) {
			try {
				JSONObject valueJSONObject = jsonFactory.createJSONObject(
					value.toString());

				valueMap.put(
					"errorMessage", valueJSONObject.getString("errorMessage"));
				valueMap.put(
					"expression", valueJSONObject.getString("expression"));
			}
			catch (JSONException jsone) {
				if (_log.isWarnEnabled()) {
					_log.warn(jsone, jsone);
				}
			}
		}
		else {
			valueMap.put("errorMessage", StringPool.BLANK);
			valueMap.put("expression", StringPool.BLANK);
		}

		return valueMap;
	}

	@Reference
	protected JSONFactory jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(
		ValidationDDMFormFieldTemplateContextContributor.class);

}