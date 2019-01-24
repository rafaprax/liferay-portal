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
import com.liferay.portal.search.aggregation.metrics.BaseMetricsAggregation;
import com.liferay.portal.search.elasticsearch6.internal.script.ScriptTranslator;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;

/**
 * @author Michael C. Han
 */
public class BaseMetricsAggregationTranslator {

	public ValuesSourceAggregationBuilder translate(
		ValuesSourceAggregationBuilderFactory
			valuesSourceAggregationBuilderFactory,
		BaseMetricsAggregation baseMetricsAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		ValuesSourceAggregationBuilder valuesSourceAggregationBuilder =
			valuesSourceAggregationBuilderFactory.create(
				baseMetricsAggregation);

		valuesSourceAggregationBuilder.field(baseMetricsAggregation.getField());

		if (baseMetricsAggregation.getMissing() != null) {
			valuesSourceAggregationBuilder.missing(
				baseMetricsAggregation.getMissing());
		}

		if (baseMetricsAggregation.getScript() != null) {
			Script elasticsearchScript = _scriptTranslator.translate(
				baseMetricsAggregation.getScript());

			valuesSourceAggregationBuilder.script(elasticsearchScript);
		}

		for (Aggregation aggregation :
				baseMetricsAggregation.getAggregations()) {

			BaseAggregationBuilder baseAggregationBuilder =
				aggregationTranslator.translate(aggregation);

			if (baseAggregationBuilder instanceof BaseAggregationBuilder) {
				valuesSourceAggregationBuilder.subAggregation(
					(AggregationBuilder)baseAggregationBuilder);
			}
			else {
				valuesSourceAggregationBuilder.subAggregation(
					(PipelineAggregationBuilder)baseAggregationBuilder);
			}
		}

		return valuesSourceAggregationBuilder;
	}

	public interface ValuesSourceAggregationBuilderFactory {

		public ValuesSourceAggregationBuilder create(
			BaseMetricsAggregation baseMetricsAggregation);

	}

	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}