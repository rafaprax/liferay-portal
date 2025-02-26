/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.SCIMSchema;
import com.liferay.scim.rest.client.dto.v1_0.SchemaAttribute;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class SCIMSchemaSerDes {

	public static SCIMSchema toDTO(String json) {
		SCIMSchemaJSONParser scimSchemaJSONParser = new SCIMSchemaJSONParser();

		return scimSchemaJSONParser.parseToDTO(json);
	}

	public static SCIMSchema[] toDTOs(String json) {
		SCIMSchemaJSONParser scimSchemaJSONParser = new SCIMSchemaJSONParser();

		return scimSchemaJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SCIMSchema scimSchema) {
		if (scimSchema == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (scimSchema.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(scimSchema.getDescription()));

			sb.append("\"");
		}

		if (scimSchema.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(scimSchema.getId()));

			sb.append("\"");
		}

		if (scimSchema.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(scimSchema.getMeta()));
		}

		if (scimSchema.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(scimSchema.getName()));

			sb.append("\"");
		}

		if (scimSchema.getSchemaAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemaAttributes\": ");

			sb.append("[");

			for (int i = 0; i < scimSchema.getSchemaAttributes().length; i++) {
				sb.append(String.valueOf(scimSchema.getSchemaAttributes()[i]));

				if ((i + 1) < scimSchema.getSchemaAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (scimSchema.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < scimSchema.getSchemas().length; i++) {
				sb.append(_toJSON(scimSchema.getSchemas()[i]));

				if ((i + 1) < scimSchema.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SCIMSchemaJSONParser scimSchemaJSONParser = new SCIMSchemaJSONParser();

		return scimSchemaJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SCIMSchema scimSchema) {
		if (scimSchema == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (scimSchema.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(scimSchema.getDescription()));
		}

		if (scimSchema.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(scimSchema.getId()));
		}

		if (scimSchema.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(scimSchema.getMeta()));
		}

		if (scimSchema.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(scimSchema.getName()));
		}

		if (scimSchema.getSchemaAttributes() == null) {
			map.put("schemaAttributes", null);
		}
		else {
			map.put(
				"schemaAttributes",
				String.valueOf(scimSchema.getSchemaAttributes()));
		}

		if (scimSchema.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(scimSchema.getSchemas()));
		}

		return map;
	}

	public static class SCIMSchemaJSONParser
		extends BaseJSONParser<SCIMSchema> {

		@Override
		protected SCIMSchema createDTO() {
			return new SCIMSchema();
		}

		@Override
		protected SCIMSchema[] createDTOArray(int size) {
			return new SCIMSchema[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemaAttributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SCIMSchema scimSchema, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					scimSchema.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					scimSchema.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					scimSchema.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					scimSchema.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemaAttributes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SchemaAttribute[] schemaAttributesArray =
						new SchemaAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < schemaAttributesArray.length; i++) {
						schemaAttributesArray[i] = SchemaAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					scimSchema.setSchemaAttributes(schemaAttributesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					scimSchema.setSchemas(
						toStrings((Object[])jsonParserFieldValue));
				}
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
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}