/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.web.internal.constants.UADWebKeys;
import com.liferay.user.associated.data.web.internal.display.UADApplicationExportDisplay;
import com.liferay.user.associated.data.web.internal.export.background.task.UADExportBackgroundTaskManagerUtil;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"javax.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/add_uad_export_processes"
	},
	service = MVCRenderCommand.class
)
public class AddUADExportProcessesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			User selectedUser = _portal.getSelectedUser(renderRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			UADApplicationExport uadApplicationExport =
				new UADApplicationExport();

			renderRequest.setAttribute(
				UADWebKeys.UAD_APPLICATION_EXPORT_DISPLAY_LIST,
				uadApplicationExport.getUADApplicationExportDisplays(
					themeDisplay.getScopeGroupId(), selectedUser.getUserId()));
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return "/add_uad_export_processes.jsp";
	}

	@Reference
	private Portal _portal;

	@Reference
	private UADRegistry _uadRegistry;

	private class UADApplicationExport {

		public Date getApplicationLastExportDate(
			String applicationKey, long groupId, long userId) {

			BackgroundTask backgroundTask =
				UADExportBackgroundTaskManagerUtil.fetchLastBackgroundTask(
					applicationKey, groupId, userId,
					BackgroundTaskConstants.STATUS_SUCCESSFUL);

			if (backgroundTask != null) {
				return backgroundTask.getCompletionDate();
			}

			return null;
		}

		public UADApplicationExportDisplay getUADApplicationExportDisplay(
			String applicationKey, long groupId, long userId) {

			List<UADExporter<?>> uadExporters = new ArrayList<>();

			for (UADDisplay<?> uadDisplay :
					_uadRegistry.getApplicationUADDisplays(applicationKey)) {

				uadExporters.add(
					_uadRegistry.getUADExporter(uadDisplay.getTypeKey()));
			}

			int applicationDataCount = 0;

			for (UADExporter<?> uadExporter : uadExporters) {
				try {
					applicationDataCount += (int)uadExporter.count(userId);
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			return new UADApplicationExportDisplay(
				applicationKey, applicationDataCount, !uadExporters.isEmpty(),
				getApplicationLastExportDate(applicationKey, groupId, userId));
		}

		public List<UADApplicationExportDisplay>
			getUADApplicationExportDisplays(long groupId, long userId) {

			List<UADApplicationExportDisplay> uadApplicationExportDisplays =
				new ArrayList<>();

			for (String applicationKey :
					_uadRegistry.getApplicationUADDisplaysKeySet()) {

				uadApplicationExportDisplays.add(
					getUADApplicationExportDisplay(
						applicationKey, groupId, userId));
			}

			uadApplicationExportDisplays.sort(
				Comparator.comparing(
					UADApplicationExportDisplay::getApplicationKey));

			return uadApplicationExportDisplays;
		}

		private final Log _log = LogFactoryUtil.getLog(
			UADApplicationExport.class);

	}

}