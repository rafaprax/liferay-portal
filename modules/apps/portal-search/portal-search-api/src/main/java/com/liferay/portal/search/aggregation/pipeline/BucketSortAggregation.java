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

package com.liferay.portal.search.aggregation.pipeline;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.BaseAggregation;

import java.util.List;

/**
 * @author Inácio Nery
 */
@ProviderType
public class BucketSortAggregation extends BaseAggregation {

	public BucketSortAggregation(String aggregationName) {
		super(aggregationName);
	}

	@Override
	public <T> T accept(AggregationVisitor<T> aggregationVisitor) {
		return aggregationVisitor.visit(this);
	}

	public void addFieldSort(FieldSort fieldSort) {
		_fieldSorts.add(fieldSort);
	}

	public List<FieldSort> getFieldSorts() {
		return _fieldSorts;
	}

	public Integer getFrom() {
		return _from;
	}

	public Integer getSize() {
		return _size;
	}

	public void setFrom(Integer from) {
		_from = from;
	}

	public void setSize(Integer size) {
		_size = size;
	}

	private List<FieldSort> _fieldSorts;
	private Integer _from;
	private Integer _size;

}