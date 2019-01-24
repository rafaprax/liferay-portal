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

import com.liferay.portal.search.aggregation.AggregationResults;

/**
 * @author Inácio Nery
 */
public class Bucket {

	public Bucket(
		AggregationResults aggregationResults, long count, String key) {

		_aggregationResults = aggregationResults;
		_count = count;
		_key = key;
	}

	public AggregationResults getAggregationResults() {
		return _aggregationResults;
	}

	public long getCount() {
		return _count;
	}

	public String getKey() {
		return _key;
	}

	private final AggregationResults _aggregationResults;
	private final long _count;
	private final String _key;

}