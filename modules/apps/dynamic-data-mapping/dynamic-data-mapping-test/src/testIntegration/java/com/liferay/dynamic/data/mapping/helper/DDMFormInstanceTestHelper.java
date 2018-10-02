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

package com.liferay.dynamic.data.mapping.helper;

import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lino Alves
 */
public class DDMFormInstanceTestHelper {

	public static DDMFormValues createFormInstanceSettingsDDMFormValues() {
		DDMForm formInstanceSettingsDDMForm = DDMFormFactory.create(
			DDMFormInstanceSettings.class);

		DDMFormValues formInstanceSettingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(
				formInstanceSettingsDDMForm);

		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"autosaveEnabled", "true"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"emailFromAddress", "from@liferay.com"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"emailFromName", "Joe Bloggs"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"emailSubject", "New Form Submission"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"emailToAddress", "to@liferay.com"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"published", "Joe Bloggs"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"redirectURL", "http://www.google.com"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"requireAuthentication", "false"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"requireCaptcha", "true"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"sendEmailNotification", "false"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"storageType", "json"));
		formInstanceSettingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"workflowDefinition", "Single Approver@1"));

		return formInstanceSettingsDDMFormValues;
	}

	public DDMFormInstanceTestHelper(
		DDMFormValuesSerializer ddmFormValuesSerializer, Group group) {

		_ddmFormValuesSerializer = ddmFormValuesSerializer;
		_group = group;
	}

	public DDMFormInstance addDDMFormInstance(DDMStructure ddmStructure)
		throws Exception {

		Map<Locale, String> nameMap = getRandomStringMap();

		Map<Locale, String> descriptionMap = getRandomStringMap();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				settingsDDMFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				_ddmFormValuesSerializer.serialize(builder.build());

		String serializedSettingsDDMFormValues =
			ddmFormValuesSerializerSerializeResponse.getContent();

		return DDMFormInstanceLocalServiceUtil.addFormInstance(
			TestPropsValues.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), nameMap, descriptionMap,
			serializedSettingsDDMFormValues, serviceContext);
	}

	public DDMFormInstance updateFormInstance(
			long formInstanceId, DDMFormValues settingsDDMFormValues)
		throws PortalException {

		return DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstanceId, settingsDDMFormValues);
	}

	protected Map<Locale, String> getRandomStringMap() {
		Map<Locale, String> map = new HashMap<>();

		map.put(LocaleUtil.US, RandomTestUtil.randomString());

		return map;
	}

	private final DDMFormValuesSerializer _ddmFormValuesSerializer;
	private final Group _group;

}