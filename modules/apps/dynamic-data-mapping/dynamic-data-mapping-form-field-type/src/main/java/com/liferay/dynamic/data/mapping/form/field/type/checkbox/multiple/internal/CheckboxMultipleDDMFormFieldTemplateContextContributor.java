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

package com.liferay.dynamic.data.mapping.form.field.type.checkbox.multiple.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.form.field.type.internal.DDMFormFieldTemplateContextContributorHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=checkbox_multiple",
	service = {
		CheckboxMultipleDDMFormFieldTemplateContextContributor.class,
		DDMFormFieldTemplateContextContributor.class
	}
)
public class CheckboxMultipleDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		DDMFormField ddmFormField =
			ddmFormFieldTemplateContextContributorGetRequest.getDDMFormField();
		Locale locale =
			ddmFormFieldTemplateContextContributorGetRequest.getLocale();
		Map<String, Object> properties =
			ddmFormFieldTemplateContextContributorGetRequest.getProperties();
		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();
		boolean viewMode =
			ddmFormFieldTemplateContextContributorGetRequest.isViewMode();

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
		).withParameter(
			"showAsSwitcher",
			GetterUtil.getBoolean(ddmFormField.getProperty("showAsSwitcher"))
		).withParameter(
			"value",
			_ddmFormFieldTemplateContextContributorHelper.getValue(
				GetterUtil.getString(value, "[]"))
		);

		List<String> predefinedValue =
			_ddmFormFieldTemplateContextContributorHelper.getValue(
				_ddmFormFieldTemplateContextContributorHelper.
					getPredefinedValue(ddmFormField, locale, false));

		if (predefinedValue != null) {
			builder = builder.withParameter("predefinedValue", predefinedValue);
		}

		return builder.build();
	}

	protected DDMFormFieldOptions getDDMFormFieldOptions(
		DDMFormField ddmFormField, Locale locale,
		List<Map<String, String>> options) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		if (options.isEmpty()) {
			return ddmFormField.getDDMFormFieldOptions();
		}

		for (Map<String, String> option : options) {
			ddmFormFieldOptions.addOptionLabel(
				option.get("value"), locale, option.get("label"));
		}

		return ddmFormFieldOptions;
	}

	@Reference
	protected DDMFormFieldTemplateContextContributorHelper
		_ddmFormFieldTemplateContextContributorHelper;

}