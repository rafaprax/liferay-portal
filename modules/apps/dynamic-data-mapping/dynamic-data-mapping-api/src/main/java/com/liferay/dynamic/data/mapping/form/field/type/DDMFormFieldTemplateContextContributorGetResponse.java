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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public final class DDMFormFieldTemplateContextContributorGetResponse {

	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(_parameters);
	}

	public static class Builder {

		public static Builder newBuilder() {
			return new Builder();
		}

		public DDMFormFieldTemplateContextContributorGetResponse build() {
			return _ddmFormFieldTemplateContextContributorGetResponse;
		}

		public Builder withParameter(String name, Object value) {
			_ddmFormFieldTemplateContextContributorGetResponse._parameters.put(
				name, value);

			return this;
		}

		private Builder() {
		}

		private final DDMFormFieldTemplateContextContributorGetResponse
			_ddmFormFieldTemplateContextContributorGetResponse =
				new DDMFormFieldTemplateContextContributorGetResponse();

	}

	private DDMFormFieldTemplateContextContributorGetResponse() {
	}

	private final Map<String, Object> _parameters = new HashMap<>();

}