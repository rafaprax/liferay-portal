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

package com.liferay.dynamic.data.mapping.form.field.type.checkbox.multiple.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRendererRenderRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRendererRenderResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRendererRenderRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateRendererRenderResponse;
import com.liferay.portal.kernel.template.TemplateResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=checkbox_multiple"
)
public class CheckboxMultipleDDMFormFieldRenderer
	implements DDMFormFieldRenderer {

	public String getTemplateNamespace() {
		return _TEMPLATE_NAMESPACE;
	}

	@Override
	public TemplateResource getTemplateResource() {
		return ddmFormFieldTemplateRenderer.getTemplateResource(_TEMPLATE_PATH);
	}

	@Override
	public DDMFormFieldRendererRenderResponse render(
		DDMFormFieldRendererRenderRequest ddmFormFieldRendererRenderRequest) {

		DDMFormFieldTemplateRendererRenderRequest.Builder builder =
			DDMFormFieldTemplateRendererRenderRequest.Builder.newBuilder(
				_TEMPLATE_NAMESPACE, _TEMPLATE_PATH
			).withDDMFormFieldRendererRenderRequest(
				ddmFormFieldRendererRenderRequest
			).withDDMFormFieldTemplateContextContributor(
				checkboxMultipleDDMFormFieldTemplateContextContributor
			);

		DDMFormFieldTemplateRendererRenderResponse
			ddmFormFieldTemplateRendererRenderResponse =
				ddmFormFieldTemplateRenderer.render(builder.build());

		return DDMFormFieldRendererRenderResponse.Builder.newBuilder(
			ddmFormFieldTemplateRendererRenderResponse.getContent()
		).build();
	}

	@Reference
	protected CheckboxMultipleDDMFormFieldTemplateContextContributor
		checkboxMultipleDDMFormFieldTemplateContextContributor;

	@Reference
	protected DDMFormFieldTemplateRenderer ddmFormFieldTemplateRenderer;

	private static final String _TEMPLATE_NAMESPACE =
		"DDMCheckboxMultiple.render";

	private static final String _TEMPLATE_PATH =
		"/META-INF/resources/checkbox-multiple/checkbox-multiple.soy";

}