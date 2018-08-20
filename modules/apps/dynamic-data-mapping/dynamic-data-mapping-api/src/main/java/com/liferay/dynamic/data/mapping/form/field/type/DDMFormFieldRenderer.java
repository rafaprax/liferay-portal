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

package com.liferay.dynamic.data.mapping.form.field.type;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.template.TemplateResource;

/**
 * @author Pablo Carvalho
 */
public interface DDMFormFieldRenderer {

	public String getTemplateNamespace();

	public TemplateResource getTemplateResource();

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {
	 * @link DDMFormFieldRenderer#render(DDMFormFieldRendererRenderRequest)}
	 */
	@Deprecated
	public default String render(
			DDMFormField ddmFormField,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws PortalException {

		DDMFormFieldRendererRenderRequest.Builder builder =
			DDMFormFieldRendererRenderRequest.Builder.newBuilder(
			).withLabel(
				ddmFormFieldRenderingContext.getLabel()
			).withName(
				ddmFormFieldRenderingContext.getName()
			).withReadOnly(
				ddmFormFieldRenderingContext.isReadOnly() ||
				ddmFormField.isReadOnly()
			).withRequired(
				ddmFormFieldRenderingContext.isRequired()
			).withShowLabel(
				ddmFormField.isShowLabel()
			).withTip(
				ddmFormFieldRenderingContext.getTip()
			).withValue(
				ddmFormFieldRenderingContext.getValue()
			).withValue(
				ddmFormFieldRenderingContext.isVisible()
			).withDDMFormField(
				ddmFormField
			).withRequest(
				ddmFormFieldRenderingContext.getHttpServletRequest()
			).withResponse(
				ddmFormFieldRenderingContext.getHttpServletResponse()
			);

		DDMFormFieldRendererRenderResponse ddmFormFieldRendererRenderResponse =
			render(builder.build());

		return ddmFormFieldRendererRenderResponse.getContent();
	}

	public DDMFormFieldRendererRenderResponse render(
		DDMFormFieldRendererRenderRequest ddmFormFieldRendererRenderRequest);

}