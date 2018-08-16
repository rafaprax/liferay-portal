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

package com.liferay.dynamic.data.mapping.form.field.type.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRendererRenderRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRendererRenderRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRendererRenderResponse;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.language.LanguageConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.URLTemplateResource;

import java.io.Writer;

import java.net.URL;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(immediate = true)
public class DDMFormFieldSoyTemplateRenderer
	implements DDMFormFieldTemplateRenderer {

	@Override
	public TemplateResource getTemplateResource(String templatePath) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		URL templateURL = classLoader.getResource(templatePath);

		return new URLTemplateResource(templateURL.getPath(), templateURL);
	}

	@Override
	public DDMFormFieldTemplateRendererRenderResponse render(
		DDMFormFieldTemplateRendererRenderRequest
			ddmFormFieldTemplateRendererRenderRequest) {

		String templatePath =
			ddmFormFieldTemplateRendererRenderRequest.getTemplatePath();
		boolean templateRenderStrict =
			ddmFormFieldTemplateRendererRenderRequest.isTemplateRenderStrict();
		String templateNamespace =
			ddmFormFieldTemplateRendererRenderRequest.getTemplateNamespace();

		DDMFormFieldRendererRenderRequest ddmFormFieldRendererRenderRequest =
			ddmFormFieldTemplateRendererRenderRequest.
				getDDMFormFieldRendererRenderRequest();

		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor =
				ddmFormFieldTemplateRendererRenderRequest.
					getDDMFormFieldTemplateContextContributor();

		Locale locale = ddmFormFieldRendererRenderRequest.getLocale();
		String label = ddmFormFieldRendererRenderRequest.getLabel();
		String name = ddmFormFieldRendererRenderRequest.getName();
		boolean readOnly = ddmFormFieldRendererRenderRequest.isReadOnly();
		boolean required = ddmFormFieldRendererRenderRequest.isRequired();
		boolean showLabel = ddmFormFieldRendererRenderRequest.isShowLabel();
		String tip = ddmFormFieldRendererRenderRequest.getTip();
		Object value = ddmFormFieldRendererRenderRequest.getValue();
		boolean visible = ddmFormFieldRendererRenderRequest.isVisible();

		String content = StringPool.BLANK;

		try {
			Template template = TemplateManagerUtil.getTemplate(
				TemplateConstants.LANG_TYPE_SOY,
				getTemplateResource(templatePath), templateRenderStrict);

			template.put(TemplateConstants.NAMESPACE, templateNamespace);
			template.put(TemplateConstants.RENDER_STRICT, templateRenderStrict);
			template.put(
				"dir", LanguageUtil.get(locale, LanguageConstants.KEY_DIR));
			template.put("label", label);
			template.put("name", name);
			template.put("readOnly", readOnly);
			template.put("required", required);
			template.put("showLabel", showLabel);
			template.put("tip", tip);
			template.put("value", value);
			template.put("visible", visible);

			if (ddmFormFieldTemplateContextContributor != null) {
				Map<String, Object> properties =
					getContextContributorProperties(
						ddmFormFieldRendererRenderRequest,
						ddmFormFieldTemplateContextContributor);

				for (Map.Entry<String, Object> entry : properties.entrySet()) {
					template.put(entry.getKey(), entry.getValue());
				}
			}

			Writer writer = new UnsyncStringWriter();

			template.processTemplate(writer);

			content = writer.toString();
		}
		catch (TemplateException te) {
			if (_log.isWarnEnabled()) {
				_log.warn(te, te);
			}
		}

		DDMFormFieldTemplateRendererRenderResponse.Builder builder =
			DDMFormFieldTemplateRendererRenderResponse.Builder.newBuilder(
				content
			);

		return builder.build();
	}

	protected Map<String, Object> getContextContributorProperties(
		DDMFormFieldRendererRenderRequest ddmFormFieldRendererRenderRequest,
		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor) {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			ddmFormFieldRendererRenderRequest.getRequest());
		ddmFormFieldRenderingContext.setHttpServletResponse(
			ddmFormFieldRendererRenderRequest.getResponse());
		ddmFormFieldRenderingContext.setLocale(
			ddmFormFieldRendererRenderRequest.getLocale());
		ddmFormFieldRenderingContext.setProperties(
			ddmFormFieldRendererRenderRequest.getProperties());

		Object value = ddmFormFieldRendererRenderRequest.getValue();

		if (value != null) {
			ddmFormFieldRenderingContext.setValue(value.toString());
		}

		return ddmFormFieldTemplateContextContributor.getParameters(
			ddmFormFieldRendererRenderRequest.getDDMFormField(),
			ddmFormFieldRenderingContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldSoyTemplateRenderer.class);

}