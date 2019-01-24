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
import com.liferay.portal.search.aggregation.metrics.WeightedAvgAggregation;
import com.liferay.portal.search.elasticsearch6.internal.script.ScriptTranslator;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.weighted_avg.WeightedAvgAggregationBuilder;
import org.elasticsearch.search.aggregations.support.MultiValuesSourceFieldConfig;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(service = WeightedAvgAggregationTranslator.class)
public class WeightedAvgAggregationTranslatorImpl
	implements WeightedAvgAggregationTranslator {

	@Override
	public WeightedAvgAggregationBuilder translate(
		WeightedAvgAggregation weightedAvgAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		WeightedAvgAggregationBuilder weightedAvgAggregationBuilder =
			AggregationBuilders.weightedAvg(
				weightedAvgAggregation.getAggregationName());

		MultiValuesSourceFieldConfig.Builder valueBuilder =
			new MultiValuesSourceFieldConfig.Builder();

		if (weightedAvgAggregation.getValueField() != null) {
			valueBuilder.setFieldName(weightedAvgAggregation.getValueField());
		}

		if (weightedAvgAggregation.getValueMissing() != null) {
			valueBuilder.setMissing(weightedAvgAggregation.getValueMissing());
		}

		if (weightedAvgAggregation.getValueScript() != null) {
			Script elasticsearchgetValueScript = _scriptTranslator.translate(
				weightedAvgAggregation.getValueScript());

			valueBuilder.setScript(elasticsearchgetValueScript);
		}

		weightedAvgAggregationBuilder.value(valueBuilder.build());

		MultiValuesSourceFieldConfig.Builder weightBuilder =
			new MultiValuesSourceFieldConfig.Builder();

		if (weightedAvgAggregation.getWeightField() != null) {
			weightBuilder.setFieldName(weightedAvgAggregation.getWeightField());
		}

		if (weightedAvgAggregation.getWeightMissing() != null) {
			weightBuilder.setMissing(weightedAvgAggregation.getWeightMissing());
		}

		if (weightedAvgAggregation.getWeightScript() != null) {
			Script elasticsearchgetWeightScript = _scriptTranslator.translate(
				weightedAvgAggregation.getWeightScript());

			weightBuilder.setScript(elasticsearchgetWeightScript);
		}

		weightedAvgAggregationBuilder.weight(weightBuilder.build());

		for (Aggregation aggregation :
				weightedAvgAggregation.getAggregations()) {

			BaseAggregationBuilder baseAggregationBuilder =
				aggregationTranslator.translate(aggregation);

			if (baseAggregationBuilder instanceof BaseAggregationBuilder) {
				weightedAvgAggregationBuilder.subAggregation(
					(AggregationBuilder)baseAggregationBuilder);
			}
			else {
				weightedAvgAggregationBuilder.subAggregation(
					(PipelineAggregationBuilder)baseAggregationBuilder);
			}
		}

		return weightedAvgAggregationBuilder;
	}

	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}