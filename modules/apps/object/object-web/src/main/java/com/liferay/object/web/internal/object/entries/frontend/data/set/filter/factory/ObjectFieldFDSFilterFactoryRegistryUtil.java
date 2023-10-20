/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldFDSFilterFactoryRegistryUtil {

	public static ObjectFieldFDSFilterFactory getObjectFieldFDSFilterFactory(
			long objectDefinitionId,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectViewFilterColumn objectViewFilterColumn)
		throws PortalException {

		if (Validator.isNotNull(objectViewFilterColumn.getFilterType())) {
			return _objectFieldFilterTypeKeyServiceTrackerMap.getService(
				objectViewFilterColumn.getFilterType());
		}

		if (Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "dateCreated") ||
			Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "dateModified")) {

			return _objectFieldBusinessTypeKeyServiceTrackerMap.getService(
				ObjectFieldConstants.BUSINESS_TYPE_DATE);
		}

		if (Objects.equals(
				objectViewFilterColumn.getObjectFieldName(), "status")) {

			return _objectFieldBusinessTypeKeyServiceTrackerMap.getService(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST);
		}

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectDefinitionId, objectViewFilterColumn.getObjectFieldName());

		return _objectFieldBusinessTypeKeyServiceTrackerMap.getService(
			objectField.getBusinessType());
	}

	private static final ServiceTrackerMap<String, ObjectFieldFDSFilterFactory>
		_objectFieldBusinessTypeKeyServiceTrackerMap;
	private static final ServiceTrackerMap<String, ObjectFieldFDSFilterFactory>
		_objectFieldFilterTypeKeyServiceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ObjectFieldFDSFilterFactoryRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_objectFieldBusinessTypeKeyServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ObjectFieldFDSFilterFactory.class,
				"object.field.business.type.key");
		_objectFieldFilterTypeKeyServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ObjectFieldFDSFilterFactory.class,
				"object.field.filter.type.key");
	}

}