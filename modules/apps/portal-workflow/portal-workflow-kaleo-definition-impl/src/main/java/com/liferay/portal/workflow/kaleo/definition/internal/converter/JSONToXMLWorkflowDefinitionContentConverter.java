/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.converter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.converter.WorkflowDefinitionContentConverter;
import com.liferay.portal.workflow.kaleo.definition.internal.converter.constants.WorkflowDefinitionContentConverterConstants;

import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "content.converter.type=json-to-xml",
	service = WorkflowDefinitionContentConverter.class
)
public class JSONToXMLWorkflowDefinitionContentConverter
	implements WorkflowDefinitionContentConverter {

	public String convert(String json) throws WorkflowException {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\"?>");

		try {
			_toNode(sb::append, _jsonFactory.createJSONObject(json));
		}
		catch (JSONException jsonException) {
			throw new WorkflowException(
				"Unable to convert workflow definition", jsonException);
		}

		return sb.toString();
	}

	private void _appendAttributes(
		Consumer<String> consumer, JSONObject jsonObject) {

		for (String key : jsonObject.keySet()) {
			if (key.equals(
					WorkflowDefinitionContentConverterConstants.CDATA_VALUE) ||
				key.equals(
					WorkflowDefinitionContentConverterConstants.CHILD_NODES) ||
				key.equals(
					WorkflowDefinitionContentConverterConstants.TAG_NAME) ||
				key.equals(WorkflowDefinitionContentConverterConstants.VALUE)) {

				continue;
			}

			consumer.accept(StringPool.SPACE);

			consumer.accept(key);

			consumer.accept(StringPool.EQUAL);
			consumer.accept(StringPool.QUOTE);

			consumer.accept(String.valueOf(jsonObject.get(key)));

			consumer.accept(StringPool.QUOTE);
		}
	}

	private void _appendValue(
		Consumer<String> consumer, JSONObject jsonObject) {

		if (jsonObject.has(
				WorkflowDefinitionContentConverterConstants.CDATA_VALUE)) {

			JSONArray jsonArray = jsonObject.getJSONArray(
				WorkflowDefinitionContentConverterConstants.CDATA_VALUE);

			StringBundler sb = new StringBundler((jsonArray.length() * 2) + 4);

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
		else if (jsonObject.has(
					WorkflowDefinitionContentConverterConstants.VALUE)) {

			consumer.accept(
				jsonObject.getString(
					WorkflowDefinitionContentConverterConstants.VALUE));
		}
	}

	private void _toNode(Consumer<String> consumer, JSONObject jsonObject) {
		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));

		_appendAttributes(consumer, jsonObject);

		consumer.accept(StringPool.GREATER_THAN);

		_appendValue(consumer, jsonObject);

		JSONArray childNodesJSONArray = jsonObject.getJSONArray(
			WorkflowDefinitionContentConverterConstants.CHILD_NODES);

		if (childNodesJSONArray != null) {
			childNodesJSONArray.forEach(
				object -> _toNode(consumer, (JSONObject)object));
		}

		consumer.accept(StringPool.LESS_THAN);
		consumer.accept(StringPool.FORWARD_SLASH);
		consumer.accept(
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));
		consumer.accept(StringPool.GREATER_THAN);
	}

	@Reference
	private JSONFactory _jsonFactory;

}