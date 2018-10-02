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

package com.liferay.dynamic.data.mapping.upgrade.v2_0_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class UpgradeDDMFormInstanceSettingsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_jsonFactory = new JSONFactoryImpl();

		setUpUpgradeDDMFormInstanceSettings();
	}

	@Test
	public void testAddRequireAuthenticationSetting() throws Exception {
		String settings = createSettings(false);

		DDMFormInstance formInstance = createFormInstance(settings);

		JSONArray fieldValues = getFieldValues(formInstance.getSettings());

		Assert.assertFalse(containsField(fieldValues, "requireAuthentication"));

		_upgradeDDMFormInstanceSettings.upgrade();

		formInstance = getRecordSet(formInstance);

		fieldValues = getFieldValues(formInstance.getSettings());

		Assert.assertTrue(containsField(fieldValues, "requireAuthentication"));
	}

	@Test
	public void testEnableAutosaveSetting() throws Exception {
		String settings = createSettings(false);

		DDMFormInstance formInstance = createFormInstance(settings);

		JSONArray fieldValues = getFieldValues(formInstance.getSettings());

		Assert.assertFalse(containsField(fieldValues, "autosaveEnabled"));

		_upgradeDDMFormInstanceSettings.upgrade();

		formInstance = getRecordSet(formInstance);

		fieldValues = getFieldValues(formInstance.getSettings());

		Assert.assertTrue(containsField(fieldValues, "autosaveEnabled"));
	}

	protected boolean containsField(JSONArray fieldValues, String field) {
		for (int i = 0; i < fieldValues.length(); i++) {
			JSONObject fieldValue = fieldValues.getJSONObject(i);

			String fieldName = fieldValue.getString("name");

			if (fieldName.equals(field)) {
				return true;
			}
		}

		return false;
	}

	protected JSONArray createFieldValues(boolean hasSetting) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		jsonArray.put(getFieldValue("requireCaptcha", "false"));
		jsonArray.put(getFieldValue("redirectURL", ""));
		jsonArray.put(getFieldValue("storageType", "json"));
		jsonArray.put(getFieldValue("workflowDefinition", ""));
		jsonArray.put(getFieldValue("sendEmailNotification", "false"));
		jsonArray.put(getFieldValue("emailFromName", ""));
		jsonArray.put(getFieldValue("emailFromAddress", ""));
		jsonArray.put(getFieldValue("emailToAddress", ""));
		jsonArray.put(getFieldValue("emailSubject", ""));
		jsonArray.put(getFieldValue("published", "false"));

		if (hasSetting) {
			jsonArray.put(getFieldValue("autosaveEnabled", "false"));
			jsonArray.put(getFieldValue("requireAuthentication", "false"));
		}

		return jsonArray;
	}

	protected DDMFormInstance createFormInstance(String settings)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field");

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				TestPropsValues.getUserId(), _group.getGroupId(), nameMap,
				descriptionMap, ddmForm, ddmFormLayout, settingsDDMFormValues,
				serviceContext);

		ddmFormInstance.setSettings(settings);

		DDMFormInstanceLocalServiceUtil.updateDDMFormInstance(ddmFormInstance);

		ddmFormInstance = DDMFormInstanceLocalServiceUtil.getFormInstance(
			ddmFormInstance.getFormInstanceId());

		return ddmFormInstance;
	}

	protected String createSettings(boolean hasSetting) {
		JSONObject object = _jsonFactory.createJSONObject();

		JSONArray availableLanguagesJSONArray = getAvailableLanguagesJSONArray(
			"en_US");

		object.put("availableLanguageIdss", availableLanguagesJSONArray);

		object.put("defaultLanguageId", "en_US");

		JSONArray fieldValues = createFieldValues(hasSetting);

		object.put("fieldValues", fieldValues);

		return object.toJSONString();
	}

	protected JSONArray getAvailableLanguagesJSONArray(String languageId) {
		JSONArray array = _jsonFactory.createJSONArray();

		array.put(languageId);

		return array;
	}

	protected JSONObject getFieldValue(String name, String value) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		jsonObject.put("instanceId", RandomTestUtil.randomString());
		jsonObject.put("name", name);
		jsonObject.put("value", value);

		return jsonObject;
	}

	protected JSONArray getFieldValues(String settings) throws JSONException {
		JSONObject settingsJSONObject = _jsonFactory.createJSONObject(settings);

		return settingsJSONObject.getJSONArray("fieldValues");
	}

	protected DDMFormInstance getRecordSet(DDMFormInstance formInstance)
		throws PortalException {

		EntityCacheUtil.clearCache();

		formInstance = DDMFormInstanceLocalServiceUtil.getDDMFormInstance(
			formInstance.getFormInstanceId());

		return formInstance;
	}

	protected void setUpUpgradeDDMFormInstanceSettings() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String bundleSymbolicName, String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					register(
						fromSchemaVersionString, toSchemaVersionString,
						upgradeSteps);
				}

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						String className = clazz.getName();

						if (className.contains(_CLASS_NAME)) {
							_upgradeDDMFormInstanceSettings =
								(UpgradeProcess)upgradeStep;
						}
					}
				}

			});
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_3." +
			"UpgradeDDMFormInstanceSettings";

	@Inject(
		filter = "(&(objectClass=com.liferay.dynamic.data.mapping.internal.upgrade.DDMServiceUpgrade))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Group _group;

	private JSONFactory _jsonFactory;
	private UpgradeProcess _upgradeDDMFormInstanceSettings;

}