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

package com.liferay.portal.search.aggregation.bucket;

/**
 * @author Inácio Nery
 */
public class ExtendedBounds {

	public ExtendedBounds(Long max, Long min) {
		_max = max;
		_min = min;
	}

	public Long getMax() {
		return _max;
	}

	public Long getMin() {
		return _min;
	}

	private final Long _max;
	private final Long _min;

}