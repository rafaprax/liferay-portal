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

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.web.internal.display.context.logic.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.logic.FileEntryDisplayContextHelper;
import com.liferay.document.library.web.internal.display.context.logic.FileVersionDisplayContextHelper;
import com.liferay.document.library.web.internal.display.context.logic.UIItemsBuilder;
import com.liferay.document.library.web.internal.display.context.util.DLRequestHelper;
import com.liferay.document.library.web.internal.display.context.util.JSPRenderer;
import com.liferay.document.library.web.internal.util.DLTrashUtil;
import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetRequest;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterGetResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.ToolbarItem;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Adolfo Pérez
 */
public class DefaultDLViewFileVersionDisplayContext
	implements DLViewFileVersionDisplayContext {

	public DefaultDLViewFileVersionDisplayContext(
			HttpServletRequest request, HttpServletResponse response,
			FileShortcut fileShortcut,
			DDMStorageAdapterTracker ddmStorageAdapterTracker,
			DDMStorageLinkLocalService ddmStorageLinkLocalService,
			DDMStructureLinkLocalService ddmStructureLinkLocalService,
			DDMStructureLocalService ddmStructureLocalService,
			DLMimeTypeDisplayContext dlMimeTypeDisplayContext, Portal portal,
			ResourceBundle resourceBundle, DLTrashUtil dlTrashUtil,
			DLPreviewRendererProvider dlPreviewRendererProvider)
		throws PortalException {

		this(
			request, fileShortcut.getFileVersion(), fileShortcut,
			ddmStorageAdapterTracker, ddmStorageLinkLocalService,
			ddmStructureLinkLocalService, ddmStructureLocalService,
			dlMimeTypeDisplayContext, portal, resourceBundle, dlTrashUtil,
			dlPreviewRendererProvider);
	}

	public DefaultDLViewFileVersionDisplayContext(
		HttpServletRequest request, HttpServletResponse response,
		FileVersion fileVersion,
		DDMStorageAdapterTracker ddmStorageAdapterTracker,
		DDMStorageLinkLocalService ddmStorageLinkLocalService,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext, Portal portal,
		ResourceBundle resourceBundle, DLTrashUtil dlTrashUtil,
		DLPreviewRendererProvider dlPreviewRendererProvider) {

		this(
			request, fileVersion, null, ddmStorageAdapterTracker,
			ddmStorageLinkLocalService, ddmStructureLinkLocalService,
			ddmStructureLocalService, dlMimeTypeDisplayContext, portal,
			resourceBundle, dlTrashUtil, dlPreviewRendererProvider);
	}

	@Override
	public String getCssClassFileMimeType() {
		if (_dlMimeTypeDisplayContext == null) {
			return "file-icon-color-0";
		}

		return _dlMimeTypeDisplayContext.getCssClassFileMimeType(
			_fileVersion.getMimeType());
	}

	@Override
	public DDMFormValues getDDMFormValues(DDMStructure ddmStructure)
		throws PortalException {

		DLFileEntryMetadata dlFileEntryMetadata =
			DLFileEntryMetadataLocalServiceUtil.getFileEntryMetadata(
				ddmStructure.getStructureId(), _fileVersion.getFileVersionId());

		return getDDMFormValues(
			dlFileEntryMetadata.getDDMStorageId(), ddmStructure);
	}

	@Override
	public DDMFormValues getDDMFormValues(long classPK)
		throws StorageException {

		try {
			DDMStorageLink ddmStorageLink =
				_ddmStorageLinkLocalService.getClassStorageLink(classPK);

			DDMStructure ddmStructure = ddmStorageLink.getStructure();

			return getDDMFormValues(ddmStorageLink.getClassPK(), ddmStructure);
		}
		catch (StorageException se) {
			throw se;
		}
		catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public List<DDMStructure> getDDMStructures() throws PortalException {
		if (_ddmStructures != null) {
			return _ddmStructures;
		}

		if (_fileVersionDisplayContextHelper.isDLFileVersion()) {
			DLFileVersion dlFileVersion =
				(DLFileVersion)_fileVersion.getModel();

			_ddmStructures = getDDMStructures(
				dlFileVersion.getDLFileEntryType());
		}
		else {
			_ddmStructures = Collections.emptyList();
		}

		return _ddmStructures;
	}

	@Override
	public int getDDMStructuresCount() throws PortalException {
		List<DDMStructure> ddmStructures = getDDMStructures();

		return ddmStructures.size();
	}

	@Override
	public String getDiscussionClassName() {
		return DLFileEntryConstants.getClassName();
	}

	@Override
	public long getDiscussionClassPK() {
		return _fileVersion.getFileEntryId();
	}

	@Override
	public String getDiscussionLabel(Locale locale) {
		return LanguageUtil.get(_resourceBundle, "comments");
	}

	@Override
	public Menu getMenu() throws PortalException {
		Menu menu = new Menu();

		menu.setDirection("left-side");
		menu.setMarkupView("lexicon");
		menu.setMenuItems(_getMenuItems());
		menu.setScroll(false);
		menu.setShowWhenSingleIcon(true);

		return menu;
	}

	@Override
	public List<ToolbarItem> getToolbarItems() throws PortalException {
		List<ToolbarItem> toolbarItems = new ArrayList<>();

		_uiItemsBuilder.addDownloadToolbarItem(toolbarItems);

		_uiItemsBuilder.addOpenInMsOfficeToolbarItem(toolbarItems);

		_uiItemsBuilder.addEditToolbarItem(toolbarItems);

		_uiItemsBuilder.addMoveToolbarItem(toolbarItems);

		_uiItemsBuilder.addCheckoutToolbarItem(toolbarItems);

		_uiItemsBuilder.addCancelCheckoutToolbarItem(toolbarItems);

		_uiItemsBuilder.addCheckinToolbarItem(toolbarItems);

		_uiItemsBuilder.addPermissionsToolbarItem(toolbarItems);

		_uiItemsBuilder.addMoveToTheRecycleBinToolbarItem(toolbarItems);

		_uiItemsBuilder.addDeleteToolbarItem(toolbarItems);

		return toolbarItems;
	}

	@Override
	public UUID getUuid() {
		return _UUID;
	}

	@Override
	public boolean hasCustomThumbnail() {
		if (_dlPreviewRendererProvider != null) {
			Optional<DLPreviewRenderer> dlPreviewRendererOptional =
				_dlPreviewRendererProvider.
					getThumbnailDLPreviewRendererOptional(_fileVersion);

			return dlPreviewRendererOptional.isPresent();
		}

		return false;
	}

	@Override
	public boolean hasPreview() {
		if (_dlPreviewRendererProvider != null) {
			Optional<DLPreviewRenderer> dlPreviewRendererOptional =
				_dlPreviewRendererProvider.getPreviewDLPreviewRendererOptional(
					_fileVersion);

			return dlPreviewRendererOptional.isPresent();
		}

		return false;
	}

	@Override
	public boolean isDownloadLinkVisible() throws PortalException {
		return _fileEntryDisplayContextHelper.isDownloadActionAvailable();
	}

	@Override
	public boolean isVersionInfoVisible() {
		return true;
	}

	@Override
	public void renderCustomThumbnail(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		if (_dlPreviewRendererProvider != null) {
			Optional<DLPreviewRenderer> dlPreviewRendererOptional =
				_dlPreviewRendererProvider.
					getThumbnailDLPreviewRendererOptional(_fileVersion);

			if (dlPreviewRendererOptional.isPresent()) {
				DLPreviewRenderer dlPreviewRenderer =
					dlPreviewRendererOptional.get();

				dlPreviewRenderer.render(request, response);
			}
		}
	}

	@Override
	public void renderPreview(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		if (_dlPreviewRendererProvider != null) {
			Optional<DLPreviewRenderer> dlPreviewRendererOptional =
				_dlPreviewRendererProvider.getPreviewDLPreviewRendererOptional(
					_fileVersion);

			if (dlPreviewRendererOptional.isPresent()) {
				DLPreviewRenderer dlPreviewRenderer =
					dlPreviewRendererOptional.get();

				dlPreviewRenderer.render(request, response);

				return;
			}
		}

		JSPRenderer jspRenderer = new JSPRenderer(
			"/document_library/view_file_entry_preview.jsp");

		jspRenderer.setAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_VERSION, _fileVersion);

		jspRenderer.render(request, response);
	}

	protected DDMFormValues getDDMFormValues(
			long storageId, DDMStructure ddmStructure)
		throws StorageException {

		String storageType = ddmStructure.getStorageType();

		DDMStorageAdapter ddmStorageAdapter =
			_ddmStorageAdapterTracker.getDDMStorageAdapter(storageType);

		DDMStorageAdapterGetRequest ddmStorageAdapterGetRequest =
			DDMStorageAdapterGetRequest.Builder.newBuilder(
				storageId, ddmStructure.getDDMForm()
			).build();

		DDMStorageAdapterGetResponse ddmStorageAdapterGetResponse =
			ddmStorageAdapter.get(ddmStorageAdapterGetRequest);

		return ddmStorageAdapterGetResponse.getDDMFormValues();
	}

	protected List<DDMStructure> getDDMStructures(
		DLFileEntryType dlFileEntryType) {

		List<DDMStructureLink> ddmStructureLinks =
			_ddmStructureLinkLocalService.getStructureLinks(
				_portal.getClassNameId(DLFileEntryType.class),
				dlFileEntryType.getFileEntryTypeId());

		List<DDMStructure> ddmStructures = new ArrayList<>();

		for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					ddmStructureLink.getStructureId());

			if (ddmStructure != null) {
				ddmStructures.add(ddmStructure);
			}
		}

		return ddmStructures;
	}

	private DefaultDLViewFileVersionDisplayContext(
		HttpServletRequest request, FileVersion fileVersion,
		FileShortcut fileShortcut,
		DDMStorageAdapterTracker ddmStorageAdapterTracker,
		DDMStorageLinkLocalService ddmStorageLinkLocalService,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext, Portal portal,
		ResourceBundle resourceBundle, DLTrashUtil dlTrashUtil,
		DLPreviewRendererProvider dlPreviewRendererProvider) {

		try {
			_fileVersion = fileVersion;
			_ddmStorageAdapterTracker = ddmStorageAdapterTracker;
			_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
			_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
			_ddmStructureLocalService = ddmStructureLocalService;
			_dlMimeTypeDisplayContext = dlMimeTypeDisplayContext;
			_portal = portal;
			_resourceBundle = resourceBundle;
			_dlPreviewRendererProvider = dlPreviewRendererProvider;

			DLRequestHelper dlRequestHelper = new DLRequestHelper(request);

			_dlPortletInstanceSettingsHelper =
				new DLPortletInstanceSettingsHelper(dlRequestHelper);

			_fileEntryDisplayContextHelper = new FileEntryDisplayContextHelper(
				dlRequestHelper.getPermissionChecker(),
				_getFileEntry(fileVersion));

			_fileVersionDisplayContextHelper =
				new FileVersionDisplayContextHelper(fileVersion);

			if (fileShortcut == null) {
				_uiItemsBuilder = new UIItemsBuilder(
					request, fileVersion, _resourceBundle, dlTrashUtil);
			}
			else {
				_uiItemsBuilder = new UIItemsBuilder(
					request, fileShortcut, _resourceBundle, dlTrashUtil);
			}
		}
		catch (PortalException pe) {
			throw new SystemException(
				"Unable to build DefaultDLViewFileVersionDisplayContext for " +
					fileVersion,
				pe);
		}
	}

	private FileEntry _getFileEntry(FileVersion fileVersion)
		throws PortalException {

		if (fileVersion != null) {
			return fileVersion.getFileEntry();
		}

		return null;
	}

	private List<MenuItem> _getMenuItems() throws PortalException {
		List<MenuItem> menuItems = new ArrayList<>();

		if (_dlPortletInstanceSettingsHelper.isShowActions()) {
			_uiItemsBuilder.addDownloadMenuItem(menuItems);

			_uiItemsBuilder.addOpenInMsOfficeMenuItem(menuItems);

			_uiItemsBuilder.addViewOriginalFileMenuItem(menuItems);

			_uiItemsBuilder.addEditMenuItem(menuItems);

			_uiItemsBuilder.addMoveMenuItem(menuItems);

			_uiItemsBuilder.addCheckoutMenuItem(menuItems);

			_uiItemsBuilder.addCheckinMenuItem(menuItems);

			_uiItemsBuilder.addCancelCheckoutMenuItem(menuItems);

			_uiItemsBuilder.addPermissionsMenuItem(menuItems);

			_uiItemsBuilder.addDeleteMenuItem(menuItems);

			_uiItemsBuilder.addPublishMenuItem(menuItems, true);
		}

		return menuItems;
	}

	private static final UUID _UUID = UUID.fromString(
		"85F6C50E-3893-4E32-9D63-208528A503FA");

	private final DDMStorageAdapterTracker _ddmStorageAdapterTracker;
	private final DDMStorageLinkLocalService _ddmStorageLinkLocalService;
	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private List<DDMStructure> _ddmStructures;
	private final DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;
	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private DLPreviewRendererProvider _dlPreviewRendererProvider;
	private final FileEntryDisplayContextHelper _fileEntryDisplayContextHelper;
	private final FileVersion _fileVersion;
	private final FileVersionDisplayContextHelper
		_fileVersionDisplayContextHelper;
	private final Portal _portal;
	private final ResourceBundle _resourceBundle;
	private final UIItemsBuilder _uiItemsBuilder;

}