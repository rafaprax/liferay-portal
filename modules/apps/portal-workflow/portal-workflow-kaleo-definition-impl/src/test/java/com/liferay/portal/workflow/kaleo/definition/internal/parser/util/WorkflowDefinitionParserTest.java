/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 */
public class WorkflowDefinitionParserTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		JSONFactory jsonFactory = new JSONFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			_JSON_TO_XML_DEFINITION_PARSER, "_jsonFactory", jsonFactory);

		ReflectionTestUtil.setFieldValue(
			_XML_TO_JSON_DEFINITION_PARSER, "_jsonFactory", jsonFactory);
	}

	@Test
	public void testParseCDataContentToJSON() throws Exception {
		System.out.println(
			_XML_TO_JSON_DEFINITION_PARSER.parse(_read("cdata.xml")));
	}

	@Test
	public void testParseSingleApproverFromJSONToXML() throws Exception {
		System.out.println(
			_JSON_TO_XML_DEFINITION_PARSER.parse(
				_read("single-approver-workflow-definition.json")));
	}

	@Test
	public void testParseSingleApproverFromXMLToJSON() throws Exception {
		System.out.println(
			_XML_TO_JSON_DEFINITION_PARSER.parse(
				_read("single-approver-workflow-definition.xml")));
	}

	@Test
	public void testParseTagWithAttributesAndContentToJSON() throws Exception {
		System.out.println(
			_XML_TO_JSON_DEFINITION_PARSER.parse(_read("labels.xml")));
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

	private static final JSONToXMLDefinitionParser
		_JSON_TO_XML_DEFINITION_PARSER = new JSONToXMLDefinitionParser();

	private static final XMLToJSONDefinitionParser
		_XML_TO_JSON_DEFINITION_PARSER = new XMLToJSONDefinitionParser();

}