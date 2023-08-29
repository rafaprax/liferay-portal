/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.converter.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowException;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
public class WorkflowDefinitionContentConverterUtil {

	public static final String CDATA_VALUE = "#cdata-value";

	public static final String CHILD_NODES = "#child-nodes";

	public static final String TAG_NAME = "#tag-name";

	public static final String VALUE = "#value";

	public static String toJSON(String xml) throws WorkflowException {
		try {
			DocumentBuilderFactory documentBuilderFactory =
				SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(
				new InputSource(new StringReader(xml)));

			JSONObject jsonObject = _toJSONObject(
				document.getDocumentElement());

			return jsonObject.toString();
		}
		catch (Exception exception) {
			throw new WorkflowException(
				"Unable to convert workflow definition content", exception);
		}
	}

	public static String toXML(String json) throws WorkflowException {
		StringBuilder contentSB = new StringBuilder();

		contentSB.append("<?xml version=\"1.0\"?>");

		try {
			_toNode(contentSB, JSONFactoryUtil.createJSONObject(json));
		}
		catch (JSONException jsonException) {
			throw new WorkflowException(
				"Unable to convert workflow definition content", jsonException);
		}

		return contentSB.toString();
	}

	private static void _appendAttributes(
		Element element, JSONObject jsonObject) {

		NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node attributeNode = attributes.item(i);

			jsonObject.put(
				attributeNode.getNodeName(), attributeNode.getNodeValue());
		}
	}

	private static void _appendAttributes(
		StringBuilder contentSB, JSONObject jsonObject) {

		for (String key : jsonObject.keySet()) {
			if (key.equals(CDATA_VALUE) || key.equals(CHILD_NODES) ||
				key.equals(TAG_NAME) || key.equals(VALUE)) {

				continue;
			}

			contentSB.append(StringPool.SPACE);

			contentSB.append(key);

			contentSB.append(StringPool.EQUAL);
			contentSB.append(StringPool.QUOTE);

			contentSB.append(jsonObject.get(key));

			contentSB.append(StringPool.QUOTE);
		}
	}

	private static void _appendValue(Element element, JSONObject jsonObject) {
		if (!element.hasChildNodes()) {
			return;
		}

		Node valueNode = null;

		NodeList childNodes = element.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if ((childNode instanceof Text) &&
				_hasContent(childNode.getNodeValue())) {

				valueNode = childNode;

				break;
			}
		}

		if (valueNode == null) {
			return;
		}

		String value = valueNode.getNodeValue();

		if (valueNode instanceof CDATASection) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			for (String line : value.split(StringPool.NEW_LINE)) {
				jsonArray.put(
					line.replaceAll(StringPool.TAB, StringPool.FOUR_SPACES));
			}

			jsonObject.put(CDATA_VALUE, jsonArray);
		}
		else {
			jsonObject.put(VALUE, value.replaceAll("[\n\t]", StringPool.BLANK));
		}
	}

	private static void _appendValue(
		StringBuilder contentSB, JSONObject jsonObject) {

		if (jsonObject.has(CDATA_VALUE)) {
			JSONArray cdataJSONArray = jsonObject.getJSONArray(CDATA_VALUE);

			StringBundler cdataSB = new StringBundler(
				(cdataJSONArray.length() * 2) + 2);

			cdataSB.append(StringPool.CDATA_OPEN);

			for (int i = 0; i < cdataJSONArray.length(); i++) {
				String line = cdataJSONArray.getString(i);

				cdataSB.append(line.replaceAll("\\s\\s\\s\\s", StringPool.TAB));

				cdataSB.append(StringPool.NEW_LINE);
			}

			cdataSB.append(StringPool.CDATA_CLOSE);

			contentSB.append(cdataSB);
		}
		else if (jsonObject.has(VALUE)) {
			contentSB.append(jsonObject.getString(VALUE));
		}
	}

	private static boolean _hasContent(String value) {
		if (Validator.isNull(value)) {
			return false;
		}

		return Validator.isNotNull(
			value.replaceAll("[\n\t]", StringPool.BLANK));
	}

	private static JSONObject _toJSONObject(Element element) {
		JSONObject jsonObject = JSONUtil.put(TAG_NAME, element.getTagName());

		_appendAttributes(element, jsonObject);

		_appendValue(element, jsonObject);

		NodeList childNodes = element.getChildNodes();

		JSONArray childNodesJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (!(childNode instanceof Element)) {
				continue;
			}

			childNodesJSONArray.put(_toJSONObject((Element)childNode));
		}

		if (childNodesJSONArray.length() > 0) {
			jsonObject.put(CHILD_NODES, childNodesJSONArray);
		}

		return jsonObject;
	}

	private static void _toNode(
		StringBuilder contentSB, JSONObject jsonObject) {

		contentSB.append(StringPool.LESS_THAN);
		contentSB.append(jsonObject.getString(TAG_NAME));

		_appendAttributes(contentSB, jsonObject);

		contentSB.append(StringPool.GREATER_THAN);

		_appendValue(contentSB, jsonObject);

		JSONArray childNodesJSONArray = jsonObject.getJSONArray(CHILD_NODES);

		if (childNodesJSONArray != null) {
			childNodesJSONArray.forEach(
				object -> _toNode(contentSB, (JSONObject)object));
		}

		contentSB.append(StringPool.LESS_THAN);
		contentSB.append(StringPool.FORWARD_SLASH);
		contentSB.append(jsonObject.getString(TAG_NAME));
		contentSB.append(StringPool.GREATER_THAN);
	}

}