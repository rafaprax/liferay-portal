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

package com.liferay.portal.search.elasticsearch6.internal.aggregation;

import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.aggregation.bucket.Bucket;
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
import com.liferay.portal.search.aggregation.metrics.GeoLocationPoint;
import com.liferay.portal.search.aggregation.metrics.MaxAggregationResult;
import com.liferay.portal.search.aggregation.metrics.MinAggregationResult;
import com.liferay.portal.search.aggregation.metrics.Percentile;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBounds;
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroid;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanks;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;

/**
 * @author Inácio Nery
 */
public class ElasticsearchAggregationResult implements AggregationResults {

	public ElasticsearchAggregationResult(Aggregations aggregations) {
		_aggregations = aggregations;
	}

	@Override
	public AvgAggregationResult getAvgAggregationResult(
		String aggregationName) {

		Avg avg = _aggregations.get(aggregationName);

		if (avg == null) {
			return new AvgAggregationResult(0);
		}

		return new AvgAggregationResult(avg.getValue());
	}

	@Override
	public CardinalityAggregationResult getCardinalityAggregationResult(
		String aggregationName) {

		Cardinality cardinality = _aggregations.get(aggregationName);

		if (cardinality == null) {
			return new CardinalityAggregationResult(0);
		}

		return new CardinalityAggregationResult(cardinality.getValue());
	}

	@Override
	public DateHistogramAggregationResult getDateHistogramAggregationResult(
		String aggregationName) {

		Histogram histogram = _aggregations.get(aggregationName);

		if (histogram == null) {
			return new DateHistogramAggregationResult(Collections.emptyList());
		}

		return new DateHistogramAggregationResult(
			getBuckets(histogram.getBuckets()));
	}

	@Override
	public ExtendedStatsAggregationResult getExtendedStatsAggregationResult(
		String aggregationName) {

		ExtendedStats extendedStats = _aggregations.get(aggregationName);

		if (extendedStats == null) {
			return new ExtendedStatsAggregationResult(0, 0, 0, 0, 0, 0, 0, 0);
		}

		return new ExtendedStatsAggregationResult(
			extendedStats.getAvg(), extendedStats.getCount(),
			extendedStats.getMax(), extendedStats.getMin(),
			extendedStats.getStdDeviation(), extendedStats.getSum(),
			extendedStats.getSumOfSquares(), extendedStats.getVariance());
	}

	@Override
	public FilterAggregationResult getFilterAggregationResult(
		String aggregationName) {

		Filter filter = _aggregations.get(aggregationName);

		if (filter == null) {
			return new FilterAggregationResult(null, 0);
		}

		return new FilterAggregationResult(
			new ElasticsearchAggregationResult(filter.getAggregations()),
			filter.getDocCount());
	}

	@Override
	public FiltersAggregationResult getFiltersAggregationResult(
		String aggregationName) {

		Filters filters = _aggregations.get(aggregationName);

		if (filters == null) {
			return new FiltersAggregationResult(Collections.emptyList());
		}

		return new FiltersAggregationResult(getBuckets(filters.getBuckets()));
	}

	@Override
	public GeoBoundsAggregationResult getGeoBoundsAggregationResult(
		String aggregationName) {

		GeoBounds geoBounds = _aggregations.get(aggregationName);

		if (geoBounds == null) {
			return new GeoBoundsAggregationResult(null, null);
		}

		GeoPoint bottomRight = geoBounds.bottomRight();

		GeoPoint topLeft = geoBounds.topLeft();

		return new GeoBoundsAggregationResult(
			new GeoLocationPoint(bottomRight.getLat(), bottomRight.getLon()),
			new GeoLocationPoint(topLeft.getLat(), topLeft.getLon()));
	}

	@Override
	public GeoCentroidAggregationResult getGeoCentroidAggregationResult(
		String aggregationName) {

		GeoCentroid geoCentroid = _aggregations.get(aggregationName);

		if (geoCentroid == null) {
			return new GeoCentroidAggregationResult(null, 0);
		}

		GeoPoint centroid = geoCentroid.centroid();

		return new GeoCentroidAggregationResult(
			new GeoLocationPoint(centroid.getLat(), centroid.getLon()),
			geoCentroid.count());
	}

	@Override
	public MaxAggregationResult getMaxAggregationResult(
		String aggregationName) {

		Max max = _aggregations.get(aggregationName);

		if (max == null) {
			return new MaxAggregationResult(0);
		}

		return new MaxAggregationResult(max.getValue());
	}

	@Override
	public MinAggregationResult getMinAggregationResult(
		String aggregationName) {

		Min min = _aggregations.get(aggregationName);

		if (min == null) {
			return new MinAggregationResult(0);
		}

		return new MinAggregationResult(min.getValue());
	}

	@Override
	public NestedAggregationResult getNestedAggregationResult(
		String aggregationName) {

		Nested nested = _aggregations.get(aggregationName);

		if (nested == null) {
			return new NestedAggregationResult(null, 0);
		}

		return new NestedAggregationResult(
			new ElasticsearchAggregationResult(nested.getAggregations()),
			nested.getDocCount());
	}

	@Override
	public PercentileRanksAggregationResult getPercentileRanksAggregationResult(
		String aggregationName) {

		PercentileRanks percentileRanks = _aggregations.get(aggregationName);

		if (percentileRanks == null) {
			return new PercentileRanksAggregationResult(
				Collections.emptyList());
		}

		List<Percentile> percentiles = new ArrayList<>();

		percentileRanks.forEach(
			percentile -> percentiles.add(
				new Percentile(
					percentile.getPercent(), percentile.getValue())));

		return new PercentileRanksAggregationResult(percentiles);
	}

	@Override
	public RangeAggregationResult getRangeAggregationResult(
		String aggregationName) {

		Range range = _aggregations.get(aggregationName);

		if (range == null) {
			return new RangeAggregationResult(Collections.emptyList());
		}

		return new RangeAggregationResult(getBuckets(range.getBuckets()));
	}

	@Override
	public TermsAggregationResult getTermsAggregationResult(
		String aggregationName) {

		Terms terms = _aggregations.get(aggregationName);

		if (terms == null) {
			return new TermsAggregationResult(Collections.emptyList());
		}

		return new TermsAggregationResult(getBuckets(terms.getBuckets()));
	}

	protected List<Bucket> getBuckets(
		List<? extends MultiBucketsAggregation.Bucket> buckets) {

		Stream<? extends MultiBucketsAggregation.Bucket> stream =
			buckets.stream();

		return stream.map(
			bucket -> new Bucket(
				new ElasticsearchAggregationResult(bucket.getAggregations()),
				bucket.getDocCount(), bucket.getKeyAsString())
		).collect(
			Collectors.toList()
		);
	}

	private final Aggregations _aggregations;

}