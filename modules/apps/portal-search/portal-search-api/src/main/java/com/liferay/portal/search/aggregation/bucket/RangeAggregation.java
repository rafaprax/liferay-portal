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

package com.liferay.portal.search.aggregation.bucket;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.AggregationVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Inácio Nery
 */
@ProviderType
public class RangeAggregation extends BaseBucketAggregation {

	public RangeAggregation(String aggregationName) {
		super(aggregationName);
	}

	@Override
	public <T> T accept(AggregationVisitor<T> aggregationVisitor) {
		return aggregationVisitor.visit(this);
	}

	public void addRange(Range range) {
		_ranges.add(range);
	}

	public List<Range> getRanges() {
		return _ranges;
	}

	private List<Range> _ranges = new ArrayList<>();

}