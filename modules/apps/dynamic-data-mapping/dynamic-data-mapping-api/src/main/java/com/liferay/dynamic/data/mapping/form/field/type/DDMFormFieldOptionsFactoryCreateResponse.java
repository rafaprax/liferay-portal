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

import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;

/**
 * @author Leonardo Barros
 */
public final class DDMFormFieldOptionsFactoryCreateResponse {

	public DDMFormFieldOptions getDDMFormFieldOptions() {
		return _ddmFormFieldOptions;
	}

	public static class Builder {

		public static Builder newBuilder(
			DDMFormFieldOptions ddmFormFieldOptions) {

			return new Builder(ddmFormFieldOptions);
		}

		public static DDMFormFieldOptionsFactoryCreateResponse of(
			DDMFormFieldOptions ddmFormFieldOptions) {

			Builder builder = new Builder(ddmFormFieldOptions);

			return builder.build();
		}

		public DDMFormFieldOptionsFactoryCreateResponse build() {
			return _ddmFormFieldOptionsFactoryCreateResponse;
		}

		private Builder(DDMFormFieldOptions ddmFormFieldOptions) {
			_ddmFormFieldOptionsFactoryCreateResponse._ddmFormFieldOptions =
				ddmFormFieldOptions;
		}

		private final DDMFormFieldOptionsFactoryCreateResponse
			_ddmFormFieldOptionsFactoryCreateResponse =
				new DDMFormFieldOptionsFactoryCreateResponse();

	}

	private DDMFormFieldOptionsFactoryCreateResponse() {
	}

	private DDMFormFieldOptions _ddmFormFieldOptions;

}