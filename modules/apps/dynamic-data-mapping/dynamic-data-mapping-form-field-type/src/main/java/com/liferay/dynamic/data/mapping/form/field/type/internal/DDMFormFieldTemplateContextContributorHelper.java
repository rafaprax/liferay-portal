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
import com.liferay.dynamic.data.mapping.model.Value;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Leonardo Barros
 */
public interface DDMFormFieldTemplateContextContributorHelper {

	public List<Object> getOptions(
		DDMFormFieldOptions ddmFormFieldOptions, Locale locale,
		boolean viewMode);

	public String getPlaceholder(
		DDMFormField ddmFormField, Locale locale, boolean viewMode);

	public String getPredefinedValue(
		DDMFormField ddmFormField, Locale locale, boolean viewMode);

	public ResourceBundle getResourceBundle(Locale locale);

	public String getTooltip(
		DDMFormField ddmFormField, Locale locale, boolean viewMode);

	public List<String> getValue(String valueString);

	public String getValueString(Value value, Locale locale, boolean viewMode);

}