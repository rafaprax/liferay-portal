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

package com.liferay.dynamic.data.mapping.form.field.type.internal;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leonardo Barros
 */
@Component(immediate = true, scope = ServiceScope.SINGLETON)
public class DDMFormFieldTemplateContextContributorHelperImpl
	implements DDMFormFieldTemplateContextContributorHelper {

	@Override
	public List<Object> getOptions(
		DDMFormFieldOptions ddmFormFieldOptions, Locale locale,
		boolean viewMode) {

		List<Object> options = new ArrayList<>();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			Map<String, String> optionMap = new HashMap<>();

			LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			String optionLabelString = optionLabel.getString(locale);

			if (viewMode) {
				optionLabelString = HtmlUtil.extractText(optionLabelString);
			}

			optionMap.put("label", optionLabelString);

			optionMap.put("value", optionValue);

			options.add(optionMap);
		}

		return options;
	}

	@Override
	public String getPlaceholder(
		DDMFormField ddmFormField, Locale locale, boolean viewMode) {

		LocalizedValue placeholder = (LocalizedValue)ddmFormField.getProperty(
			"placeholder");

		return getValueString(placeholder, locale, viewMode);
	}

	@Override
	public String getPredefinedValue(
		DDMFormField ddmFormField, Locale locale, boolean viewMode) {

		LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

		if (predefinedValue == null) {
			return null;
		}

		String predefinedValueString = predefinedValue.getString(locale);

		if (viewMode) {
			predefinedValueString = HtmlUtil.extractText(predefinedValueString);
		}

		return predefinedValueString;
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		Class<?> clazz = getClass();

		ResourceBundleLoader portalResourceBundleLoader =
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader();

		ResourceBundle portalResourceBundle =
			portalResourceBundleLoader.loadResourceBundle(locale);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, clazz.getClassLoader());

		return new AggregateResourceBundle(
			resourceBundle, portalResourceBundle);
	}

	@Override
	public String getTooltip(
		DDMFormField ddmFormField, Locale locale, boolean viewMode) {

		LocalizedValue tooltip = (LocalizedValue)ddmFormField.getProperty(
			"tooltip");

		return getValueString(tooltip, locale, viewMode);
	}

	@Override
	public List<String> getValue(String valueString) {
		JSONArray jsonArray = null;

		try {
			jsonArray = jsonFactory.createJSONArray(valueString);
		}
		catch (JSONException jsone) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsone, jsone);
			}

			jsonArray = jsonFactory.createJSONArray();
		}

		List<String> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(String.valueOf(jsonArray.get(i)));
		}

		return values;
	}

	@Override
	public String getValueString(Value value, Locale locale, boolean viewMode) {
		if (value == null) {
			return StringPool.BLANK;
		}

		String valueString = value.getString(locale);

		if (viewMode) {
			valueString = HtmlUtil.extractText(valueString);
		}

		return valueString;
	}

	@Reference
	protected JSONFactory jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldTemplateContextContributorHelperImpl.class);

}