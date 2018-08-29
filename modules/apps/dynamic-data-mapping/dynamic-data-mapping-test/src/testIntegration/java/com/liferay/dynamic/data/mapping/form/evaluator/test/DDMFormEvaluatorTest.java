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

package com.liferay.dynamic.data.mapping.form.evaluator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldContextKey;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerTracker;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.test.BaseDDMServiceTestCase;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Pablo Carvalho
 */
@RunWith(Arquillian.class)
public class DDMFormEvaluatorTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSumValuesForRepeatableField() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-sum-values-repeatable-field.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-sum-values-repeatable-field-test-data.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		String actualResult = jsonSerializer.serializeDeep(
			doEvaluate(ddmForm, ddmFormValues));

		String expectedResult = read(
			"ddm-form-evaluator-result-sum-values-repeatable-field.json");

		JSONAssert.assertEquals(expectedResult, actualResult, false);
	}

	@Test
	public void testValidFields() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-form-valid-fields-test-data.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-form-values-valid-fields-test-data.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		String actualResult = jsonSerializer.serializeDeep(
			doEvaluate(ddmForm, ddmFormValues));

		String expectedResult = read(
			"ddm-form-evaluator-result-valid-fields-data.json");

		JSONAssert.assertEquals(expectedResult, actualResult, false);
	}

	@Test
	public void testVisibleFields1() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-form-visible-fields-test-data-1.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-form-values-visible-fields-test-data-1.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			doEvaluate(ddmForm, ddmFormValues);

		Map<DDMFormFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> fieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormFieldContextKey("Confirmation", "hany"));

		Assert.assertFalse((Boolean)fieldPropertyChanges.get("visible"));
	}

	@Test
	public void testVisibleFields2() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-form-visible-fields-test-data-2.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-form-values-visible-fields-test-data-2.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			doEvaluate(ddmForm, ddmFormValues);

		Map<DDMFormFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormFieldContextKey("Phone", "hany"));

		Assert.assertTrue((Boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	@Test
	public void testVisibleFields3() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-form-visible-fields-test-data-3.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-form-values-visible-fields-test-data-3.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			doEvaluate(ddmForm, ddmFormValues);

		Map<DDMFormFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormFieldContextKey("Phone", "hany"));

		Assert.assertFalse((Boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	@Test
	public void testVisibleFields4() throws Exception {
		String serializedDDMForm = read(
			"ddm-form-evaluator-form-visible-fields-test-data-4.json");

		DDMForm ddmForm = deserialize(serializedDDMForm);

		String serializedDDMFormValues = read(
			"ddm-form-evaluator-form-values-visible-fields-test-data-4.json");

		DDMFormValues ddmFormValues = deserialize(
			serializedDDMFormValues, ddmForm);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			doEvaluate(ddmForm, ddmFormValues);

		Map<DDMFormFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormFieldContextKey("Phone", "hany"));

		Assert.assertTrue((Boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	protected DDMForm deserialize(String content) {
		DDMFormDeserializer ddmFormDeserializer =
			_ddmFormDeserializerTracker.getDDMFormDeserializer("json");

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	protected DDMFormValues deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializer ddmFormValuesDeserializer =
			_ddmFormValuesDeserializerTracker.getDDMFormValuesDeserializer(
				"json");

		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				ddmFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	protected DDMFormEvaluatorEvaluateResponse doEvaluate(
		DDMForm ddmForm, DDMFormValues ddmFormValues) {

		DDMFormEvaluatorEvaluateRequest.Builder builder =
			DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
				ddmForm, ddmFormValues, LocaleUtil.US);

		builder.withGroupId(1L);

		return _ddmFormEvaluator.evaluate(builder.build());
	}

	@Inject
	private DDMFormDeserializerTracker _ddmFormDeserializerTracker;

	@Inject(type = DDMFormEvaluator.class)
	private DDMFormEvaluator _ddmFormEvaluator;

	@Inject
	private DDMFormValuesDeserializerTracker _ddmFormValuesDeserializerTracker;

}