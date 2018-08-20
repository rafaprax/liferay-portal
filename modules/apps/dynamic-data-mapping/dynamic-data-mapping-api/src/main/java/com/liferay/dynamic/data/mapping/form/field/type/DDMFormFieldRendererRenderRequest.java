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
public final class DDMFormFieldRendererRenderRequest {

	public DDMFormField getDDMFormField() {
		return _ddmFormField;
	}

	public String getLabel() {
		return _label;
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getName() {
		return _name;
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

	public String getTip() {
		return _tip;
	}

	public Object getValue() {
		return _value;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	public boolean isViewMode() {
		return _viewMode;
	}

	public boolean isVisible() {
		return _visible;
	}

	public static class Builder {

		public static Builder newBuilder() {
			return new Builder();
		}

		public DDMFormFieldRendererRenderRequest build() {
			return _ddmFormFieldRendererRenderRequest;
		}

		public Builder withDDMFormField(DDMFormField ddmFormField) {
			_ddmFormFieldRendererRenderRequest._ddmFormField = ddmFormField;

			return this;
		}

		public Builder withLabel(String label) {
			_ddmFormFieldRendererRenderRequest._label = label;

			return this;
		}

		public Builder withLocale(Locale locale) {
			_ddmFormFieldRendererRenderRequest._locale = locale;

			return this;
		}

		public Builder withName(String name) {
			_ddmFormFieldRendererRenderRequest._name = name;

			return this;
		}

		public Builder withProperty(String name, Object value) {
			_ddmFormFieldRendererRenderRequest._properties.put(name, value);

			return this;
		}

		public Builder withReadOnly(boolean readOnly) {
			_ddmFormFieldRendererRenderRequest._readOnly = readOnly;

			return this;
		}

		public Builder withRequest(HttpServletRequest request) {
			_ddmFormFieldRendererRenderRequest._request = request;

			return this;
		}

		public Builder withRequired(boolean required) {
			_ddmFormFieldRendererRenderRequest._required = required;

			return this;
		}

		public Builder withResponse(HttpServletResponse response) {
			_ddmFormFieldRendererRenderRequest._response = response;

			return this;
		}

		public Builder withShowLabel(boolean showLabel) {
			_ddmFormFieldRendererRenderRequest._showLabel = showLabel;

			return this;
		}

		public Builder withTip(String tip) {
			_ddmFormFieldRendererRenderRequest._tip = tip;

			return this;
		}

		public Builder withValue(Object value) {
			_ddmFormFieldRendererRenderRequest._value = value;

			return this;
		}

		public Builder withViewMode(boolean viewMode) {
			_ddmFormFieldRendererRenderRequest._viewMode = viewMode;

			return this;
		}

		public Builder withVisible(boolean visible) {
			_ddmFormFieldRendererRenderRequest._visible = visible;

			return this;
		}

		private Builder() {
		}

		private final DDMFormFieldRendererRenderRequest
			_ddmFormFieldRendererRenderRequest =
				new DDMFormFieldRendererRenderRequest();

	}

	private DDMFormFieldRendererRenderRequest() {
	}

	private DDMFormField _ddmFormField;
	private String _label;
	private Locale _locale = LocaleUtil.getDefault();
	private String _name;
	private final Map<String, Object> _properties = new HashMap<>();
	private boolean _readOnly;
	private HttpServletRequest _request;
	private boolean _required;
	private HttpServletResponse _response;
	private boolean _showLabel = true;
	private String _tip;
	private Object _value;
	private boolean _viewMode;
	private boolean _visible = true;

}