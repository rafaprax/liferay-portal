package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.portal.kernel.exception.PortalException;

public interface DDMFormAdminPermissionCheckerHelper<T> {

	boolean isDisabledManagementBar();

	boolean isShowAddButton();

	boolean isShowCopyButton();

	boolean isShowCopyURLIcon(T model) throws PortalException;

	boolean isShowDeleteIcon(T model) throws PortalException;

	boolean isShowEditIcon(T model) throws PortalException;

	boolean isShowExportIcon(T model) throws PortalException;

	boolean isShowPermissionsIcon(T model) throws PortalException;

	boolean isShowViewEntriesIcon(T model) throws PortalException;

}