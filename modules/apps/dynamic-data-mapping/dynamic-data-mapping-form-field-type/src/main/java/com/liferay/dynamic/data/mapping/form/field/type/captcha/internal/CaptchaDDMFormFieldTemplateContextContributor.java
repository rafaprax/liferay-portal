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

package com.liferay.dynamic.data.mapping.form.field.type.captcha.internal;

import com.liferay.captcha.taglib.servlet.taglib.CaptchaTag;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetRequest;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributorGetResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.template.soy.utils.SoyHTMLSanitizer;
import com.liferay.taglib.servlet.PageContextFactoryUtil;
import com.liferay.taglib.servlet.PipingServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=captcha",
	service = {
		CaptchaDDMFormFieldTemplateContextContributor.class,
		DDMFormFieldTemplateContextContributor.class
	}
)
public class CaptchaDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public DDMFormFieldTemplateContextContributorGetResponse get(
		DDMFormFieldTemplateContextContributorGetRequest
			ddmFormFieldTemplateContextContributorGetRequest) {

		String html = StringPool.BLANK;

		try {
			html = renderCaptchaTag(
				ddmFormFieldTemplateContextContributorGetRequest);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}
		}

		DDMFormFieldTemplateContextContributorGetResponse.Builder builder =
			DDMFormFieldTemplateContextContributorGetResponse.Builder.
				newBuilder();

		return builder.withParameter(
			"html", _soyHTMLSanitizer.sanitize(html)
		).build();
	}

	protected String renderCaptchaTag(
			DDMFormFieldTemplateContextContributorGetRequest
				ddmFormFieldTemplateContextContributorGetRequest)
		throws Exception {

		DDMFormField ddmFormField =
			ddmFormFieldTemplateContextContributorGetRequest.getDDMFormField();
		HttpServletRequest request =
			ddmFormFieldTemplateContextContributorGetRequest.getRequest();
		HttpServletResponse response =
			ddmFormFieldTemplateContextContributorGetRequest.getResponse();

		CaptchaTag captchaTag = new CaptchaTag();

		captchaTag.setUrl(
			GetterUtil.getString(ddmFormField.getProperty("url")));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PageContext pageContext = PageContextFactoryUtil.create(
			request, new PipingServletResponse(response, unsyncStringWriter));

		captchaTag.setPageContext(pageContext);

		captchaTag.runTag();

		return unsyncStringWriter.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CaptchaDDMFormFieldTemplateContextContributor.class);

	@Reference
	private SoyHTMLSanitizer _soyHTMLSanitizer;

}