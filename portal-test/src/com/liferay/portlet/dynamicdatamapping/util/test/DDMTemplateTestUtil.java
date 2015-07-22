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

package com.liferay.portlet.dynamicdatamapping.util.test;

import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.dynamicdatamapping.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.DDMTemplateManager;
import com.liferay.portlet.dynamicdatamapping.DDMTemplateManagerUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 * @author Rafael Praxedes
 */
public class DDMTemplateTestUtil {

	public static DDMTemplate addTemplate(
			long groupId, long classNameId, long classPK)
		throws Exception {

		return addTemplate(
			groupId, classNameId, classPK, TemplateConstants.LANG_TYPE_VM,
			getSampleTemplateXSL(), LocaleUtil.getSiteDefault());
	}

	public static DDMTemplate addTemplate(
			long groupId, long classNameId, long classPK, String language,
			String script, Locale defaultLocale)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(defaultLocale, "Test Template");

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		return DDMTemplateLocalServiceUtil.addTemplate(
			TestPropsValues.getUserId(), groupId, classNameId, classPK, 0,
			nameMap, null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
			language, script, serviceContext);
	}

	public static DDMTemplate addTemplate(
			long groupId, long structureId, String language, String script,
			Locale defaultLocale)
		throws Exception {

		return addTemplate(
			groupId, PortalUtil.getClassNameId(DDMStructure.class), structureId,
			language, script, defaultLocale);
	}

	public static DDMTemplate addTemplate(
			long structureId, String language, String script)
		throws Exception {

		return addTemplate(
			TestPropsValues.getGroupId(), structureId, language, script,
			LocaleUtil.getSiteDefault());
	}

	public static DDMTemplate addTemplate(
			long structureId, String language, String script,
			Locale defaultLocale)
		throws Exception {

		return addTemplate(
			TestPropsValues.getGroupId(), structureId, language, script,
			defaultLocale);
	}

	public static Document createDocument(
		String availableLocales, String defaultLocale) {

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("available-locales", availableLocales);
		rootElement.addAttribute("default-locale", defaultLocale);
		rootElement.addElement("request");

		return document;
	}

	public static String getSampleTemplateXSL() {
		return "$name.getData()";
	}

}
