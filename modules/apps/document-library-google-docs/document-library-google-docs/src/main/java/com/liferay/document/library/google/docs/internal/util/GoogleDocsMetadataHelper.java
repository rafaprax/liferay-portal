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

package com.liferay.document.library.google.docs.internal.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterSaveResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class GoogleDocsMetadataHelper {

	public static DDMStructure getGoogleDocsDDMStructure(
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLFileEntryType dlFileEntryType, Portal portal) {

		List<DDMStructureLink> ddmStructureLinks =
			ddmStructureLinkLocalService.getStructureLinks(
				portal.getClassNameId(DLFileEntryType.class),
				dlFileEntryType.getFileEntryTypeId());

		List<DDMStructure> ddmStructures = new ArrayList<>();

		for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
			DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
				ddmStructureLink.getStructureId());

			if (ddmStructure != null) {
				ddmStructures.add(ddmStructure);
			}
		}

		for (DDMStructure ddmStructure : ddmStructures) {
			String structureKey = ddmStructure.getStructureKey();

			if (structureKey.equals(
					GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS)) {

				return ddmStructure;
			}
		}

		return null;
	}

	public GoogleDocsMetadataHelper(
		DDMStorageAdapterTracker ddmStorageAdapterTracker,
		DDMStorageLinkLocalService ddmStorageLinkLocalService,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLFileEntry dlFileEntry,
		DLFileEntryMetadataLocalService dlFileEntryMetadataLocalService,
		Portal portal) {

		try {
			_ddmStorageAdapterTracker = ddmStorageAdapterTracker;
			_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
			_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
			_ddmStructureLocalService = ddmStructureLocalService;
			_dlFileEntryMetadataLocalService = dlFileEntryMetadataLocalService;
			_portal = portal;

			_dlFileVersion = dlFileEntry.getFileVersion();
			_ddmStructure = getGoogleDocsDDMStructure(
				_ddmStructureLinkLocalService, _ddmStructureLocalService,
				dlFileEntry.getDLFileEntryType(), portal);
		}
		catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	public GoogleDocsMetadataHelper(
		DDMStorageAdapterTracker ddmStorageAdapterTracker,
		DDMStorageLinkLocalService ddmStorageLinkLocalService,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLFileVersion dlFileVersion,
		DLFileEntryMetadataLocalService dlFileEntryMetadataLocalService,
		Portal portal) {

		_ddmStorageAdapterTracker = ddmStorageAdapterTracker;
		_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_dlFileVersion = dlFileVersion;
		_dlFileEntryMetadataLocalService = dlFileEntryMetadataLocalService;
		_portal = portal;

		try {
			_ddmStructure = getGoogleDocsDDMStructure(
				_ddmStructureLinkLocalService, _ddmStructureLocalService,
				dlFileVersion.getDLFileEntryType(), portal);
		}
		catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	public boolean containsField(String fieldName) {
		initDLFileEntryMetadataAndFields();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			_ddmFormValues.getDDMFormFieldValuesMap();

		return ddmFormFieldValuesMap.containsKey(fieldName);
	}

	public String getFieldValue(String fieldName) {
		if (!containsField(fieldName)) {
			throw new IllegalArgumentException("Unknown field " + fieldName);
		}

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			_ddmFormValues.getDDMFormFieldValuesMap();

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			fieldName);

		DDMFormFieldValue firstDDMFormFieldValue = ddmFormFieldValues.get(0);

		Value value = firstDDMFormFieldValue.getValue();

		if (value instanceof UnlocalizedValue) {
			UnlocalizedValue unlocalizedValue = (UnlocalizedValue)value;

			Locale defaultLocale = unlocalizedValue.getDefaultLocale();

			return unlocalizedValue.getString(defaultLocale);
		}

		return null;
	}

	public boolean isGoogleDocs() {
		if (_ddmStructure != null) {
			return true;
		}

		return false;
	}

	protected void addGoogleDocsDLFileEntryMetadata() {
		try {
			DLFileEntry dlFileEntry = _dlFileVersion.getFileEntry();

			_dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.createDLFileEntryMetadata(
					CounterLocalServiceUtil.increment());

			long ddmStructureId = _ddmStructure.getStructureId();

			DDMForm ddmForm = _ddmStructure.getDDMForm();

			_ddmFormValues = new DDMFormValues(ddmForm);

			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_DESCRIPTION,
					new UnlocalizedValue("")));
			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_EMBEDDABLE_URL,
					new UnlocalizedValue("")));
			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_ICON_URL,
					new UnlocalizedValue("")));
			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_ID,
					new UnlocalizedValue("")));
			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_NAME,
					new UnlocalizedValue("")));
			_ddmFormValues.addDDMFormFieldValue(
				createDDMFormFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_URL,
					new UnlocalizedValue("")));

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);
			serviceContext.setScopeGroupId(_dlFileVersion.getGroupId());
			serviceContext.setUserId(_dlFileVersion.getUserId());

			long ddmStorageId = createDLFileEntryMetadata(
				_ddmFormValues, _ddmStructure, serviceContext);

			_dlFileEntryMetadata.setDDMStorageId(ddmStorageId);

			_dlFileEntryMetadata.setDDMStructureId(ddmStructureId);
			_dlFileEntryMetadata.setFileEntryId(dlFileEntry.getFileEntryId());
			_dlFileEntryMetadata.setFileVersionId(
				_dlFileVersion.getFileVersionId());

			_dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.addDLFileEntryMetadata(
					_dlFileEntryMetadata);
		}
		catch (PortalException pe) {
			throw new SystemException(
				"Unable to add DDM fields for file version " +
					_dlFileVersion.getFileVersionId(),
				pe);
		}
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String name, Value value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);
		ddmFormFieldValue.setValue(value);

		return ddmFormFieldValue;
	}

	protected long createDLFileEntryMetadata(
			DDMFormValues ddmFormValues, DDMStructure ddmStructure,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStorageAdapter ddmStorageAdapter =
			_ddmStorageAdapterTracker.getDDMStorageAdapter(
				ddmStructure.getStorageType());

		DDMStorageAdapterSaveRequest ddmStorageAdapterSaveRequest =
			DDMStorageAdapterSaveRequest.Builder.newBuilder(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				ddmFormValues
			).withClassName(
				DDMContent.class.getName()
			).build();

		DDMStorageAdapterSaveResponse ddmStorageAdapterSaveResponse =
			ddmStorageAdapter.save(ddmStorageAdapterSaveRequest);

		long ddmContentId = ddmStorageAdapterSaveResponse.getPrimaryKey();

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getLatestStructureVersion();

		long classNameId = _portal.getClassNameId(DDMContent.class.getName());

		_ddmStorageLinkLocalService.addStorageLink(
			classNameId, ddmContentId,
			ddmStructureVersion.getStructureVersionId(), serviceContext);

		return ddmContentId;
	}

	protected void initDLFileEntryMetadataAndFields() {
		if (_ddmFormValues != null) {
			return;
		}

		if (_dlFileVersion == null) {
			return;
		}

		_dlFileEntryMetadata =
			_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
				_ddmStructure.getStructureId(),
				_dlFileVersion.getFileVersionId());

		if (_dlFileEntryMetadata == null) {
			addGoogleDocsDLFileEntryMetadata();
		}

		try {
			DDMStorageAdapter ddmStorageAdapter =
				_ddmStorageAdapterTracker.getDDMStorageAdapter(
					_ddmStructure.getStorageType());

			DDMStorageAdapterGetRequest ddmStorageAdapterGetRequest =
				DDMStorageAdapterGetRequest.Builder.newBuilder(
					_dlFileEntryMetadata.getDDMStorageId(),
					_ddmStructure.getDDMForm()
				).build();

			DDMStorageAdapterGetResponse ddmStorageAdapterGetResponse =
				ddmStorageAdapter.get(ddmStorageAdapterGetRequest);

			_ddmFormValues = ddmStorageAdapterGetResponse.getDDMFormValues();
		}
		catch (PortalException pe) {
			throw new SystemException(
				"Unable to load DDM fields for file version " +
					_dlFileVersion.getFileVersionId(),
				pe);
		}
	}

	private DDMFormValues _ddmFormValues;
	private final DDMStorageAdapterTracker _ddmStorageAdapterTracker;
	private final DDMStorageLinkLocalService _ddmStorageLinkLocalService;
	private final DDMStructure _ddmStructure;
	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private DLFileEntryMetadata _dlFileEntryMetadata;
	private final DLFileEntryMetadataLocalService
		_dlFileEntryMetadataLocalService;
	private DLFileVersion _dlFileVersion;
	private final Portal _portal;

}