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

package com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline;

import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.pipeline.BucketSortAggregation;
import com.liferay.portal.search.aggregation.pipeline.FieldSort;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.bucketsort.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(service = BucketSortAggregationTranslator.class)
public class BucketSortAggregationTranslatorImpl
	implements BucketSortAggregationTranslator {

	public BucketSortPipelineAggregationBuilder translate(
		BucketSortAggregation bucketSortAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		List<FieldSortBuilder> fieldSortBuilders = new ArrayList<>();

		for (FieldSort fieldSort : bucketSortAggregation.getFieldSorts()) {
			FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(
				fieldSort.getName());

			SortOrder sortOrder = SortOrder.DESC;

			if (fieldSort.isAsc()) {
				sortOrder = SortOrder.ASC;
			}

			fieldSortBuilder.order(sortOrder);

			fieldSortBuilders.add(fieldSortBuilder);
		}

		BucketSortPipelineAggregationBuilder
			bucketSortPipelineAggregationBuilder =
				PipelineAggregatorBuilders.bucketSort(
					bucketSortAggregation.getAggregationName(),
					fieldSortBuilders);

		if (bucketSortAggregation.getFrom() != null) {
			bucketSortPipelineAggregationBuilder.from(
				bucketSortAggregation.getFrom());
		}

		if (bucketSortAggregation.getSize() != null) {
			bucketSortPipelineAggregationBuilder.size(
				bucketSortAggregation.getSize());
		}

		return bucketSortPipelineAggregationBuilder;
	}

}