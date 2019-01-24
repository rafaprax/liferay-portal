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

package com.liferay.portal.search.aggregation;

import aQute.bnd.annotation.ProviderType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Michael C. Han
 */
@ProviderType
public abstract class BaseAggregation implements Aggregation {

	public BaseAggregation(String aggregationName) {
		_aggregationName = aggregationName;
	}

	@Override
	public void addAggregation(Aggregation aggregation) {
		_aggregation.add(aggregation);
	}

	@Override
	public void addAggregations(Aggregation... aggregations) {
		Collections.addAll(_aggregation, aggregations);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}

		final BaseAggregation baseAggregation = (BaseAggregation)object;

		return Objects.equals(
			_aggregationName, baseAggregation._aggregationName);
	}

	@Override
	public String getAggregationName() {
		return _aggregationName;
	}

	@Override
	public Collection<Aggregation> getAggregations() {
		return Collections.unmodifiableCollection(_aggregation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_aggregationName);
	}

	private Set<Aggregation> _aggregation = new LinkedHashSet<>();
	private final String _aggregationName;

}