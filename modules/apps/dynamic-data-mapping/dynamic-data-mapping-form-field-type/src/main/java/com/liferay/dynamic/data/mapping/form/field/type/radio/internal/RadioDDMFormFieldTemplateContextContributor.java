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

package com.liferay.dynamic.data.mapping.form.field.type.radio.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.form.field.type.internal.DDMFormFieldTemplateContextContributorHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=radio",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		RadioDDMFormFieldTemplateContextContributor.class
	}
)
public class RadioDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		DDMFormField ddmFormField =
			ddmFormFieldTemplateContextContributorGetRequest.getDDMFormField();
		Locale locale =
			ddmFormFieldTemplateContextContributorGetRequest.getLocale();
		boolean viewMode =
			ddmFormFieldTemplateContextContributorGetRequest.isViewMode();
		Map<String, Object> properties =
			ddmFormFieldTemplateContextContributorGetRequest.getProperties();
		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		DDMFormFieldOptions ddmFormFieldOptions = getDDMFormFieldOptions(
			ddmFormField, locale,
			(List<Map<String, String>>)properties.get("options"));

		builder = builder.withParameter(
			"inline", GetterUtil.getBoolean(ddmFormField.getProperty("inline"))
		).withParameter(
			"options",
			_ddmFormFieldTemplateContextContributorHelper.getOptions(
				ddmFormFieldOptions, locale, viewMode)
		);

		String predefinedValue = getPredefinedValue(
			ddmFormField, locale, viewMode);

		if (predefinedValue != null) {
			builder = builder.withParameter("predefinedValue", predefinedValue);
		}

		if (value == null) {
			builder = builder.withParameter("value", "[]");
		}
		else {
			builder = builder.withParameter(
				"value", getValue(GetterUtil.getString(value.toString(), "[]"))
			);
		}

		return builder.build();
	}

	protected DDMFormFieldOptions getDDMFormFieldOptions(
		DDMFormField ddmFormField, Locale locale,
		List<Map<String, String>> options) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		String dataSourceType = GetterUtil.getString(
			ddmFormField.getProperty("dataSourceType"), "manual");

		if (Objects.equals(dataSourceType, "manual")) {
			if (options.isEmpty()) {
				return ddmFormField.getDDMFormFieldOptions();
			}

			for (Map<String, String> option : options) {
				ddmFormFieldOptions.addOptionLabel(
					option.get("value"), locale, option.get("label"));
			}
		}

		return ddmFormFieldOptions;
	}

	protected String getPredefinedValue(
		DDMFormField ddmFormField, Locale locale, boolean viewMode) {

		LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

		if (predefinedValue == null) {
			return null;
		}

		String predefinedValueString = GetterUtil.getString(
			predefinedValue.getString(locale), "[]");

		if (viewMode) {
			predefinedValueString = HtmlUtil.extractText(predefinedValueString);
		}

		return getValue(predefinedValueString);
	}

	protected String getValue(String valueString) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(valueString);

			return GetterUtil.getString(jsonArray.get(0));
		}
		catch (JSONException jsone) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsone, jsone);
			}

			return valueString;
		}
	}

	@Reference
	protected DDMFormFieldTemplateContextContributorHelper
		_ddmFormFieldTemplateContextContributorHelper;

	@Reference
	protected JSONFactory jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(
		RadioDDMFormFieldTemplateContextContributor.class);

}