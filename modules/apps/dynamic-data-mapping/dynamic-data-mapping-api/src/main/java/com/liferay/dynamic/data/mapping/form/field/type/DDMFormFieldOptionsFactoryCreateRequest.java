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

package com.liferay.dynamic.data.mapping.form.field.type;

import com.liferay.dynamic.data.mapping.model.DDMFormField;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Leonardo Barros
 */
public final class DDMFormFieldOptionsFactoryCreateRequest {

	public DDMFormField getDDMFormField() {
		return _ddmFormField;
	}

	public Locale getLocale() {
		return _locale;
	}

	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(_properties);
	}

	public HttpServletRequest getRequest() {
		return _request;
	}

	public Object getValue() {
		return _value;
	}

	public static class Builder {

		public static Builder newBuilder(DDMFormField ddmFormField) {
			return new Builder(ddmFormField);
		}

		public DDMFormFieldOptionsFactoryCreateRequest build() {
			return _ddmFormFieldOptionsFactoryCreateRequest;
		}

		public Builder withLocale(Locale locale) {
			_ddmFormFieldOptionsFactoryCreateRequest._locale = locale;

			return this;
		}

		public Builder withProperties(Map<String, Object> properties) {
			_ddmFormFieldOptionsFactoryCreateRequest._properties.putAll(
				properties);

			return this;
		}

		public Builder withRequest(HttpServletRequest request) {
			_ddmFormFieldOptionsFactoryCreateRequest._request = request;

			return this;
		}

		public Builder withValue(Object value) {
			_ddmFormFieldOptionsFactoryCreateRequest._value = value;

			return this;
		}

		private Builder(DDMFormField ddmFormField) {
			_ddmFormFieldOptionsFactoryCreateRequest._ddmFormField =
				ddmFormField;
		}

		private final DDMFormFieldOptionsFactoryCreateRequest
			_ddmFormFieldOptionsFactoryCreateRequest =
				new DDMFormFieldOptionsFactoryCreateRequest();

	}

	private DDMFormFieldOptionsFactoryCreateRequest() {
	}

	private DDMFormField _ddmFormField;
	private Locale _locale;
	private final Map<String, Object> _properties = new HashMap<>();
	private HttpServletRequest _request;
	private Object _value;

}