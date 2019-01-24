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

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregation;
import com.liferay.portal.search.elasticsearch6.internal.query.ElasticsearchQueryTranslator;

import java.util.List;
import java.util.stream.Stream;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = FiltersAggregationTranslator.class)
public class FiltersAggregationTranslatorImpl
	implements FiltersAggregationTranslator {

	public FiltersAggregationBuilder translate(
		FiltersAggregation filtersAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		List<Query> queries = filtersAggregation.getQueries();

		Stream<Query> stream = queries.stream();

		QueryBuilder[] queryBuilders = stream.map(
			query -> _elasticsearchQueryTranslator.translate(query, null)
		).toArray(
			QueryBuilder[]::new
		);

		FiltersAggregationBuilder filtersAggregationBuilder =
			AggregationBuilders.filters(
				filtersAggregation.getAggregationName(), queryBuilders);

		for (Aggregation aggregation : filtersAggregation.getAggregations()) {
			BaseAggregationBuilder baseAggregationBuilder =
				aggregationTranslator.translate(aggregation);

			if (baseAggregationBuilder instanceof BaseAggregationBuilder) {
				filtersAggregationBuilder.subAggregation(
					(AggregationBuilder)baseAggregationBuilder);
			}
			else {
				filtersAggregationBuilder.subAggregation(
					(PipelineAggregationBuilder)baseAggregationBuilder);
			}
		}

		return filtersAggregationBuilder;
	}

	@Reference
	private ElasticsearchQueryTranslator _elasticsearchQueryTranslator;

}