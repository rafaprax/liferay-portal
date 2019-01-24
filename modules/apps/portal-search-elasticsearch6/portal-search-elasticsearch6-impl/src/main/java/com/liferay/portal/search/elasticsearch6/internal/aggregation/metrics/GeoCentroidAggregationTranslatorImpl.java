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

package com.liferay.portal.search.elasticsearch6.internal.aggregation.metrics;

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.metrics.GeoCentroidAggregation;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidAggregationBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = GeoCentroidAggregationTranslator.class)
public class GeoCentroidAggregationTranslatorImpl
	implements GeoCentroidAggregationTranslator {

	@Override
	public GeoCentroidAggregationBuilder translate(
		GeoCentroidAggregation geoCentroidAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		GeoCentroidAggregationBuilder geoCentroidAggregationBuilder =
			AggregationBuilders.geoCentroid(
				geoCentroidAggregation.getAggregationName());

		geoCentroidAggregationBuilder.field(geoCentroidAggregation.getField());

		for (Aggregation aggregation :
				geoCentroidAggregation.getAggregations()) {

			BaseAggregationBuilder baseAggregationBuilder =
				aggregationTranslator.translate(aggregation);

			if (baseAggregationBuilder instanceof BaseAggregationBuilder) {
				geoCentroidAggregationBuilder.subAggregation(
					(AggregationBuilder)baseAggregationBuilder);
			}
			else {
				geoCentroidAggregationBuilder.subAggregation(
					(PipelineAggregationBuilder)baseAggregationBuilder);
			}
		}

		return geoCentroidAggregationBuilder;
	}

}