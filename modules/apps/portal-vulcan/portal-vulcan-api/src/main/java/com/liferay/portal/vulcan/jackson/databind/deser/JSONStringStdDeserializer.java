/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.liferay.portal.vulcan.jackson.databind.ser.JSONStringWrapper;

import java.io.IOException;

/**
 * @author Sergio Jiménez del Coso
 */
public class JSONStringStdDeserializer
	extends StdDeserializer<JSONStringWrapper> {

	public JSONStringStdDeserializer() {
		super(String.class);
	}

	@Override
	public JSONStringWrapper deserialize(
			JsonParser jsonParser,
			DeserializationContext deserializationContext)
		throws IOException {

		if (jsonParser.hasToken(JsonToken.VALUE_STRING)) {
			return new JSONStringWrapper(jsonParser.getText());
		}

		TreeNode treeNode = jsonParser.readValueAsTree();

		return new JSONStringWrapper(treeNode.toString());
	}

}