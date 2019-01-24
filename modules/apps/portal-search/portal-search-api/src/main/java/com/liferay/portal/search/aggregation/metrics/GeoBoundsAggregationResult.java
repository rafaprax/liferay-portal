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
public class GeoBoundsAggregationResult {

	public GeoBoundsAggregationResult(
		GeoLocationPoint bottomRight, GeoLocationPoint topLeft) {

		_bottomRight = bottomRight;
		_topLeft = topLeft;
	}

	public GeoLocationPoint getBottomRight() {
		return _bottomRight;
	}

	public GeoLocationPoint getTopLeft() {
		return _topLeft;
	}

	private final GeoLocationPoint _bottomRight;
	private final GeoLocationPoint _topLeft;

}