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
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Barros
 */
@PrepareForTest(ServiceTrackerMapFactory.class)
@RunWith(PowerMockRunner.class)
public class DDMFormInstanceRecordWriterTrackerImplTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		mockStatic(ServiceTrackerMapFactory.class);
	}

	@Test
	public void testActivate() {
		DDMFormInstanceRecordWriterTrackerImpl
			ddmFormInstanceRecordWriterTracker =
				new DDMFormInstanceRecordWriterTrackerImpl();

		BundleContext bundleContext = mock(BundleContext.class);

		when(
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMFormInstanceRecordWriter.class,
				"ddm.form.instance.record.writer.type")
		).thenReturn(
			_ddmFormInstanceRecordWriterServiceTrackerMap
		);

		ddmFormInstanceRecordWriterTracker.activate(bundleContext);

		Assert.assertNotNull(
			ddmFormInstanceRecordWriterTracker.
				ddmFormInstanceRecordWriterServiceTrackerMap);
	}

	@Test
	public void testDeactivate() {
		DDMFormInstanceRecordWriterTrackerImpl
			ddmFormInstanceRecordWriterTracker =
				new DDMFormInstanceRecordWriterTrackerImpl();

		ddmFormInstanceRecordWriterTracker.
			ddmFormInstanceRecordWriterServiceTrackerMap =
				_ddmFormInstanceRecordWriterServiceTrackerMap;

		ddmFormInstanceRecordWriterTracker.deactivate();

		Mockito.verify(
			_ddmFormInstanceRecordWriterServiceTrackerMap, Mockito.times(1)
		).close();
	}

	@Test
	public void testGetDDMFormInstanceRecordWriter() {
		DDMFormInstanceRecordWriterTrackerImpl
			ddmFormInstanceRecordWriterTracker =
				new DDMFormInstanceRecordWriterTrackerImpl();

		ddmFormInstanceRecordWriterTracker.
			ddmFormInstanceRecordWriterServiceTrackerMap =
				_ddmFormInstanceRecordWriterServiceTrackerMap;

		ddmFormInstanceRecordWriterTracker.getDDMFormInstanceRecordWriter(
			"json");

		Mockito.verify(
			_ddmFormInstanceRecordWriterServiceTrackerMap, Mockito.times(1)
		).getService(
			"json"
		);
	}

	@Test
	public void testGetDDMFormInstanceRecordWriterTypes() {
		DDMFormInstanceRecordWriterTrackerImpl
			ddmFormInstanceRecordWriterTracker =
				new DDMFormInstanceRecordWriterTrackerImpl();

		ddmFormInstanceRecordWriterTracker.
			ddmFormInstanceRecordWriterServiceTrackerMap =
				_ddmFormInstanceRecordWriterServiceTrackerMap;

		ddmFormInstanceRecordWriterTracker.
			getDDMFormInstanceRecordWriterTypes();

		Mockito.verify(
			_ddmFormInstanceRecordWriterServiceTrackerMap, Mockito.times(1)
		).keySet();
	}

	@Mock
	private ServiceTrackerMap<String, DDMFormInstanceRecordWriter>
		_ddmFormInstanceRecordWriterServiceTrackerMap;

}