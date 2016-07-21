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

package com.liferay.dynamic.data.mapping.form.renderer.internal.servlet;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;

import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-renderer-evaluator",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.renderer.internal.servlet.DDMFormRendererEvaluatorServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-renderer-evaluator/*"
	},
	service = Servlet.class
)
public class DDMFormRendererEvaluatorServlet extends HttpServlet {

	protected DDMFormRenderingContext createDDMFormRenderingContext(
		HttpServletRequest request, HttpServletResponse response) {

		String portletNamespace = ParamUtil.getString(
			request, "portletNamespace");

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(request);
		ddmFormRenderingContext.setHttpServletResponse(response);
		ddmFormRenderingContext.setLocale(request.getLocale());
		ddmFormRenderingContext.setPortletNamespace(portletNamespace);

		return ddmFormRenderingContext;
	}

	protected Object doEvaluate(
		HttpServletRequest request, HttpServletResponse response) {

		String serializedDDMForm = ParamUtil.getString(
			request, "serializedDDMForm");
		String serializedDDMFormLayout = ParamUtil.getString(
			request, "serializedDDMFormLayout");
		String serializedDDMFormValues = ParamUtil.getString(
			request, "serializedDDMFormValues");

		try {
			DDMForm ddmForm = _ddmFormJSONDeserializer.deserialize(
				serializedDDMForm);

			DDMFormValues ddmFormValues =
				_ddmFormValuesJSONDeserializer.deserialize(
					ddmForm, serializedDDMFormValues);

			DDMFormRenderingContext ddmFormRenderingContext =
				createDDMFormRenderingContext(request, response);

			ddmFormRenderingContext.setDDMFormValues(ddmFormValues);

			DDMFormLayout ddmFormLayout =
				_ddmFormLayoutJSONDeserializer.deserialize(
					serializedDDMFormLayout);

			LocaleThreadLocal.setThemeDisplayLocale(ddmForm.getDefaultLocale());

			Map<String, Object> templateContext =
				_ddmFormTemplateContextFactory.create(
					ddmForm, ddmFormLayout, ddmFormRenderingContext);

			return templateContext.get("pages");
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return null;
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		Object pages = doEvaluate(request, response);

		if (pages == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		response.setContentType(ContentTypes.APPLICATION_JSON);
		response.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			response, jsonSerializer.serializeDeep(pages));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormRendererEvaluatorServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMFormJSONDeserializer _ddmFormJSONDeserializer;

	@Reference
	private DDMFormLayoutJSONDeserializer _ddmFormLayoutJSONDeserializer;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer;

	@Reference
	private JSONFactory _jsonFactory;

}