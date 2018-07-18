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

package com.liferay.dynamic.data.mapping.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Leonardo Barros
 */
public final class UpdateFieldPropertyRequest {

	public String getField() {
		return _field;
	}

	public <T> Optional<T> getParameter(String name) {
		if (!_parameters.containsKey(name)) {
			return Optional.empty();
		}

		Supplier<T> supplier = (Supplier<T>)_parameters.get(name);

		return Optional.of(supplier.get());
	}

	public String getProperty() {
		return _property;
	}

	public <T> T getValue() {
		return ((Supplier<T>)_valueSupplier).get();
	}

	public static class Builder {

		public static Builder newBuilder(
			String field, String property, Supplier valueSupplier) {

			return new Builder(field, property, valueSupplier);
		}

		public UpdateFieldPropertyRequest build() {
			return _updateFieldPropertyRequest;
		}

		public Builder withParameter(String name, Supplier supplier) {
			_updateFieldPropertyRequest._parameters.put(name, supplier);

			return this;
		}

		private Builder(String field, String property, Supplier valueSupplier) {
			_updateFieldPropertyRequest._field = field;
			_updateFieldPropertyRequest._property = property;
			_updateFieldPropertyRequest._valueSupplier = valueSupplier;
		}

		private final UpdateFieldPropertyRequest _updateFieldPropertyRequest =
			new UpdateFieldPropertyRequest();

	}

	private UpdateFieldPropertyRequest() {
	}

	private String _field;
	private Map<String, Supplier> _parameters = new HashMap<>();
	private String _property;
	private Supplier _valueSupplier;

}