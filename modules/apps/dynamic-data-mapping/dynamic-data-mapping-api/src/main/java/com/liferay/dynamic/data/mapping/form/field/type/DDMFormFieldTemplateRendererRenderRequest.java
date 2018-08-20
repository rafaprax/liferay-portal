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

/**
 * @author Leonardo Barros
 */
public final class DDMFormFieldTemplateRendererRenderRequest {

	public DDMFormFieldRendererRenderRequest
		getDDMFormFieldRendererRenderRequest() {

		return _ddmFormFieldRendererRenderRequest;
	}

	public DDMFormFieldTemplateContextContributor
		getDDMFormFieldTemplateContextContributor() {

		return _ddmFormFieldTemplateContextContributor;
	}

	public String getTemplateNamespace() {
		return _templateNamespace;
	}

	public String getTemplatePath() {
		return _templatePath;
	}

	public boolean isTemplateRenderStrict() {
		return _templateRenderStrict;
	}

	public static class Builder {

		public static Builder newBuilder(
			String templateNamespace, String templatePath) {

			return new Builder(templateNamespace, templatePath);
		}

		public DDMFormFieldTemplateRendererRenderRequest build() {
			return _ddmFormFieldTemplateRendererRenderRequest;
		}

		public Builder withDDMFormFieldRendererRenderRequest(
			DDMFormFieldRendererRenderRequest
				ddmFormFieldRendererRenderRequest) {

			_ddmFormFieldTemplateRendererRenderRequest.
				_ddmFormFieldRendererRenderRequest =
					ddmFormFieldRendererRenderRequest;

			return this;
		}

		public Builder withDDMFormFieldTemplateContextContributor(
			DDMFormFieldTemplateContextContributor
				ddmFormFieldTemplateContextContributor) {

			_ddmFormFieldTemplateRendererRenderRequest.
				_ddmFormFieldTemplateContextContributor =
					ddmFormFieldTemplateContextContributor;

			return this;
		}

		public Builder withTemplateRenderStrict(boolean templateRenderStrict) {
			_ddmFormFieldTemplateRendererRenderRequest._templateRenderStrict =
				templateRenderStrict;

			return this;
		}

		private Builder(String templateNamespace, String templatePath) {
			_ddmFormFieldTemplateRendererRenderRequest._templateNamespace =
				templateNamespace;

			_ddmFormFieldTemplateRendererRenderRequest._templatePath =
				templatePath;
		}

		private final DDMFormFieldTemplateRendererRenderRequest
			_ddmFormFieldTemplateRendererRenderRequest =
				new DDMFormFieldTemplateRendererRenderRequest();

	}

	private DDMFormFieldTemplateRendererRenderRequest() {
	}

	private DDMFormFieldRendererRenderRequest
		_ddmFormFieldRendererRenderRequest;
	private DDMFormFieldTemplateContextContributor
		_ddmFormFieldTemplateContextContributor;
	private String _templateNamespace;
	private String _templatePath;
	private boolean _templateRenderStrict;

}