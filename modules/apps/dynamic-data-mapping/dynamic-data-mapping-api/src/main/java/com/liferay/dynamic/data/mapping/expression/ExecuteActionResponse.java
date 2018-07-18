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
public final class ExecuteActionResponse {

	public <T> Optional<T> getOutput(String name) {
		if (!_output.containsKey(name)) {
			return Optional.empty();
		}

		Supplier<T> supplier = (Supplier<T>)_output.get(name);

		return Optional.of(supplier.get());
	}

	public static class Builder {

		public static Builder newBuilder(boolean success) {
			return new Builder(success);
		}

		public ExecuteActionResponse build() {
			return _executeActionResponse;
		}

		public Builder withOutput(String name, Supplier supplier) {
			_executeActionResponse._output.put(name, supplier);

			return this;
		}

		private Builder(boolean success) {
			_executeActionResponse._success = success;
		}

		private final ExecuteActionResponse _executeActionResponse =
			new ExecuteActionResponse();

	}

	private ExecuteActionResponse() {
	}

	private Map<String, Supplier> _output = new HashMap<>();
	private boolean _success;

}