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

package com.liferay.dynamic.data.mapping.form.field.type.numeric.internal;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldEvaluationResult;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.form.field.type.internal.DDMFormFieldTemplateContextContributorHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=numeric",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		NumericDDMFormFieldTemplateContextContributor.class
	}
)
public class NumericDDMFormFieldTemplateContextContributor
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
		Object value =
			ddmFormFieldTemplateContextContributorGetRequest.getValue();
		Map<String, Object> properties =
			ddmFormFieldTemplateContextContributorGetRequest.getProperties();

		DDMFormFieldEvaluationResult ddmFormFieldEvaluationResult =
			(DDMFormFieldEvaluationResult)properties.get(
				"ddmFormFieldEvaluationResult");

		String predefinedValue =
			_ddmFormFieldTemplateContextContributorHelper.getValueString(
				ddmFormField.getPredefinedValue(), locale, viewMode);

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		builder = builder.withParameter(
			"dataType", getDataType(ddmFormField, ddmFormFieldEvaluationResult)
		).withParameter(
			"placeholder",
			_ddmFormFieldTemplateContextContributorHelper.getPlaceholder(
				ddmFormField, locale, viewMode)
		).withParameter(
			"predefinedValue", getFormattedValue(predefinedValue, locale)
		).withParameter(
			"symbols", getSymbolsMap(locale)
		).withParameter(
			"tooltip",
			_ddmFormFieldTemplateContextContributorHelper.getTooltip(
				ddmFormField, locale, viewMode)
		);

		if (value != null) {
			builder = builder.withParameter(
				"value",
				getFormattedValue(
					HtmlUtil.extractText(value.toString()), locale)
			);
		}

		return builder.build();
	}

	protected String getDataType(
		DDMFormField ddmFormField,
		DDMFormFieldEvaluationResult ddmFormFieldEvaluationResult) {

		if (ddmFormFieldEvaluationResult != null) {
			String dataType = ddmFormFieldEvaluationResult.getProperty(
				"dataType");

			if (dataType != null) {
				return dataType;
			}
		}

		return ddmFormField.getDataType();
	}

	protected String getFormattedValue(String value, Locale locale) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		DecimalFormat numberFormat = NumericDDMFormFieldUtil.getNumberFormat(
			locale);

		return numberFormat.format(GetterUtil.getNumber(value));
	}

	protected Map<String, String> getSymbolsMap(Locale locale) {
		DecimalFormat formatter = NumericDDMFormFieldUtil.getNumberFormat(
			locale);

		DecimalFormatSymbols decimalFormatSymbols =
			formatter.getDecimalFormatSymbols();

		Map<String, String> symbolsMap = new HashMap<>();

		symbolsMap.put(
			"decimalSymbol",
			String.valueOf(decimalFormatSymbols.getDecimalSeparator()));
		symbolsMap.put(
			"thousandsSeparator",
			String.valueOf(decimalFormatSymbols.getGroupingSeparator()));

		return symbolsMap;
	}

	@Reference
	protected DDMFormFieldTemplateContextContributorHelper
		_ddmFormFieldTemplateContextContributorHelper;

}