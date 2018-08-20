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

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInvoker;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactoryCreateResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(immediate = true, service = DDMFormFieldOptionsFactory.class)
public class DDMFormFieldOptionsFactoryImpl
	implements DDMFormFieldOptionsFactory {

	@Override
	public DDMFormFieldOptionsFactoryCreateResponse create(
		DDMFormFieldOptionsFactoryCreateRequest
			ddmFormFieldOptionsFactoryCreateRequest) {

		DDMFormField ddmFormField =
			ddmFormFieldOptionsFactoryCreateRequest.getDDMFormField();
		Locale locale = ddmFormFieldOptionsFactoryCreateRequest.getLocale();
		HttpServletRequest request =
			ddmFormFieldOptionsFactoryCreateRequest.getRequest();
		Object value = ddmFormFieldOptionsFactoryCreateRequest.getValue();
		Map<String, Object> properties =
			ddmFormFieldOptionsFactoryCreateRequest.getProperties();

		String dataSourceType = GetterUtil.getString(
			ddmFormField.getProperty("dataSourceType"), "manual");

		DDMFormFieldOptions ddmFormFieldOptions = null;

		if (Objects.equals(dataSourceType, "from-autofill")) {
			ddmFormFieldOptions = new DDMFormFieldOptions();

			ddmFormFieldOptions.setDefaultLocale(locale);
		}
		else if (Objects.equals(dataSourceType, "data-provider")) {
			ddmFormFieldOptions = createDDMFormFieldOptionsFromDataProvider(
				ddmFormField, locale, request, value);
		}
		else {
			ddmFormFieldOptions = createDDMFormFieldOptions(
				ddmFormField, locale, properties);
		}

		return DDMFormFieldOptionsFactoryCreateResponse.Builder.of(
			ddmFormFieldOptions);
	}

	protected DDMFormFieldOptions createDDMFormFieldOptions(
		DDMFormField ddmFormField, Locale locale,
		Map<String, Object> properties) {

		List<Map<String, String>> options =
			(List<Map<String, String>>)properties.get("options");

		if (options == null) {
			return ddmFormField.getDDMFormFieldOptions();
		}

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		for (Map<String, String> option : options) {
			ddmFormFieldOptions.addOptionLabel(
				option.get("value"), locale, option.get("label"));
		}

		return ddmFormFieldOptions;
	}

	protected DDMFormFieldOptions createDDMFormFieldOptionsFromDataProvider(
		DDMFormField ddmFormField, Locale locale, HttpServletRequest request,
		Object value) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.setDefaultLocale(locale);

		try {
			String ddmDataProviderInstanceId = getJSONArrayFirstValue(
				GetterUtil.getString(
					ddmFormField.getProperty("ddmDataProviderInstanceId")));

			DDMDataProviderRequest.Builder builder =
				DDMDataProviderRequest.Builder.newBuilder();

			DDMDataProviderRequest ddmDataProviderRequest =
				builder.withDDMDataProviderId(
					ddmDataProviderInstanceId
				).withCompanyId(
					portal.getCompanyId(request)
				).withGroupId(
					getGroupId(request)
				).withLocale(
					locale
				).withParameter(
					"filterParameterValue", String.valueOf(value)
				).withParameter(
					"httpServletRequest", request
				).build();

			DDMDataProviderResponse ddmDataProviderResponse =
				ddmDataProviderInvoker.invoke(ddmDataProviderRequest);

			String ddmDataProviderInstanceOutput = getJSONArrayFirstValue(
				GetterUtil.getString(
					ddmFormField.getProperty("ddmDataProviderInstanceOutput"),
					"Default-Output"));

			Optional<List<KeyValuePair>> keyValuesPairsOptional =
				ddmDataProviderResponse.getOutput(
					ddmDataProviderInstanceOutput, List.class);

			if (!keyValuesPairsOptional.isPresent()) {
				return ddmFormFieldOptions;
			}

			for (KeyValuePair keyValuePair : keyValuesPairsOptional.get()) {
				ddmFormFieldOptions.addOptionLabel(
					keyValuePair.getKey(), locale, keyValuePair.getValue());
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return ddmFormFieldOptions;
	}

	protected long getGroupId(HttpServletRequest httpServletRequest) {
		long scopeGroupId = ParamUtil.getLong(
			httpServletRequest, "scopeGroupId");

		if (scopeGroupId == 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			scopeGroupId = themeDisplay.getScopeGroupId();
		}

		return scopeGroupId;
	}

	protected String getJSONArrayFirstValue(String value) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(value);

			return jsonArray.getString(0);
		}
		catch (Exception e) {
			return value;
		}
	}

	@Reference
	protected DDMDataProviderInvoker ddmDataProviderInvoker;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldOptionsFactoryImpl.class);

}