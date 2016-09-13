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

package com.liferay.dynamic.data.lists.form.web.display.context;

import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;

import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Adam Brandizzi
 */
@PrepareForTest(PortalUtil.class)
@RunWith(PowerMockRunner.class)
public class DDLFormAdminDisplayContextTest {

	@Before
	public void setUp() {
		setUpMocks();
		setUpPortalUtil();
	}

	@Test
	public void testGetSiteDefaultLanguageId() {
		DDLFormAdminDisplayContext ddlFormAdminDisplayContext =
			createDDLFormAdminDisplayContext();

		setUpThemeDisplaySiteDefaultLocale(LocaleUtil.BRAZIL);

		Assert.assertEquals(
			LocaleUtil.toLanguageId(LocaleUtil.BRAZIL),
			ddlFormAdminDisplayContext.getSiteDefaultLanguageId());

		setUpThemeDisplaySiteDefaultLocale(LocaleUtil.FRANCE);

		Assert.assertEquals(
			LocaleUtil.toLanguageId(LocaleUtil.FRANCE),
			ddlFormAdminDisplayContext.getSiteDefaultLanguageId());
	}

	protected DDLFormAdminDisplayContext createDDLFormAdminDisplayContext() {
		return new DDLFormAdminDisplayContext(
			_renderRequest, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null);
	}

	protected void setUpMocks() {
		_httpServletRequestRequest = mock(HttpServletRequest.class);
		_renderRequest = mock(RenderRequest.class);
		_themeDisplay = mock(ThemeDisplay.class);
	}

	protected void setUpPortalUtil() {
		mockStatic(PortalUtil.class);

		when(
			PortalUtil.getHttpServletRequest(_renderRequest)
		).thenReturn(
			_httpServletRequestRequest
		);
	}

	protected void setUpThemeDisplaySiteDefaultLocale(Locale defaultLocale) {
		whenHttpServletRequestRequestGetThemeDisplay(_themeDisplay);

		whenThemeDisplayGetSiteDefaultLocale(_themeDisplay, defaultLocale);
	}

	protected void whenHttpServletRequestRequestGetThemeDisplay(
		ThemeDisplay returnThemeDisplay) {

		when(
			_httpServletRequestRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			returnThemeDisplay
		);
	}

	protected void whenThemeDisplayGetSiteDefaultLocale(
		ThemeDisplay themeDisplay, Locale returnSiteDefaultLocale) {

		when(
			themeDisplay.getSiteDefaultLocale()
		).thenReturn(
			returnSiteDefaultLocale
		);
	}

	private HttpServletRequest _httpServletRequestRequest;
	private RenderRequest _renderRequest;
	private ThemeDisplay _themeDisplay;

}