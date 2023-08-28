/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = JSONToXMLDefinitionParser.class)
public class JSONToXMLDefinitionParser {

	public String parse(String content) throws Exception {
		JSONObject workflowDefinitionJSONObject = _jsonFactory.createJSONObject(
			content);

		StringBuilder sb = new StringBuilder();

		_toNode(
			sb::append, "workflow-definition",
			workflowDefinitionJSONObject.getJSONObject("workflow-definition"));

		return sb.toString();
	}

	private void _appendAttributes(
		Consumer<String> consumer, Set<String> attributesNames,
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

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(nodeName);

		Set<String> nodeAttributes = _nodeAttributes.getOrDefault(
			nodeName, Collections.emptySet());

		_appendAttributes(consumer, nodeAttributes, jsonObject);

		consumer.accept(StringPool.GREATER_THAN);

		for (String key : jsonObject.keySet()) {
			if (nodeAttributes.contains(key)) {
				continue;
			}

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
			else {
				_toNode(consumer, key, value);
			}
		}

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(StringPool.FORWARD_SLASH);
		consumer.accept(nodeName);
		consumer.accept(StringPool.GREATER_THAN);
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

	private static final Map<String, Set<String>> _nodeAttributes =
		HashMapBuilder.<String, Set<String>>put(
			"label", new HashSet<>(Arrays.asList("language-id"))
		).put(
			"workflow-definition",
			new HashSet<>(
				Arrays.asList("xmlns", "xsi:schemaLocation", "xmlns:xsi"))
		).build();

	@Reference
	private JSONFactory _jsonFactory;

}