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

package com.liferay.dynamic.data.mapping.form.field.type.text.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.form.field.type.internal.DDMFormFieldTemplateContextContributorHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=text",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		TextDDMFormFieldTemplateContextContributor.class
	}
)
public class TextDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		DDMFormField ddmFormField =
			ddmFormFieldTemplateContextContributorGetRequest.getDDMFormField();
		Locale locale =
			ddmFormFieldTemplateContextContributorGetRequest.getLocale();
		HttpServletRequest request =
			ddmFormFieldTemplateContextContributorGetRequest.getRequest();
		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();
		Map<String, Object> properties =
			ddmFormFieldTemplateContextContributorGetRequest.getProperties();
		boolean viewMode =
			ddmFormFieldTemplateContextContributorGetRequest.isViewMode();

		DDMFormFieldOptionsFactoryCreateRequest
			ddmFormFieldOptionsFactoryCreateRequest =
				DDMFormFieldOptionsFactoryCreateRequest.Builder.newBuilder(
					ddmFormField
				).withLocale(
					locale
				).withRequest(
					request
				).withValue(
					value
				).withProperties(
					properties
				).build();

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		builder = builder.withParameter(
			"autocompleteEnabled", isAutocompleteEnabled(ddmFormField)
		).withParameter(
			"displayStyle", getDisplayStyle(ddmFormField)
		).withParameter(
			"options",
			getOptions(ddmFormFieldOptionsFactoryCreateRequest, viewMode)
		).withParameter(
			"placeholder",
			_ddmFormFieldTemplateContextContributorHelper.getPlaceholder(
				ddmFormField, locale, viewMode)
		).withParameter(
			"tooltip",
			_ddmFormFieldTemplateContextContributorHelper.getTooltip(
				ddmFormField, locale, viewMode)
		);

		String predefinedValue =
			_ddmFormFieldTemplateContextContributorHelper.getPredefinedValue(
				ddmFormField, locale, viewMode);

		if (predefinedValue != null) {
			builder = builder.withParameter("predefinedValue", predefinedValue);
		}

		String valueString = getValue(properties, viewMode);

		if (valueString != null) {
			builder = builder.withParameter("value", valueString);
		}

		return builder.build();
	}

	protected String getDisplayStyle(DDMFormField ddmFormField) {
		return GetterUtil.getString(
			ddmFormField.getProperty("displayStyle"), "singleline");
	}

	protected List<Object> getOptions(
		DDMFormFieldOptionsFactoryCreateRequest
			ddmFormFieldOptionsFactoryCreateRequest, boolean viewMode) {

		List<Object> options = new ArrayList<>();

		DDMFormFieldOptionsFactoryCreateResponse
			ddmFormFieldOptionsFactoryCreateResponse =
				ddmFormFieldOptionsFactory.create(
					ddmFormFieldOptionsFactoryCreateRequest);

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormFieldOptionsFactoryCreateResponse.getDDMFormFieldOptions();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			Map<String, String> optionMap = new HashMap<>();

			LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			String optionLabelString = optionLabel.getString(
				ddmFormFieldOptionsFactoryCreateRequest.getLocale());

			if (viewMode) {
				optionLabelString = HtmlUtil.extractText(optionLabelString);
			}

			optionMap.put("label", optionLabelString);

			optionMap.put("value", optionValue);

			options.add(optionMap);
		}

		return options;
	}

	protected String getValue(
		Map<String, Object> properties, boolean viewMode) {

		String value = String.valueOf(properties.get("value"));

		if (viewMode) {
			value = HtmlUtil.extractText(value);
		}

		return value;
	}

	protected boolean isAutocompleteEnabled(DDMFormField ddmFormField) {
		return GetterUtil.getBoolean(ddmFormField.getProperty("autocomplete"));
	}

	@Reference
	protected DDMFormFieldTemplateContextContributorHelper
		_ddmFormFieldTemplateContextContributorHelper;

	@Reference
	protected DDMFormFieldOptionsFactory ddmFormFieldOptionsFactory;

	@Reference
	protected JSONFactory jsonFactory;

}