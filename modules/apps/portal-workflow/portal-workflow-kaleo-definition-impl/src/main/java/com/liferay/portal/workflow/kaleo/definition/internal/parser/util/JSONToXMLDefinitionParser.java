/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author Rafael Praxedes
 */
@Component(service = JSONToXMLDefinitionParser.class)
public class JSONToXMLDefinitionParser {

	public String parse(String content) throws Exception {
		JSONObject jsonObject = _jsonFactory.createJSONObject(content);

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\"?>");

		for (String key : jsonObject.keySet()) {
			_toNode(
				sb::append, key, jsonObject.getJSONObject(key));
		}

		return sb.toString();
	}

	private Map<String, Set<String>> _processJSONObjectKeys(JSONObject jsonObject) {
		Map<String, Set<String>> keysMap = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			if (key.equals(_CDATA_VALUE) || key.equals(_VALUE) ) {
				continue;
			}

			Object value = jsonObject.get(key);

			if (value instanceof JSONArray || value instanceof JSONObject) {
				Set<String> childNodeKeys =
					keysMap.computeIfAbsent(
						"childNodeNames", attributeKey -> new TreeSet<>());

				childNodeKeys.add(key);
			}
			else {
				Set<String> attributesKeys =
					keysMap.computeIfAbsent(
						"attributeNames", attributeKey -> new TreeSet<>());

				attributesKeys.add(key);
			}
		}

		return keysMap;
	}

	private void _appendAttributes(
		Set<String> attributesNames,
		Consumer<String> consumer,
		JSONObject jsonObject) {

		for (String attributeName : attributesNames) {
			consumer.accept(StringPool.SPACE);

			consumer.accept(attributeName);

			consumer.accept(StringPool.EQUAL);
			consumer.accept(StringPool.QUOTE);

			consumer.accept(String.valueOf(jsonObject.get(attributeName)));

			consumer.accept(StringPool.QUOTE);
		}
	}

	private void _toNode(
		Consumer<String> consumer, String nodeName, JSONObject jsonObject) {

		Map<String, Set<String>> keysMap =
			_processJSONObjectKeys(jsonObject);

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(nodeName);

		_appendAttributes(
			keysMap.getOrDefault("attributeNames", Collections.emptySet()),
			consumer, jsonObject);

		consumer.accept(StringPool.GREATER_THAN);

		_appendValue(consumer, jsonObject);

		for (String key : keysMap.getOrDefault("childNodeNames", Collections.emptySet())) {
			Object value = jsonObject.get(key);

			if (value instanceof JSONObject) {
				JSONObject childJSONObject = (JSONObject)value;

				_toNode(consumer, key, childJSONObject);
			}
			else if (value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)value;

				for (int i = 0; i < jsonArray.length(); i++) {
					Object itemValue = jsonArray.get(i);

					if (itemValue instanceof JSONObject) {
						_toNode(consumer, key, (JSONObject)itemValue);
					}
					else {
						_toNode(consumer, key, itemValue);
					}
				}
			}

		}

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(StringPool.FORWARD_SLASH);
		consumer.accept(nodeName);
		consumer.accept(StringPool.GREATER_THAN);
	}

	private void _appendValue(Consumer<String> consumer, JSONObject jsonObject) {
		if (jsonObject.has(_CDATA_VALUE)) {
			JSONArray jsonArray = jsonObject.getJSONArray(_CDATA_VALUE);

			StringBundler sb = new StringBundler(jsonArray.length() * 2 + 2);

			sb.append(StringPool.CDATA_OPEN);

			for (int i = 0; i < jsonArray.length(); i++) {
				String line = jsonArray.getString(i);
				line = line.replaceAll("\\s\\s", "\t");

				sb.append(line);
				sb.append("\n");

			}

			sb.append(StringPool.CDATA_CLOSE);

			consumer.accept(sb.toString());
		}
		else if(jsonObject.has(_VALUE)) {
			consumer.accept(String.valueOf(jsonObject.get(_VALUE)));
		}
	}

	private void _toNode(Consumer<String> consumer, String name, Object value) {
		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(name);
		consumer.accept(StringPool.GREATER_THAN);
		consumer.accept(String.valueOf(value));

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(StringPool.FORWARD_SLASH);
		consumer.accept(name);
		consumer.accept(StringPool.GREATER_THAN);
	}

	private static final String _CDATA_VALUE = "#cdata-value";
	private static final String _VALUE = "#value";

	@Reference
	private JSONFactory _jsonFactory;

}