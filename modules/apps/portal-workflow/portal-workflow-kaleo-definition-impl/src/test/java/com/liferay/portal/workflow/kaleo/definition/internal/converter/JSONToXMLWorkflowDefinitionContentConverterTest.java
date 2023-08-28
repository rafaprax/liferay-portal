/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.converter;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.definition.converter.WorkflowDefinitionContentConverter;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Rafael Praxedes
 */
public class JSONToXMLWorkflowDefinitionContentConverterTest
	extends BaseWorkflowDefinitionContentConverterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_jsonToXMLWorkflowDefinitionConverter =
			new JSONToXMLWorkflowDefinitionContentConverter();

		ReflectionTestUtil.setFieldValue(
			_jsonToXMLWorkflowDefinitionConverter, "_jsonFactory",
			new JSONFactoryImpl());
	}

	@Test
	public void testConvertCDataContent() throws Exception {
		Document document = convert("cdata.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("metadata", rootElement.getTagName());

		Assert.assertTrue(rootElement.hasChildNodes());

		Node cdataNode = null;

		NodeList childNodes = rootElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (childNode instanceof CDATASection) {
				cdataNode = childNode;

				break;
			}
		}

		Assert.assertNotNull(cdataNode);

		String cdataContent = cdataNode.getTextContent();

		Assert.assertTrue(cdataContent.contains("xy"));
		Assert.assertTrue(cdataContent.contains("168,"));
		Assert.assertTrue(cdataContent.contains("36"));
	}

	@Test
	public void testConvertRepeatableTags() throws Exception {
		Document document = convert("repeatable-tag.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("container-tag", rootElement.getTagName());

		NodeList repeatableTagNodeList = rootElement.getElementsByTagName(
			"repeatable-tag");

		Assert.assertEquals(2, repeatableTagNodeList.getLength());

		Node repeatableTagNode = repeatableTagNodeList.item(0);

		Assert.assertEquals("first", repeatableTagNode.getTextContent());

		repeatableTagNode = repeatableTagNodeList.item(1);

		Assert.assertEquals("second", repeatableTagNode.getTextContent());
	}

	@Test
	public void testConvertSimpleNode() throws Exception {
		Document document = convert("simple-tag.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("test", rootElement.getTagName());

		String nodeContent = rootElement.getTextContent();

		Assert.assertTrue(nodeContent.contains("simple tag"));
	}

	@Test
	public void testConvertTagWithAttributesAndContent() throws Exception {
		Document document = convert("repeatable-tag-with-attributes.json");

		Element rootElement = document.getDocumentElement();

		Assert.assertEquals("labels", rootElement.getTagName());

		NodeList labelNodeList = rootElement.getElementsByTagName("label");

		Assert.assertEquals(1, labelNodeList.getLength());

		Node labelNode = labelNodeList.item(0);

		Assert.assertEquals("Label", labelNode.getTextContent());

		NamedNodeMap attributes = labelNode.getAttributes();

		Node attributeNode = attributes.getNamedItem("language-id");

		Assert.assertEquals("en_US", attributeNode.getNodeValue());
	}

	@Override
	protected WorkflowDefinitionContentConverter
		getWorkflowDefinitionContentConverter() {

		return _jsonToXMLWorkflowDefinitionConverter;
	}

	@Override
	protected Document processContent(String content) throws Exception {
		DocumentBuilderFactory documentBuilderFactory =
			SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		return documentBuilder.parse(
			new InputSource(new StringReader(content)));
	}

	private JSONToXMLWorkflowDefinitionContentConverter
		_jsonToXMLWorkflowDefinitionConverter =
			new JSONToXMLWorkflowDefinitionContentConverter();

}