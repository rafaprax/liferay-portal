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

package com.liferay.app.builder.web.internal.deploy;

import com.liferay.app.builder.constants.AppBuilderPortletKeys;
import com.liferay.app.builder.deploy.AppDeployer;
import com.liferay.app.builder.model.AppBuilderApp;
import com.liferay.app.builder.web.internal.portlet.AppPortlet;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.BasePortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	immediate = true, property = "app.builder.deploy.type=widget",
	service = AppDeployer.class
)
public class WidgetAppDeployer extends BaseAppDeployer {

	@Override
	public void deploy(long appId) throws Exception {
		AppBuilderApp appBuilderApp =
			appBuilderAppLocalService.getAppBuilderApp(appId);

		appBuilderApp.setActive(true);

		_serviceRegistrationsMap.computeIfAbsent(
			appId,
			key -> new ServiceRegistration<?>[] {
				_deployPortlet(
					appBuilderApp, _getAppName(appBuilderApp, null),
					_getPortletName(appId, null), true, true),
				_deployPortlet(
					appBuilderApp, _getAppName(appBuilderApp, "Form View"),
					_getPortletName(appId, "form_view"), true, false),
				_deployPortlet(
					appBuilderApp, _getAppName(appBuilderApp, "Table View"),
					_getPortletName(appId, "table_view"), false, true)
			});

		appBuilderAppLocalService.updateAppBuilderApp(appBuilderApp);
	}

	@Override
	public String getAppPortletURL(
			long appId, long groupId, HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletName = _getPortletName(appId, null);

		Layout layout = _findWidgetAppPortletLayout(
			groupId, portletName, themeDisplay);

		if (layout != null) {
			PortletURL portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletName, layout,
				PortletRequest.RENDER_PHASE);

			return portletURL.toString();
		}

		try {
			PortletLayoutFinder portletLayoutFinder = getPortletLayoutFinder(
				appId);

			PortletLayoutFinder.Result result = portletLayoutFinder.find(
				themeDisplay, groupId);

			PortletURL portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, result.getPortletId(), result.getPlid(),
				PortletRequest.RENDER_PHASE);

			return portletURL.toString();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			return StringPool.BLANK;
		}
	}

	@Override
	public void undeploy(long appId) throws Exception {
		undeploy(appBuilderAppLocalService, appId, _serviceRegistrationsMap);
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();

		_serviceRegistrationsMap.clear();
	}

	protected PortletLayoutFinder getPortletLayoutFinder(long appId) {
		return new BasePortletLayoutFinder() {

			@Override
			protected String[] getPortletIds() {
				return new String[] {_getPortletName(appId, null)};
			}

		};
	}

	private ServiceRegistration<?> _deployPortlet(
		AppBuilderApp appBuilderApp, String appName, String portletName,
		boolean showFormView, boolean showTableView) {

		return deployPortlet(
			new AppPortlet(
				appBuilderApp, appBuilderAppPortletTabServiceTrackerMap,
				"widget", appName,
				appPortletMVCResourceCommandServiceTrackerMap, portletName,
				showFormView, showTableView),
			HashMapBuilder.<String, Object>put(
				"com.liferay.portlet.display-category", "category.app_builder"
			).build());
	}

	private Layout _findWidgetAppPortletLayout(
			long groupId, String portletName, ThemeDisplay themeDisplay)
		throws Exception {

		for (boolean privateLayout : Arrays.asList(false, true)) {
			List<Layout> layouts = _layoutLocalService.getLayouts(
				groupId, privateLayout, LayoutConstants.TYPE_CONTENT);

			for (Layout layout : layouts) {
				if (!_layoutPermission.contains(
						themeDisplay.getPermissionChecker(), layout,
						ActionKeys.VIEW)) {

					continue;
				}

				List<FragmentEntryLink> fragmentEntryLinks =
					_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
						layout.getGroupId(), layout.getPlid());

				for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
					List<String> portletIds =
						_portletRegistry.getFragmentEntryLinkPortletIds(
							fragmentEntryLink);

					if (portletIds.isEmpty()) {
						continue;
					}

					if (Stream.of(
							portletIds
						).flatMap(
							List::parallelStream
						).map(
							PortletIdCodec::decodePortletName
						).anyMatch(
							currentPortletName -> Objects.equals(
								currentPortletName, portletName)
						)) {

						return layout;
					}
				}
			}
		}

		return null;
	}

	private String _getAppName(AppBuilderApp appBuilderApp, String suffix) {
		StringBundler sb = new StringBundler(5);

		sb.append(appBuilderApp.getName(LocaleThreadLocal.getDefaultLocale()));

		if (suffix != null) {
			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(suffix);
			sb.append(StringPool.CLOSE_PARENTHESIS);
		}

		return sb.toString();
	}

	private String _getPortletName(long appId, String suffix) {
		StringBundler sb = new StringBundler(5);

		sb.append(AppBuilderPortletKeys.WIDGET_APP);
		sb.append(StringPool.UNDERLINE);
		sb.append(appId);

		if (suffix != null) {
			sb.append(StringPool.UNDERLINE);
			sb.append(suffix);
		}

		return sb.toString();
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private PortletRegistry _portletRegistry;

	private final ConcurrentHashMap<Long, ServiceRegistration<?>[]>
		_serviceRegistrationsMap = new ConcurrentHashMap<>();

}