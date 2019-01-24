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

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.BaseAggregation;
import com.liferay.portal.search.script.Script;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Inácio Nery
 */
@ProviderType
public abstract class BaseBucketAggregation extends BaseAggregation {

	public BaseBucketAggregation(String aggregationName) {
		super(aggregationName);
	}

	public void addBucketOrder(BucketOrder bucketOrder) {
		_bucketOrders.add(bucketOrder);
	}

	public List<BucketOrder> getbucketOrders() {
		return _bucketOrders;
	}

	public String getField() {
		return _field;
	}

	public Object getMissing() {
		return _missing;
	}

	public Script getScript() {
		return _script;
	}

	public void setField(String field) {
		_field = field;
	}

	public void setMissing(Object missing) {
		_missing = missing;
	}

	public void setScript(Script script) {
		_script = script;
	}

	private List<BucketOrder> _bucketOrders = new ArrayList<>();
	private String _field;
	private Object _missing;
	private Script _script;

}