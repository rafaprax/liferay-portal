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

package com.liferay.portal.search.elasticsearch6.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.bucket.NestedAggregation;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(service = NestedAggregationTranslator.class)
public class NestedAggregationTranslatorImpl
	implements NestedAggregationTranslator {

	public NestedAggregationBuilder translate(
		NestedAggregation nestedAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		NestedAggregationBuilder nestedAggregationBuilder =
			AggregationBuilders.nested(
				nestedAggregation.getAggregationName(),
				nestedAggregation.getPath());

		for (Aggregation aggregation : nestedAggregation.getAggregations()) {
			BaseAggregationBuilder baseAggregationBuilder =
				aggregationTranslator.translate(aggregation);

			if (baseAggregationBuilder instanceof BaseAggregationBuilder) {
				nestedAggregationBuilder.subAggregation(
					(AggregationBuilder)baseAggregationBuilder);
			}
			else {
				nestedAggregationBuilder.subAggregation(
					(PipelineAggregationBuilder)baseAggregationBuilder);
			}
		}

		return nestedAggregationBuilder;
	}

}