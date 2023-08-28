/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.converter;

import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.workflow.kaleo.definition.converter.WorkflowDefinitionContentConverter;

import org.junit.BeforeClass;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseWorkflowDefinitionContentConverterTestCase {

	@BeforeClass
	public static void setUpClass() {
		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());
	}

	protected <T> T convert(String fileName) throws Exception {
		WorkflowDefinitionContentConverter workflowDefinitionContentConverter =
			getWorkflowDefinitionContentConverter();

		return processContent(
			workflowDefinitionContentConverter.convert(_read(fileName)));
	}

	protected abstract WorkflowDefinitionContentConverter
		getWorkflowDefinitionContentConverter();

	protected abstract <T> T processContent(String content) throws Exception;

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

}