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

package com.liferay.dynamic.data.mapping.test.util.web;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayRenderResponse;
import com.liferay.portal.kernel.servlet.URLEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionURL;
import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderURL;
import javax.portlet.ResourceURL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.util.PortalUtil;
import org.w3c.dom.Element;

/**
 * @author Rodrigo Paulino
 */
public class MockRenderResponse implements LiferayRenderResponse {

	public MockRenderResponse(
		HttpServletResponse httpServletResponse, String portletName) {

		_httpServletResponse = httpServletResponse;
		_portletName = portletName;
	}

	@Override
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, Element element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletURL createActionURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ActionURL createActionURL(MimeResponse.Copy copy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createActionURL(String portletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createActionURL(
		String portletName, MimeResponse.Copy copy) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Element createElement(String tagName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		MimeResponse.Copy copy) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle, MimeResponse.Copy copy,
		boolean includeLinkToLayoutUuid) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(String lifecycle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, MimeResponse.Copy copy) {

		throw new UnsupportedOperationException();
	}

	@Override
	public PortletURL createRenderURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderURL createRenderURL(MimeResponse.Copy copy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createRenderURL(String portletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createRenderURL(
		String portletName, MimeResponse.Copy copy) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceURL createResourceURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LiferayPortletURL createResourceURL(String portletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheControl getCacheControl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	@Override
	public String getLifecycle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNamespace() {
		if (_namespace == null) {
			_namespace = PortalUtil.getPortletNamespace(_portletName);
		}

		return _namespace;
	}

	@Override
	public Portlet getPortlet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getPropertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTitle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getUseDefaultTemplate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentType(String type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNextPossiblePortletModes(
		Collection<? extends PortletMode> portletModes) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResourceName(String resourceName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTitle(String title) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setURLEncoder(URLEncoder urlEncoder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUseDefaultTemplate(Boolean useDefaultTemplate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transferHeaders(HttpServletResponse response) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transferMarkupHeadElements() {
		throw new UnsupportedOperationException();
	}

	private final HttpServletResponse _httpServletResponse;
	private final String _portletName;

	private String _namespace;

}