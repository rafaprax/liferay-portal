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

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.bucket.BucketOrder;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.ExtendedBounds;
import com.liferay.portal.search.aggregation.bucket.FilterAggregation;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregation;
import com.liferay.portal.search.aggregation.bucket.NestedAggregation;
import com.liferay.portal.search.aggregation.bucket.Range;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.metrics.AvgAggregation;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregation;
import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregation;
import com.liferay.portal.search.aggregation.metrics.GeoBoundsAggregation;
import com.liferay.portal.search.aggregation.metrics.GeoCentroidAggregation;
import com.liferay.portal.search.aggregation.metrics.MaxAggregation;
import com.liferay.portal.search.aggregation.metrics.MinAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesMethod;
import com.liferay.portal.search.aggregation.metrics.ScriptedMetricAggregation;
import com.liferay.portal.search.aggregation.metrics.StatsAggregation;
import com.liferay.portal.search.aggregation.metrics.SumAggregation;
import com.liferay.portal.search.aggregation.metrics.ValueCountAggregation;
import com.liferay.portal.search.aggregation.metrics.WeightedAvgAggregation;
import com.liferay.portal.search.aggregation.pipeline.BucketSortAggregation;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.bucket.BaseBucketAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.bucket.FilterAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.bucket.FiltersAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.bucket.NestedAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics.BaseMetricsAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics.GeoBoundsAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics.GeoCentroidAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics.ScriptedMetricAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics.WeightedAvgAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline.BucketSortAggregationTranslator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregator;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanksAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true, property = "search.engine.impl=Elasticsearch",
	service = {AggregationTranslator.class, AggregationVisitor.class}
)
public class ElasticsearchAggregationVisitor
	implements AggregationTranslator<BaseAggregationBuilder>,
			   AggregationVisitor<BaseAggregationBuilder> {

	@Override
	public BaseAggregationBuilder translate(Aggregation aggregation) {
		return aggregation.accept(this);
	}

	@Override
	public BaseAggregationBuilder visit(AvgAggregation avgAggregation) {
		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.avg(
					baseMetricsAggregation.getAggregationName()),
			avgAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		BucketSortAggregation bucketSortAggregation) {

		return _bucketSortAggregationTranslator.translate(
			bucketSortAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		CardinalityAggregation cardinalityAggregation) {

		CardinalityAggregationBuilder cardinalityAggregationBuilder =
			(CardinalityAggregationBuilder)
				_baseMetricsAggregationTranslator.translate(
					baseMetricsAggregation ->
						AggregationBuilders.cardinality(
							baseMetricsAggregation.getAggregationName()),
					cardinalityAggregation, this);

		if (cardinalityAggregation.getPrecisionThreshold() != null) {
			cardinalityAggregationBuilder.precisionThreshold(
				cardinalityAggregation.getPrecisionThreshold());
		}

		return cardinalityAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(
		DateHistogramAggregation dateHistogramAggregation) {

		DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
			(DateHistogramAggregationBuilder)
				_baseBucketAggregationTranslator.translate(
					baseBucketAggregation ->
						AggregationBuilders.dateHistogram(
							baseBucketAggregation.getAggregationName()),
					dateHistogramAggregation, this);

		if (dateHistogramAggregation.getKeyed() != null) {
			dateHistogramAggregationBuilder.keyed(
				dateHistogramAggregation.getKeyed());
		}

		if (dateHistogramAggregation.getDateHistogramInterval() != null) {
			dateHistogramAggregationBuilder.dateHistogramInterval(
				new DateHistogramInterval(
					dateHistogramAggregation.getDateHistogramInterval()));
		}

		if (dateHistogramAggregation.getExtendedBounds() != null) {
			ExtendedBounds extendedBounds =
				dateHistogramAggregation.getExtendedBounds();

			dateHistogramAggregationBuilder.extendedBounds(
				new org.elasticsearch.search.aggregations.bucket.histogram.
					ExtendedBounds(
						extendedBounds.getMin(), extendedBounds.getMax()));
		}

		if (dateHistogramAggregation.getInterval() != null) {
			dateHistogramAggregationBuilder.interval(
				dateHistogramAggregation.getInterval());
		}

		if (dateHistogramAggregation.getMinDocCount() != null) {
			dateHistogramAggregationBuilder.minDocCount(
				dateHistogramAggregation.getMinDocCount());
		}

		if (dateHistogramAggregation.getOffset() != null) {
			dateHistogramAggregationBuilder.offset(
				dateHistogramAggregation.getOffset());
		}

		List<BucketOrder> bucketOrders =
			dateHistogramAggregation.getbucketOrders();

		if (!bucketOrders.isEmpty()) {
			Stream<BucketOrder> stream = bucketOrders.stream();

			List<org.elasticsearch.search.aggregations.BucketOrder>
				elasticsearchBucketOrders = stream.map(
					bucketOrder -> org.elasticsearch.search.aggregations.
						BucketOrder.aggregation(
							bucketOrder.getPath(), bucketOrder.isAsc())
				).collect(
					Collectors.toList()
				);

			dateHistogramAggregationBuilder.order(elasticsearchBucketOrders);
		}

		return dateHistogramAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(
		ExtendedStatsAggregation extendedStatsAggregation) {

		ExtendedStatsAggregationBuilder extendedStatsAggregationBuilder =
			(ExtendedStatsAggregationBuilder)
				_baseMetricsAggregationTranslator.translate(
					baseMetricsAggregation ->
						AggregationBuilders.extendedStats(
							baseMetricsAggregation.getAggregationName()),
					extendedStatsAggregation, this);

		if (extendedStatsAggregation.getSigma() != null) {
			extendedStatsAggregationBuilder.sigma(
				extendedStatsAggregation.getSigma());
		}

		return extendedStatsAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(FilterAggregation filterAggregation) {
		return _filterAggregationTranslator.translate(filterAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(FiltersAggregation filtersAggregation) {
		return _filtersAggregationTranslator.translate(
			filtersAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		GeoBoundsAggregation geoBoundsAggregation) {

		return _geoBoundsAggregationTranslator.translate(
			geoBoundsAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		GeoCentroidAggregation geoCentroidAggregation) {

		return _geoCentroidAggregationTranslator.translate(
			geoCentroidAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(MaxAggregation maxAggregation) {
		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.max(
					baseMetricsAggregation.getAggregationName()),
			maxAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(MinAggregation minAggregation) {
		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.min(
					baseMetricsAggregation.getAggregationName()),
			minAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(NestedAggregation nestedAggregation) {
		return _nestedAggregationTranslator.translate(nestedAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		final PercentileRanksAggregation percentileRanksAggregation) {

		PercentileRanksAggregationBuilder percentileRanksAggregationBuilder =
			(PercentileRanksAggregationBuilder)
				_baseMetricsAggregationTranslator.translate(
					baseMetricsAggregation ->
						AggregationBuilders.percentileRanks(
							baseMetricsAggregation.getAggregationName(),
							percentileRanksAggregation.getValues()),
					percentileRanksAggregation, this);

		if (percentileRanksAggregation.getCompression() != null) {
			percentileRanksAggregationBuilder.compression(
				percentileRanksAggregation.getCompression());
		}

		if (percentileRanksAggregation.getHdrSignificantValueDigits() != null) {
			percentileRanksAggregationBuilder.numberOfSignificantValueDigits(
				percentileRanksAggregation.getHdrSignificantValueDigits());
		}

		if (percentileRanksAggregation.getKeyed() != null) {
			percentileRanksAggregationBuilder.keyed(
				percentileRanksAggregation.getKeyed());
		}

		if (percentileRanksAggregation.getPercentilesMethod() != null) {
			PercentilesMethod percentilesMethod =
				percentileRanksAggregation.getPercentilesMethod();

			percentileRanksAggregationBuilder.method(
				org.elasticsearch.search.aggregations.metrics.percentiles.
					PercentilesMethod.valueOf(percentilesMethod.name()));
		}

		return percentileRanksAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(
		PercentilesAggregation percentilesAggregation) {

		PercentilesAggregationBuilder percentilesAggregationBuilder =
			(PercentilesAggregationBuilder)
				_baseMetricsAggregationTranslator.translate(
					baseMetricsAggregation ->
						AggregationBuilders.percentiles(
							baseMetricsAggregation.getAggregationName()),
					percentilesAggregation, this);

		if (percentilesAggregation.getCompression() != null) {
			percentilesAggregationBuilder.compression(
				percentilesAggregation.getCompression());
		}

		if (percentilesAggregation.getHdrSignificantValueDigits() != null) {
			percentilesAggregationBuilder.numberOfSignificantValueDigits(
				percentilesAggregation.getHdrSignificantValueDigits());
		}

		if (percentilesAggregation.getKeyed() != null) {
			percentilesAggregationBuilder.keyed(
				percentilesAggregation.getKeyed());
		}

		if (percentilesAggregation.getPercentilesMethod() != null) {
			PercentilesMethod percentilesMethod =
				percentilesAggregation.getPercentilesMethod();

			percentilesAggregationBuilder.method(
				org.elasticsearch.search.aggregations.metrics.percentiles.
					PercentilesMethod.valueOf(percentilesMethod.name()));
		}

		return percentilesAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(RangeAggregation rangeAggregation) {
		RangeAggregationBuilder rangeAggregationBuilder =
			(RangeAggregationBuilder)
				_baseBucketAggregationTranslator.translate(
					baseBucketAggregation ->
						AggregationBuilders.range(
							baseBucketAggregation.getAggregationName()),
					rangeAggregation, this);

		List<Range> ranges = rangeAggregation.getRanges();

		if (!ranges.isEmpty()) {
			ranges.forEach(
				range -> rangeAggregationBuilder.addRange(
					new RangeAggregator.Range(
						range.getKey(), range.getFrom(), range.getTo())));
		}

		return rangeAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(
		ScriptedMetricAggregation scriptedMetricAggregation) {

		return _scriptedMetricAggregationTranslator.translate(
			scriptedMetricAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(StatsAggregation statsAggregation) {
		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.stats(
					baseMetricsAggregation.getAggregationName()),
			statsAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(SumAggregation sumAggregation) {
		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.sum(
					baseMetricsAggregation.getAggregationName()),
			sumAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(TermsAggregation termsAggregation) {
		TermsAggregationBuilder termsAggregationBuilder =
			(TermsAggregationBuilder)
				_baseBucketAggregationTranslator.translate(
					baseBucketAggregation ->
						AggregationBuilders.terms(
							baseBucketAggregation.getAggregationName()),
					termsAggregation, this);

		if (termsAggregation.getSize() != null) {
			termsAggregationBuilder.size(termsAggregation.getSize());
		}

		List<BucketOrder> bucketOrders = termsAggregation.getbucketOrders();

		if (!bucketOrders.isEmpty()) {
			Stream<BucketOrder> stream = bucketOrders.stream();

			List<org.elasticsearch.search.aggregations.BucketOrder>
				elasticsearchBucketOrders = stream.map(
					bucketOrder -> org.elasticsearch.search.aggregations.
						BucketOrder.aggregation(
							bucketOrder.getPath(), bucketOrder.isAsc())
				).collect(
					Collectors.toList()
				);

			termsAggregationBuilder.order(elasticsearchBucketOrders);
		}

		return termsAggregationBuilder;
	}

	@Override
	public BaseAggregationBuilder visit(
		ValueCountAggregation valueCountAggregation) {

		return _baseMetricsAggregationTranslator.translate(
			baseMetricsAggregation ->
				AggregationBuilders.count(
					baseMetricsAggregation.getAggregationName()),
			valueCountAggregation, this);
	}

	@Override
	public BaseAggregationBuilder visit(
		WeightedAvgAggregation weightedAvgAggregation) {

		return _weightedAvgAggregationTranslator.translate(
			weightedAvgAggregation, this);
	}

	private final BaseBucketAggregationTranslator
		_baseBucketAggregationTranslator =
			new BaseBucketAggregationTranslator();
	private final BaseMetricsAggregationTranslator
		_baseMetricsAggregationTranslator =
			new BaseMetricsAggregationTranslator();

	@Reference
	private BucketSortAggregationTranslator _bucketSortAggregationTranslator;

	@Reference
	private FilterAggregationTranslator _filterAggregationTranslator;

	@Reference
	private FiltersAggregationTranslator _filtersAggregationTranslator;

	@Reference
	private GeoBoundsAggregationTranslator _geoBoundsAggregationTranslator;

	@Reference
	private GeoCentroidAggregationTranslator _geoCentroidAggregationTranslator;

	@Reference
	private NestedAggregationTranslator _nestedAggregationTranslator;

	@Reference
	private ScriptedMetricAggregationTranslator
		_scriptedMetricAggregationTranslator;

	@Reference
	private WeightedAvgAggregationTranslator _weightedAvgAggregationTranslator;

}