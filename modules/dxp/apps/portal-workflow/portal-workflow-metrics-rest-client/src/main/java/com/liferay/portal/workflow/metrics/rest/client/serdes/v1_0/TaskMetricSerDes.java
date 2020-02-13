/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.TaskMetric;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class TaskMetricSerDes {

	public static TaskMetric toDTO(String json) {
		TaskMetricJSONParser taskMetricJSONParser = new TaskMetricJSONParser();

		return taskMetricJSONParser.parseToDTO(json);
	}

	public static TaskMetric[] toDTOs(String json) {
		TaskMetricJSONParser taskMetricJSONParser = new TaskMetricJSONParser();

		return taskMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaskMetric taskMetric) {
		if (taskMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taskMetric.getBreachedInstanceCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"breachedInstanceCount\": ");

			sb.append(taskMetric.getBreachedInstanceCount());
		}

		if (taskMetric.getBreachedInstancePercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"breachedInstancePercentage\": ");

			sb.append(taskMetric.getBreachedInstancePercentage());
		}

		if (taskMetric.getDurationAvg() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"durationAvg\": ");

			sb.append(taskMetric.getDurationAvg());
		}

		if (taskMetric.getInstanceCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceCount\": ");

			sb.append(taskMetric.getInstanceCount());
		}

		if (taskMetric.getOnTimeInstanceCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onTimeInstanceCount\": ");

			sb.append(taskMetric.getOnTimeInstanceCount());
		}

		if (taskMetric.getOverdueInstanceCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueInstanceCount\": ");

			sb.append(taskMetric.getOverdueInstanceCount());
		}

		if (taskMetric.getTask() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"task\": ");

			sb.append(String.valueOf(taskMetric.getTask()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaskMetricJSONParser taskMetricJSONParser = new TaskMetricJSONParser();

		return taskMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TaskMetric taskMetric) {
		if (taskMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taskMetric.getBreachedInstanceCount() == null) {
			map.put("breachedInstanceCount", null);
		}
		else {
			map.put(
				"breachedInstanceCount",
				String.valueOf(taskMetric.getBreachedInstanceCount()));
		}

		if (taskMetric.getBreachedInstancePercentage() == null) {
			map.put("breachedInstancePercentage", null);
		}
		else {
			map.put(
				"breachedInstancePercentage",
				String.valueOf(taskMetric.getBreachedInstancePercentage()));
		}

		if (taskMetric.getDurationAvg() == null) {
			map.put("durationAvg", null);
		}
		else {
			map.put("durationAvg", String.valueOf(taskMetric.getDurationAvg()));
		}

		if (taskMetric.getInstanceCount() == null) {
			map.put("instanceCount", null);
		}
		else {
			map.put(
				"instanceCount", String.valueOf(taskMetric.getInstanceCount()));
		}

		if (taskMetric.getOnTimeInstanceCount() == null) {
			map.put("onTimeInstanceCount", null);
		}
		else {
			map.put(
				"onTimeInstanceCount",
				String.valueOf(taskMetric.getOnTimeInstanceCount()));
		}

		if (taskMetric.getOverdueInstanceCount() == null) {
			map.put("overdueInstanceCount", null);
		}
		else {
			map.put(
				"overdueInstanceCount",
				String.valueOf(taskMetric.getOverdueInstanceCount()));
		}

		if (taskMetric.getTask() == null) {
			map.put("task", null);
		}
		else {
			map.put("task", String.valueOf(taskMetric.getTask()));
		}

		return map;
	}

	public static class TaskMetricJSONParser
		extends BaseJSONParser<TaskMetric> {

		@Override
		protected TaskMetric createDTO() {
			return new TaskMetric();
		}

		@Override
		protected TaskMetric[] createDTOArray(int size) {
			return new TaskMetric[size];
		}

		@Override
		protected void setField(
			TaskMetric taskMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "breachedInstanceCount")) {
				if (jsonParserFieldValue != null) {
					taskMetric.setBreachedInstanceCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "breachedInstancePercentage")) {

				if (jsonParserFieldValue != null) {
					taskMetric.setBreachedInstancePercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "durationAvg")) {
				if (jsonParserFieldValue != null) {
					taskMetric.setDurationAvg(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "instanceCount")) {
				if (jsonParserFieldValue != null) {
					taskMetric.setInstanceCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "onTimeInstanceCount")) {

				if (jsonParserFieldValue != null) {
					taskMetric.setOnTimeInstanceCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "overdueInstanceCount")) {

				if (jsonParserFieldValue != null) {
					taskMetric.setOverdueInstanceCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "task")) {
				if (jsonParserFieldValue != null) {
					taskMetric.setTask(
						TaskSerDes.toDTO((String)jsonParserFieldValue));
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