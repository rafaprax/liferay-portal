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

package com.liferay.portal.search.aggregation.metrics;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Inácio Nery
 */
@ProviderType
public class ExtendedStatsAggregationResult {

	public ExtendedStatsAggregationResult(
		double avg, long count, double max, double min, double stdDeviation,
		double sum, double sumOfSquares, double variance) {

		_avg = avg;
		_count = count;
		_max = max;
		_min = min;
		_stdDeviation = stdDeviation;
		_sum = sum;
		_sumOfSquares = sumOfSquares;
		_variance = variance;
	}

	public double getAvg() {
		return _avg;
	}

	public long getCount() {
		return _count;
	}

	public double getMax() {
		return _max;
	}

	public double getMin() {
		return _min;
	}

	public double getStdDeviation() {
		return _stdDeviation;
	}

	public double getSum() {
		return _sum;
	}

	public double getSumOfSquares() {
		return _sumOfSquares;
	}

	public double getVariance() {
		return _variance;
	}

	private final double _avg;
	private final long _count;
	private final double _max;
	private final double _min;
	private final double _stdDeviation;
	private final double _sum;
	private final double _sumOfSquares;
	private final double _variance;

}