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

package com.liferay.dynamic.data.mapping.internal;

import com.liferay.dynamic.data.mapping.storage.StorageEngine;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.dynamicdatamapping.StorageEngineManager;
import com.liferay.portlet.dynamicdatamapping.StorageException;
import com.liferay.portlet.dynamicdatamapping.storage.DDMFormValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true)
public class StorageEngineManagerImpl implements StorageEngineManager {

	@Override
	public long create(
			long companyId, long ddmStructureId, DDMFormValues ddmFormValues,
			ServiceContext serviceContext)
		throws StorageException {

		try {
			return _storageEngine.create(
				companyId, ddmStructureId, ddmFormValues, serviceContext);
		}
		catch (com.liferay.dynamic.data.mapping.exception.StorageException e) {
			throw new StorageException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void deleteByClass(long classPK) throws StorageException {
		try {
			_storageEngine.deleteByClass(classPK);
		}
		catch (com.liferay.dynamic.data.mapping.exception.StorageException e) {
			throw new StorageException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void deleteByDDMStructure(long ddmStructureId)
		throws StorageException {

		try {
			_storageEngine.deleteByDDMStructure(ddmStructureId);
		}
		catch (com.liferay.dynamic.data.mapping.exception.StorageException e) {
			throw new StorageException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMFormValues getDDMFormValues(long classPK)
		throws StorageException {

		try {
			return _storageEngine.getDDMFormValues(classPK);
		}
		catch (com.liferay.dynamic.data.mapping.exception.StorageException e) {
			throw new StorageException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMFormValues getDDMFormValues(
			long ddmStructureId, String fieldNamespace,
			ServiceContext serviceContext)
		throws PortalException {

		return _ddm.getDDMFormValues(
			ddmStructureId, fieldNamespace, serviceContext);
	}

	@Override
	public void update(
			long classPK, DDMFormValues ddmFormValues,
			ServiceContext serviceContext)
		throws StorageException {

		try {
			_storageEngine.update(classPK, ddmFormValues, serviceContext);
		}
		catch (com.liferay.dynamic.data.mapping.exception.StorageException e) {
			throw new StorageException(e.getMessage(), e.getCause());
		}
	}

	@Reference
	protected void setDDM(DDM ddm) {
		_ddm = ddm;
	}

	@Reference
	protected void setStorageEngine(StorageEngine storageEngine) {
		_storageEngine = storageEngine;
	}

	private DDM _ddm;
	private StorageEngine _storageEngine;

}