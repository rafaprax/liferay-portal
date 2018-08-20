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
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Leonardo Barros
 */
public final class DDMFormFieldTemplateContextContributorGetRequest {

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

	public HttpServletResponse getResponse() {
		return _response;
	}

	public Object getValue() {
		return _value;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isViewMode() {
		return _viewMode;
	}

	public static class Builder {

		public static Builder newBuilder() {
			return new Builder();
		}

		public DDMFormFieldTemplateContextContributorGetRequest build() {
			return _ddmFormFieldTemplateContextContributorGetParametersRequest;
		}

		public Builder withDDMFormField(DDMFormField ddmFormField) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_ddmFormField = ddmFormField;

			return this;
		}

		public Builder withLocale(Locale locale) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_locale = locale;

			return this;
		}

		public Builder withProperty(String name, Object value) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_properties.put(name, value);

			return this;
		}

		public Builder withReadOnly(boolean readOnly) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_readOnly = readOnly;

			return this;
		}

		public Builder withRequest(HttpServletRequest request) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_request = request;

			return this;
		}

		public Builder withResponse(HttpServletResponse response) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_response = response;

			return this;
		}

		public Builder withValue(Object value) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_value = value;

			return this;
		}

		public Builder withViewMode(boolean viewMode) {
			_ddmFormFieldTemplateContextContributorGetParametersRequest.
				_viewMode = viewMode;

			return this;
		}

		private Builder() {
		}

		private final DDMFormFieldTemplateContextContributorGetRequest
			_ddmFormFieldTemplateContextContributorGetParametersRequest =
				new DDMFormFieldTemplateContextContributorGetRequest();

	}

	private DDMFormFieldTemplateContextContributorGetRequest() {
	}

	private DDMFormField _ddmFormField;
	private Locale _locale = LocaleUtil.getDefault();
	private final Map<String, Object> _properties = new HashMap<>();
	private boolean _readOnly;
	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private Object _value;
	private boolean _viewMode;

}