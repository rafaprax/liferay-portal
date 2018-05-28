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

package com.liferay.dynamic.data.mapping.io.internal.exporter;

import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriter;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterTracker;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Leonardo Barros
 */
@Component(immediate = true)
public class DDMFormInstanceRecordWriterTrackerImpl
	implements DDMFormInstanceRecordWriterTracker {

	@Override
	public DDMFormInstanceRecordWriter getDDMFormInstanceRecordWriter(
		String type) {

		return ddmFormInstanceRecordWriterServiceTrackerMap.getService(type);
	}

	@Override
	public Set<String> getDDMFormInstanceRecordWriterTypes() {
		return ddmFormInstanceRecordWriterServiceTrackerMap.keySet();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		ddmFormInstanceRecordWriterServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMFormInstanceRecordWriter.class,
				"ddm.form.instance.record.writer.type");
	}

	@Deactivate
	protected void deactivate() {
		ddmFormInstanceRecordWriterServiceTrackerMap.close();
	}

	protected ServiceTrackerMap<String, DDMFormInstanceRecordWriter>
		ddmFormInstanceRecordWriterServiceTrackerMap;

}