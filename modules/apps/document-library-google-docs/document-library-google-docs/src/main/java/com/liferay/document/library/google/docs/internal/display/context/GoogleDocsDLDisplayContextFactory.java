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

package com.liferay.document.library.google.docs.internal.display.context;

import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.google.docs.internal.util.GoogleDocsMetadataHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.Portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	immediate = true, property = "service.ranking:Integer=-100",
	service = DLDisplayContextFactory.class
)
public class GoogleDocsDLDisplayContextFactory
	implements DLDisplayContextFactory {

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest request, HttpServletResponse response,
		DLFileEntryType dlFileEntryType) {

		DDMStructure googleDocsDDMStructure =
			GoogleDocsMetadataHelper.getGoogleDocsDDMStructure(
				ddmStructureLinkLocalService, ddmStructureLocalService,
				dlFileEntryType, portal);

		if (googleDocsDDMStructure != null) {
			return new GoogleDocsDLEditFileEntryDisplayContext(
				parentDLEditFileEntryDisplayContext, request, response,
				dlFileEntryType);
		}

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest request, HttpServletResponse response,
		FileEntry fileEntry) {

		Object model = fileEntry.getModel();

		if (model instanceof DLFileEntry) {
			GoogleDocsMetadataHelper googleDocsMetadataHelper =
				new GoogleDocsMetadataHelper(
					ddmStorageAdapterTracker, ddmStorageLinkLocalService,
					ddmStructureLinkLocalService, ddmStructureLocalService,
					(DLFileEntry)model, dlFileEntryMetadataLocalService,
					portal);

			if (googleDocsMetadataHelper.isGoogleDocs()) {
				return new GoogleDocsDLEditFileEntryDisplayContext(
					parentDLEditFileEntryDisplayContext, request, response,
					fileEntry);
			}
		}

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest request, HttpServletResponse response,
		FileShortcut fileShortcut) {

		try {
			long fileEntryId = fileShortcut.getToFileEntryId();

			FileEntry fileEntry = dlAppService.getFileEntry(fileEntryId);

			FileVersion fileVersion = fileEntry.getFileVersion();

			return getDLViewFileVersionDisplayContext(
				parentDLViewFileVersionDisplayContext, request, response,
				fileVersion);
		}
		catch (PortalException pe) {
			throw new SystemException(
				"Unable to build GoogleDocsDLViewFileVersionDisplayContext " +
					"for shortcut " + fileShortcut.getPrimaryKey(),
				pe);
		}
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest request, HttpServletResponse response,
		FileVersion fileVersion) {

		Object model = fileVersion.getModel();

		if (model instanceof DLFileVersion) {
			GoogleDocsMetadataHelper googleDocsMetadataHelper =
				new GoogleDocsMetadataHelper(
					ddmStorageAdapterTracker, ddmStorageLinkLocalService,
					ddmStructureLinkLocalService, ddmStructureLocalService,
					(DLFileVersion)model, dlFileEntryMetadataLocalService,
					portal);

			if (googleDocsMetadataHelper.isGoogleDocs()) {
				return new GoogleDocsDLViewFileVersionDisplayContext(
					parentDLViewFileVersionDisplayContext, request, response,
					fileVersion, googleDocsMetadataHelper);
			}
		}

		return parentDLViewFileVersionDisplayContext;
	}

	@Reference
	protected DDMStorageAdapterTracker ddmStorageAdapterTracker;

	@Reference
	protected DDMStorageLinkLocalService ddmStorageLinkLocalService;

	@Reference
	protected DDMStructureLinkLocalService ddmStructureLinkLocalService;

	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected DLFileEntryMetadataLocalService dlFileEntryMetadataLocalService;

	@Reference
	protected Portal portal;

}