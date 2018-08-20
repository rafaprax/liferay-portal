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

package com.liferay.dynamic.data.mapping.form.field.type.select.internal;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldEvaluationResult;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.form.field.type.internal.DDMFormFieldTemplateContextContributorHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=select",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		SelectDDMFormFieldTemplateContextContributor.class
	}
)
public class SelectDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		DDMFormField ddmFormField =
			ddmFormFieldTemplateContextContributorGetRequest.getDDMFormField();
		Map<String, Object> properties =
			ddmFormFieldTemplateContextContributorGetRequest.getProperties();
		Locale locale =
			ddmFormFieldTemplateContextContributorGetRequest.getLocale();
		boolean viewMode =
			ddmFormFieldTemplateContextContributorGetRequest.isViewMode();
		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();
		HttpServletRequest request =
			ddmFormFieldTemplateContextContributorGetRequest.getRequest();

		DDMFormFieldEvaluationResult ddmFormFieldEvaluationResult =
			(DDMFormFieldEvaluationResult)properties.get(
				"ddmFormFieldEvaluationResult");

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

		DDMFormFieldOptionsFactoryCreateResponse
			ddmFormFieldOptionsFactoryCreateResponse =
				ddmFormFieldOptionsFactory.create(
					ddmFormFieldOptionsFactoryCreateRequest);

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormFieldOptionsFactoryCreateResponse.getDDMFormFieldOptions();

		Map<String, String> stringsMap = new HashMap<>();

		ResourceBundle resourceBundle =
			_ddmFormFieldTemplateContextContributorHelper.getResourceBundle(
				locale);

		stringsMap.put(
			"chooseAnOption",
			LanguageUtil.get(resourceBundle, "choose-an-option"));
		stringsMap.put(
			"chooseOptions",
			LanguageUtil.get(resourceBundle, "choose-options"));
		stringsMap.put(
			"dynamicallyLoadedData",
			LanguageUtil.get(resourceBundle, "dynamically-loaded-data"));
		stringsMap.put(
			"emptyList", LanguageUtil.get(resourceBundle, "empty-list"));
		stringsMap.put("search", LanguageUtil.get(resourceBundle, "search"));

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		builder = builder.withParameter(
			"dataSourceType",
			GetterUtil.getString(
				ddmFormField.getProperty("dataSourceType"), "manual")
		).withParameter(
			"multiple", getMultiple(ddmFormField, ddmFormFieldEvaluationResult)
		).withParameter(
			"options",
			_ddmFormFieldTemplateContextContributorHelper.getOptions(
				ddmFormFieldOptions, locale, viewMode)
		).withParameter(
			"strings", stringsMap
		);

		List<String> predefinedValue =
			_ddmFormFieldTemplateContextContributorHelper.getValue(
				_ddmFormFieldTemplateContextContributorHelper.
					getPredefinedValue(ddmFormField, locale, false));

		if (predefinedValue != null) {
			builder = builder.withParameter("predefinedValue", predefinedValue);
		}

		if (value == null) {
			builder = builder.withParameter("value", "[]");
		}
		else {
			builder = builder.withParameter(
				"value",
				_ddmFormFieldTemplateContextContributorHelper.getValue(
					GetterUtil.getString(value.toString(), "[]"))
			);
		}

		return builder.build();
	}

	protected boolean getMultiple(
		DDMFormField ddmFormField,
		DDMFormFieldEvaluationResult ddmFormFieldEvaluationResult) {

		if (ddmFormFieldEvaluationResult != null) {
			Boolean multiple = ddmFormFieldEvaluationResult.getProperty(
				"multiple");

			if (multiple != null) {
				return multiple;
			}
		}

		return ddmFormField.isMultiple();
	}

	@Reference
	protected DDMFormFieldTemplateContextContributorHelper
		_ddmFormFieldTemplateContextContributorHelper;

	@Reference
	protected DDMFormFieldOptionsFactory ddmFormFieldOptionsFactory;

}