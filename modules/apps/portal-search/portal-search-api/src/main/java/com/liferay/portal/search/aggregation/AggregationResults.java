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

import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregationResult;
import com.liferay.portal.search.aggregation.bucket.FilterAggregationResult;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregationResult;
import com.liferay.portal.search.aggregation.bucket.NestedAggregationResult;
import com.liferay.portal.search.aggregation.bucket.RangeAggregationResult;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.AvgAggregationResult;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregationResult;
import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.GeoBoundsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.GeoCentroidAggregationResult;
import com.liferay.portal.search.aggregation.metrics.MaxAggregationResult;
import com.liferay.portal.search.aggregation.metrics.MinAggregationResult;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregationResult;

/**
 * @author Inácio Nery
 */
@ProviderType
public interface AggregationResults {

	public AvgAggregationResult getAvgAggregationResult(String aggregationName);

	public CardinalityAggregationResult getCardinalityAggregationResult(
		String aggregationName);

	public DateHistogramAggregationResult getDateHistogramAggregationResult(
		String aggregationName);

	public ExtendedStatsAggregationResult getExtendedStatsAggregationResult(
		String aggregationName);

	public FilterAggregationResult getFilterAggregationResult(
		String aggregationName);

	public FiltersAggregationResult getFiltersAggregationResult(
		String aggregationName);

	public GeoBoundsAggregationResult getGeoBoundsAggregationResult(
		String aggregationName);

	public GeoCentroidAggregationResult getGeoCentroidAggregationResult(
		String aggregationName);

	public MaxAggregationResult getMaxAggregationResult(String aggregationName);

	public MinAggregationResult getMinAggregationResult(String aggregationName);

	public NestedAggregationResult getNestedAggregationResult(
		String aggregationName);

	public PercentileRanksAggregationResult getPercentileRanksAggregationResult(
		String aggregationName);

	public RangeAggregationResult getRangeAggregationResult(
		String aggregationName);

	public TermsAggregationResult getTermsAggregationResult(
		String aggregationName);

}