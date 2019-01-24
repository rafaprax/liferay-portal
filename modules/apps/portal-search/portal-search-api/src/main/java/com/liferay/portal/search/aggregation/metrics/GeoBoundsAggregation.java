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

package com.liferay.portal.search.aggregation.metrics;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.BaseAggregation;

/**
 * @author Michael C. Han
 */
@ProviderType
public class GeoBoundsAggregation extends BaseAggregation {

	public GeoBoundsAggregation(String aggregationName, String field) {
		super(aggregationName);

		_field = field;
	}

	@Override
	public <T> T accept(AggregationVisitor<T> aggregationVisitor) {
		return aggregationVisitor.visit(this);
	}

	public String getField() {
		return _field;
	}

	public Boolean getWrapLongitude() {
		return _wrapLongitude;
	}

	public void setWrapLongitude(Boolean wrapLongitude) {
		_wrapLongitude = wrapLongitude;
	}

	private final String _field;
	private Boolean _wrapLongitude;

}