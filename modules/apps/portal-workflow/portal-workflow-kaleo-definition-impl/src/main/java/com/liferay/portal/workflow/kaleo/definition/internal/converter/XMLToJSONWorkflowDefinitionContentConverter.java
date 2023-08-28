/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.converter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.converter.WorkflowDefinitionContentConverter;
import com.liferay.portal.workflow.kaleo.definition.internal.converter.constants.WorkflowDefinitionContentConverterConstants;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.xml.sax.InputSource;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "content.converter.type=xml-to-json",
	service = WorkflowDefinitionContentConverter.class
)
public class XMLToJSONWorkflowDefinitionContentConverter
	implements WorkflowDefinitionContentConverter {

	public String convert(String xml) throws WorkflowException {
		try {
			DocumentBuilderFactory documentBuilderFactory =
				SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(
				new InputSource(new StringReader(xml)));

			Element rootElement = document.getDocumentElement();

			JSONObject rootJSONObject = _toJSONObject(rootElement);

			return rootJSONObject.toString();
		}
		catch (Exception exception) {
			throw new WorkflowException(
				"Unable to convert workflow definition", exception);
		}
	}

	private void _appendAttributes(Element element, JSONObject jsonObject) {
		NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);

			jsonObject.put(node.getNodeName(), node.getNodeValue());
		}
	}

	private void _appendValue(Element element, JSONObject jsonObject) {
		if (!element.hasChildNodes()) {
			return;
		}

		String content = null;
		boolean cdata = false;

		NodeList childNodes = element.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);

			if (!(node instanceof Text) || !_hasContent(node.getNodeValue())) {
				continue;
			}

			if (node instanceof CDATASection) {
				cdata = true;
			}

			content = node.getNodeValue();

			break;
		}

		if (content == null) {
			return;
		}

		if (cdata) {
			jsonObject.put(
				WorkflowDefinitionContentConverterConstants.CDATA_VALUE,
				_toJSONArray(content));
		}
		else {
			jsonObject.put(
				WorkflowDefinitionContentConverterConstants.VALUE,
				content.replaceAll("[\n\t]", ""));
		}
	}

	private boolean _hasContent(String value) {
		if (Validator.isNull(value)) {
			return false;
		}

		value = value.replaceAll("[\n\t]", "");

		return !value.isEmpty();
	}

	private JSONArray _toJSONArray(String cdata) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		String[] lines = cdata.split(StringPool.NEW_LINE);

		for (String line : lines) {
			jsonArray.put(
				line.replaceAll(StringPool.TAB, StringPool.DOUBLE_SPACE));
		}

		return jsonArray;
	}

	private JSONObject _toJSONObject(Element element) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		jsonObject.put(
			WorkflowDefinitionContentConverterConstants.TAG_NAME,
			element.getTagName());

		_appendAttributes(element, jsonObject);

		_appendValue(element, jsonObject);

		NodeList childNodes = element.getChildNodes();

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (!(childNode instanceof Element)) {
				continue;
			}

			jsonArray.put(_toJSONObject((Element)childNode));
		}

		if (jsonArray.length() > 0) {
			jsonObject.put(
				WorkflowDefinitionContentConverterConstants.CHILD_NODES,
				jsonArray);
		}

		return jsonObject;
	}

	@Reference
	private JSONFactory _jsonFactory;

}