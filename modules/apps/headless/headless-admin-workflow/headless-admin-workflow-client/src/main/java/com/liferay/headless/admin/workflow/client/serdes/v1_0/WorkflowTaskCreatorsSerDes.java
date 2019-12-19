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

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.Creator;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTaskCreators;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskCreatorsSerDes {

	public static WorkflowTaskCreators toDTO(String json) {
		WorkflowTaskCreatorsJSONParser workflowTaskCreatorsJSONParser =
			new WorkflowTaskCreatorsJSONParser();

		return workflowTaskCreatorsJSONParser.parseToDTO(json);
	}

	public static WorkflowTaskCreators[] toDTOs(String json) {
		WorkflowTaskCreatorsJSONParser workflowTaskCreatorsJSONParser =
			new WorkflowTaskCreatorsJSONParser();

		return workflowTaskCreatorsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowTaskCreators workflowTaskCreators) {
		if (workflowTaskCreators == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (workflowTaskCreators.getCreators() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creators\": ");

			sb.append("[");

			for (int i = 0; i < workflowTaskCreators.getCreators().length;
				 i++) {

				sb.append(
					String.valueOf(workflowTaskCreators.getCreators()[i]));

				if ((i + 1) < workflowTaskCreators.getCreators().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTaskCreators.getTaskId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskId\": ");

			sb.append(workflowTaskCreators.getTaskId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTaskCreatorsJSONParser workflowTaskCreatorsJSONParser =
			new WorkflowTaskCreatorsJSONParser();

		return workflowTaskCreatorsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowTaskCreators workflowTaskCreators) {

		if (workflowTaskCreators == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (workflowTaskCreators.getCreators() == null) {
			map.put("creators", null);
		}
		else {
			map.put(
				"creators", String.valueOf(workflowTaskCreators.getCreators()));
		}

		if (workflowTaskCreators.getTaskId() == null) {
			map.put("taskId", null);
		}
		else {
			map.put("taskId", String.valueOf(workflowTaskCreators.getTaskId()));
		}

		return map;
	}

	public static class WorkflowTaskCreatorsJSONParser
		extends BaseJSONParser<WorkflowTaskCreators> {

		@Override
		protected WorkflowTaskCreators createDTO() {
			return new WorkflowTaskCreators();
		}

		@Override
		protected WorkflowTaskCreators[] createDTOArray(int size) {
			return new WorkflowTaskCreators[size];
		}

		@Override
		protected void setField(
			WorkflowTaskCreators workflowTaskCreators,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creators")) {
				if (jsonParserFieldValue != null) {
					workflowTaskCreators.setCreators(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> CreatorSerDes.toDTO((String)object)
						).toArray(
							size -> new Creator[size]
						));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taskId")) {
				if (jsonParserFieldValue != null) {
					workflowTaskCreators.setTaskId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\":");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}