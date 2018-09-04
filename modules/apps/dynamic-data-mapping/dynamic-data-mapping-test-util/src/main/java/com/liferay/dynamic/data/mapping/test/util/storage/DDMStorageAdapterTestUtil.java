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

package com.liferay.dynamic.data.mapping.test.util.storage;

import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveResponse;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Leonardo Barros
 */
public class DDMStorageAdapterTestUtil {

	public static long create(
			DDMFormValues ddmFormValues, DDMStorageAdapter ddmStorageAdapter,
			DDMStorageLinkLocalService ddmStorageLinkLocalService,
			DDMStructure ddmStructure, Portal portal,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStorageAdapterSaveRequest.Builder builder =
			DDMStorageAdapterSaveRequest.Builder.newBuilder(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				ddmFormValues
			).withClassName(
				DDMContent.class.getName()
			);

		DDMStorageAdapterSaveResponse ddmStorageAdapterSaveResponse =
			ddmStorageAdapter.save(builder.build());

		long storageId = ddmStorageAdapterSaveResponse.getPrimaryKey();

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getLatestStructureVersion();

		ddmStorageLinkLocalService.addStorageLink(
			portal.getClassNameId(DDMContent.class.getName()), storageId,
			ddmStructureVersion.getStructureVersionId(), serviceContext);

		return storageId;
	}

	public static DDMFormValues getDDMFormValues(
			long ddmStorageId, DDMStorageAdapter ddmStorageAdapter,
			DDMStorageLinkLocalService ddmStorageLinkLocalService)
		throws StorageException {

		try {
			DDMStorageLink ddmStorageLink =
				ddmStorageLinkLocalService.getClassStorageLink(ddmStorageId);

			DDMStructure ddmStructure = ddmStorageLink.getStructure();

			return getDDMFormValues(
				ddmStorageLink.getClassPK(), ddmStorageAdapter, ddmStructure);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public static void update(
			long ddmStorageId, DDMFormValues ddmFormValues,
			DDMStorageAdapter ddmStorageAdapter, ServiceContext serviceContext)
		throws StorageException {

		DDMStorageAdapterSaveRequest.Builder builder =
			DDMStorageAdapterSaveRequest.Builder.newBuilder(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				ddmFormValues
			).withPrimaryKey(
				ddmStorageId
			);

		ddmStorageAdapter.save(builder.build());
	}

	protected static DDMFormValues getDDMFormValues(
			long ddmStorageId, DDMStorageAdapter ddmStorageAdapter,
			DDMStructure ddmStructure)
		throws PortalException {

		DDMStorageAdapterGetRequest ddmStorageAdapterGetRequest =
			DDMStorageAdapterGetRequest.Builder.newBuilder(
				ddmStorageId, ddmStructure.getDDMForm()
			).build();

		DDMStorageAdapterGetResponse ddmStorageAdapterGetResponse =
			ddmStorageAdapter.get(ddmStorageAdapterGetRequest);

		return ddmStorageAdapterGetResponse.getDDMFormValues();
	}

}