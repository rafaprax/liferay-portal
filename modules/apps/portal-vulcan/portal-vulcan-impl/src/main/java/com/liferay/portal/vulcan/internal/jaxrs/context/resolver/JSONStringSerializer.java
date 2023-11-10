package com.liferay.portal.vulcan.internal.jaxrs.context.resolver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jackson.databind.ser.JSONStringWrapper;

import java.io.IOException;

public class JSONStringSerializer extends JsonSerializer<JSONStringWrapper> {

	@Override
	public void serialize(
			JSONStringWrapper jsonStringWrapper, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider)
		throws IOException {

		String value = jsonStringWrapper.getJSONString();

		if (!StringUtil.startsWith(value, CharPool.OPEN_CURLY_BRACE)) {
			jsonGenerator.writeObject(value);

			return;
		}

		try {
			jsonGenerator.writeObject(JSONFactoryUtil.createJSONObject(value));
		}
		catch (JSONException jsonException) {
			ReflectionUtil.throwException(jsonException);
		}
	}

}