/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * @author Rafael Praxedes
 */
@Component(service = XMLToJSONDefinitionParser.class)
public class XMLToJSONDefinitionParser {

	public String parse(String xml) throws Exception {
		DocumentBuilderFactory documentBuilderFactory =
			SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(
			new ByteArrayInputStream(xml.getBytes()));

		Element rootElement = document.getDocumentElement();

		JSONObject rootJSONObject = _jsonFactory.createJSONObject();

		rootJSONObject.put(
			rootElement.getTagName(), _toJSONObject(rootElement));

		return rootJSONObject.toString();
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
			jsonObject.put("#value", StringPool.BLANK);

			return;
		}

		NodeList childNodes = element.getChildNodes();

		boolean cDATA = false;

		String content = null;

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);

			if (!_hasContent(node.getNodeValue())) {
				continue;
			}

			if (node instanceof CDATASection) {
				cDATA = true;
			}

			content = node.getNodeValue();

			break;
		}

		if (cDATA) {
			jsonObject.put("#cdata-value", _toCDATAJSONArray(content));
		}
		else {
			jsonObject.put("#value", content);
		}
	}

	private Map<String, List<Element>> _getChildrenElementsMap(
		Element element) {

		Map<String, List<Element>> childrenElementsMap = new HashMap<>();

		NodeList childNodes = element.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (!(childNode instanceof Element)) {
				continue;
			}

			List<Element> childrenElements =
				childrenElementsMap.computeIfAbsent(
					childNode.getNodeName(), key -> new ArrayList<>());

			childrenElements.add((Element)childNode);
		}

		return childrenElementsMap;
	}

	private boolean _hasContent(String value) {
		if (Validator.isNull(value)) {
			return false;
		}

		value = value.replaceAll("[\n\t]", "");

		return !value.isEmpty();
	}

	private JSONArray _toCDATAJSONArray(String cDATA) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		String[] lines = cDATA.split("\n");

		for (String line : lines) {
			jsonArray.put(line.replaceAll("\t", StringPool.DOUBLE_SPACE));
		}

		return jsonArray;
	}

	private JSONObject _toJSONObject(Element element) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		_appendAttributes(element, jsonObject);

		_appendValue(element, jsonObject);

		Map<String, List<Element>> childrenElementsMap =
			_getChildrenElementsMap(element);

		for (Map.Entry<String, List<Element>> entry :
				childrenElementsMap.entrySet()) {

			List<Element> childrenElements = entry.getValue();

			if (childrenElements.size() == 1) {
				Element childElement = childrenElements.get(0);

				jsonObject.put(
					childElement.getNodeName(), _toJSONObject(childElement));
			}
			else {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				jsonObject.put(entry.getKey(), jsonArray);

				for (Element childElement : childrenElements) {
					jsonArray.put(_toJSONObject(childElement));
				}
			}
		}

		return jsonObject;
	}

	@Reference
	private JSONFactory _jsonFactory;

}