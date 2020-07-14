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

package com.liferay.app.builder.portlet.tab;

import com.liferay.app.builder.model.AppBuilderApp;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Inácio Nery
 */
@ProviderType
public interface AppBuilderAppPortletTab {

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getDataLayoutMap(AppBuilderApp, long)}
	 */
	@Deprecated
	public default List<Long> getDataLayoutIds(
		AppBuilderApp appBuilderApp, long dataRecordId) {

		return Stream.of(
			getDataLayoutMap(appBuilderApp, dataRecordId)
		).map(
			Map::keySet
		).flatMap(
			Set::stream
		).map(
			DDMStructureLayout::getStructureLayoutId
		).collect(
			Collectors.toList()
		);
	}

	public Map<DDMStructureLayout, Boolean> getDataLayoutMap(
		AppBuilderApp appBuilderApp, long dataRecordId);

	public String getEditEntryPoint();

	public String getListEntryPoint();

	public String getViewEntryPoint();

}