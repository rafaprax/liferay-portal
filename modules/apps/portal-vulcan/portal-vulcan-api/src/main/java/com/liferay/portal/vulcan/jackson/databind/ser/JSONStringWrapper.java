package com.liferay.portal.vulcan.jackson.databind.ser;

public class JSONStringWrapper {

	public JSONStringWrapper(String jsonString) {
		_jsonString = jsonString;
	}

	public String getJSONString() {
		return _jsonString;
	}

	@Override
	public String toString() {
		return _jsonString;
	}

	private final String _jsonString;

}