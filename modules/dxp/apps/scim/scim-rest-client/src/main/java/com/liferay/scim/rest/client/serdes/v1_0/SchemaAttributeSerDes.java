/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

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
public class SchemaAttributeSerDes {

	public static SchemaAttribute toDTO(String json) {
		SchemaAttributeJSONParser schemaAttributeJSONParser =
			new SchemaAttributeJSONParser();

		return schemaAttributeJSONParser.parseToDTO(json);
	}

	public static SchemaAttribute[] toDTOs(String json) {
		SchemaAttributeJSONParser schemaAttributeJSONParser =
			new SchemaAttributeJSONParser();

		return schemaAttributeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SchemaAttribute schemaAttribute) {
		if (schemaAttribute == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (schemaAttribute.getCanonicalValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalValues\": ");

			sb.append("[");

			for (int i = 0; i < schemaAttribute.getCanonicalValues().length;
				 i++) {

				sb.append(_toJSON(schemaAttribute.getCanonicalValues()[i]));

				if ((i + 1) < schemaAttribute.getCanonicalValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (schemaAttribute.getCaseExact() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"caseExact\": ");

			sb.append(schemaAttribute.getCaseExact());
		}

		if (schemaAttribute.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(schemaAttribute.getDescription()));

			sb.append("\"");
		}

		if (schemaAttribute.getMultiValued() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(schemaAttribute.getMultiValued());
		}

		if (schemaAttribute.getMutability() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mutability\": ");

			sb.append("\"");

			sb.append(schemaAttribute.getMutability());

			sb.append("\"");
		}

		if (schemaAttribute.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(schemaAttribute.getName()));

			sb.append("\"");
		}

		if (schemaAttribute.getReferenceTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"referenceTypes\": ");

			sb.append("[");

			for (int i = 0; i < schemaAttribute.getReferenceTypes().length;
				 i++) {

				sb.append(_toJSON(schemaAttribute.getReferenceTypes()[i]));

				if ((i + 1) < schemaAttribute.getReferenceTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (schemaAttribute.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(schemaAttribute.getRequired());
		}

		if (schemaAttribute.getReturned() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"returned\": ");

			sb.append("\"");

			sb.append(schemaAttribute.getReturned());

			sb.append("\"");
		}

		if (schemaAttribute.getSchemaAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemaAttributes\": ");

			sb.append("[");

			for (int i = 0; i < schemaAttribute.getSchemaAttributes().length;
				 i++) {

				sb.append(
					String.valueOf(schemaAttribute.getSchemaAttributes()[i]));

				if ((i + 1) < schemaAttribute.getSchemaAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (schemaAttribute.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(schemaAttribute.getType());

			sb.append("\"");
		}

		if (schemaAttribute.getUniqueness() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uniqueness\": ");

			sb.append("\"");

			sb.append(schemaAttribute.getUniqueness());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SchemaAttributeJSONParser schemaAttributeJSONParser =
			new SchemaAttributeJSONParser();

		return schemaAttributeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SchemaAttribute schemaAttribute) {
		if (schemaAttribute == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (schemaAttribute.getCanonicalValues() == null) {
			map.put("canonicalValues", null);
		}
		else {
			map.put(
				"canonicalValues",
				String.valueOf(schemaAttribute.getCanonicalValues()));
		}

		if (schemaAttribute.getCaseExact() == null) {
			map.put("caseExact", null);
		}
		else {
			map.put(
				"caseExact", String.valueOf(schemaAttribute.getCaseExact()));
		}

		if (schemaAttribute.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(schemaAttribute.getDescription()));
		}

		if (schemaAttribute.getMultiValued() == null) {
			map.put("multiValued", null);
		}
		else {
			map.put(
				"multiValued",
				String.valueOf(schemaAttribute.getMultiValued()));
		}

		if (schemaAttribute.getMutability() == null) {
			map.put("mutability", null);
		}
		else {
			map.put(
				"mutability", String.valueOf(schemaAttribute.getMutability()));
		}

		if (schemaAttribute.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(schemaAttribute.getName()));
		}

		if (schemaAttribute.getReferenceTypes() == null) {
			map.put("referenceTypes", null);
		}
		else {
			map.put(
				"referenceTypes",
				String.valueOf(schemaAttribute.getReferenceTypes()));
		}

		if (schemaAttribute.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(schemaAttribute.getRequired()));
		}

		if (schemaAttribute.getReturned() == null) {
			map.put("returned", null);
		}
		else {
			map.put("returned", String.valueOf(schemaAttribute.getReturned()));
		}

		if (schemaAttribute.getSchemaAttributes() == null) {
			map.put("schemaAttributes", null);
		}
		else {
			map.put(
				"schemaAttributes",
				String.valueOf(schemaAttribute.getSchemaAttributes()));
		}

		if (schemaAttribute.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(schemaAttribute.getType()));
		}

		if (schemaAttribute.getUniqueness() == null) {
			map.put("uniqueness", null);
		}
		else {
			map.put(
				"uniqueness", String.valueOf(schemaAttribute.getUniqueness()));
		}

		return map;
	}

	public static class SchemaAttributeJSONParser
		extends BaseJSONParser<SchemaAttribute> {

		@Override
		protected SchemaAttribute createDTO() {
			return new SchemaAttribute();
		}

		@Override
		protected SchemaAttribute[] createDTOArray(int size) {
			return new SchemaAttribute[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "canonicalValues")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "caseExact")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mutability")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "referenceTypes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "returned")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemaAttributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uniqueness")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SchemaAttribute schemaAttribute, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "canonicalValues")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setCanonicalValues(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "caseExact")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setCaseExact((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setMultiValued(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mutability")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setMutability(
						SchemaAttribute.Mutability.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "referenceTypes")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setReferenceTypes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "returned")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setReturned(
						SchemaAttribute.Returned.create(
							(String)jsonParserFieldValue));
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

					schemaAttribute.setSchemaAttributes(schemaAttributesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setType(
						SchemaAttribute.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uniqueness")) {
				if (jsonParserFieldValue != null) {
					schemaAttribute.setUniqueness(
						SchemaAttribute.Uniqueness.create(
							(String)jsonParserFieldValue));
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