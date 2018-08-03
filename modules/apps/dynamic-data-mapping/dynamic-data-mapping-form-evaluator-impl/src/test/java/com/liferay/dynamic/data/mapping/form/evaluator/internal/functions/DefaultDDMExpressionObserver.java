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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.functions;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionObserver;
import com.liferay.dynamic.data.mapping.expression.ExecuteActionRequest;
import com.liferay.dynamic.data.mapping.expression.ExecuteActionResponse;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyResponse;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author Leonardo Barros
 */
public class DefaultDDMExpressionObserver implements DDMExpressionObserver {

	@Override
	public ExecuteActionResponse executeAction(
		ExecuteActionRequest executeActionRequest) {

		return null;
	}

	@Override
	public long getCompanyId() {
		return _getCompanyIdSupplier.get();
	}

	@Override
	public GetFieldPropertyResponse getFieldProperty(
		GetFieldPropertyRequest getFieldPropertyRequest) {

		return _getFieldPropertyResponseSupplier.get();
	}

	@Override
	public long getGroupId() {
		return _getGroupIdSupplier.get();
	}

	@Override
	public Locale getLocale() {
		return _getLocaleSupplier.get();
	}

	@Override
	public long getUserId() {
		return _getUserIdSupplier.get();
	}

	@Override
	public boolean isField(String parameter) {
		return _isFieldSupplier.get();
	}

	@Override
	public UpdateFieldPropertyResponse updateFieldProperty(
		UpdateFieldPropertyRequest updateFieldPropertyRequest) {

		return null;
	}

	protected void setGetCompanyIdSupplier(Supplier<Long> supplier) {
		_getCompanyIdSupplier = supplier;
	}

	protected void setGetFieldPropertyResponseSupplier(
		Supplier<GetFieldPropertyResponse> supplier) {

		_getFieldPropertyResponseSupplier = supplier;
	}

	protected void setGetGroupIdSupplier(Supplier<Long> supplier) {
		_getGroupIdSupplier = supplier;
	}

	protected void setGetLocaleSupplier(Supplier<Locale> supplier) {
		_getLocaleSupplier = supplier;
	}

	protected void setGetUserIdSupplier(Supplier<Long> supplier) {
		_getUserIdSupplier = supplier;
	}

	protected void setIsFieldSupplier(Supplier<Boolean> supplier) {
		_isFieldSupplier = supplier;
	}

	private Supplier<Long> _getCompanyIdSupplier = () -> 0L;
	private Supplier<GetFieldPropertyResponse>
		_getFieldPropertyResponseSupplier = () -> null;
	private Supplier<Long> _getGroupIdSupplier = () -> 0L;
	private Supplier<Locale> _getLocaleSupplier = () -> new Locale("pt", "BR");
	private Supplier<Long> _getUserIdSupplier = () -> 0L;
	private Supplier<Boolean> _isFieldSupplier = () -> false;

}