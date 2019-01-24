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
import com.liferay.portal.search.aggregation.bucket.BaseBucketAggregation;
import com.liferay.portal.search.elasticsearch6.internal.script.ScriptTranslator;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;

/**
 * @author Inácio Nery
 */
public class BaseBucketAggregationTranslator {

	public ValuesSourceAggregationBuilder translate(
		ValuesSourceAggregationBuilderFactory
			valuesSourceAggregationBuilderFactory,
		BaseBucketAggregation baseBucketAggregation,
		AggregationTranslator<BaseAggregationBuilder> aggregationTranslator) {

		ValuesSourceAggregationBuilder valuesSourceAggregationBuilder =
			valuesSourceAggregationBuilderFactory.create(baseBucketAggregation);

		valuesSourceAggregationBuilder.field(baseBucketAggregation.getField());

		if (baseBucketAggregation.getMissing() != null) {
			valuesSourceAggregationBuilder.missing(
				baseBucketAggregation.getMissing());
		}

		if (baseBucketAggregation.getScript() != null) {
			Script elasticsearchScript = _scriptTranslator.translate(
				baseBucketAggregation.getScript());

			valuesSourceAggregationBuilder.script(elasticsearchScript);
		}

		for (Aggregation aggregation :
				baseBucketAggregation.getAggregations()) {

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
			BaseBucketAggregation baseBucketAggregation);

	}

	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}