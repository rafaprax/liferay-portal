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

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.test;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

/**
 * @author Rodrigo Paulino
 */
public abstract class BaseDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		_originalThemeDisplayLocale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

		setUpOptional();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setThemeDisplayLocale(_originalThemeDisplayLocale);

		tearDownOptional();
	}

	protected DDMFormInstance addOneFieldOneLocaleFormInstance(
			Locale locale, String name, String description, String fieldName,
			String fieldLabel, String fieldType, String fieldDataType,
			boolean fieldLocalizable, boolean fieldRepeatable,
			boolean fieldRequired, String fieldTip, String fieldPredefinedValue,
			String fieldPlaceHolder, String fieldToolTip, String fieldOption,
			String fieldDDMFormFieldValidationErrorMessage,
			String fieldDDMFormFieldValidationExpression, String fieldNamespace,
			String fieldIndexType, boolean fieldAutocomplete,
			String fieldDataSourceType,
			JSONArray fieldDDMDataProviderInstanceId,
			JSONArray fieldDDMDataProviderInstanceOutput,
			String fieldDisplayStyle, boolean fieldReadOnly,
			boolean fieldShowLabel, String fieldVisibilityExpression,
			String pageTitle, String pageDescription, int columnSize,
			String paginationMode, long groupId, long userId)
		throws PortalException {

		Map<Locale, String> nameMap = createOneLocaleStringMap(locale, name);

		Map<Locale, String> descriptionMap = createOneLocaleStringMap(
			locale, description);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(locale), locale);

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			fieldName, fieldLabel, fieldType, fieldDataType, fieldLocalizable,
			fieldRepeatable, fieldRequired, fieldTip, fieldPredefinedValue,
			fieldPlaceHolder, fieldToolTip, fieldOption,
			fieldDDMFormFieldValidationErrorMessage,
			fieldDDMFormFieldValidationExpression, fieldNamespace,
			fieldIndexType, fieldAutocomplete, fieldDataSourceType,
			fieldDDMDataProviderInstanceId, fieldDDMDataProviderInstanceOutput,
			fieldDisplayStyle, fieldReadOnly, fieldShowLabel,
			fieldVisibilityExpression);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		LocalizedValue pageTitleLocalizedValue =
			createOneLocaleStringLocalizedValue(locale, pageTitle);

		ddmFormLayoutPage.setTitle(pageTitleLocalizedValue);

		LocalizedValue pageDescriptionLocalizedValue =
			createOneLocaleStringLocalizedValue(locale, pageDescription);

		ddmFormLayoutPage.setDescription(pageDescriptionLocalizedValue);

		for (DDMFormField field : ddmForm.getDDMFormFields()) {
			DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

			ddmFormLayoutRow.addDDMFormLayoutColumn(
				new DDMFormLayoutColumn(columnSize, field.getName()));

			ddmFormLayoutPage.addDDMFormLayoutRow(ddmFormLayoutRow);
		}

		ddmFormLayout.addDDMFormLayoutPage(ddmFormLayoutPage);

		ddmFormLayout.setDefaultLocale(locale);
		ddmFormLayout.setPaginationMode(paginationMode);

		DDMForm settingsDDMForm = DDMFormFactory.create(
			DDMFormInstanceSettings.class);

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				userId, groupId, nameMap, descriptionMap, ddmForm,
				ddmFormLayout, settingsDDMFormValues, serviceContext);

		return ddmFormInstance;
	}

	protected DDMFormInstanceRecord addOneLocaleFormInstanceRecord(
			DDMFormInstance ddmFormInstance, Locale locale, String fieldValue,
			String fieldValueInstanceId, String fieldName, long groupId,
			long userId, int status, boolean validateDDMFormValues,
			int workflowAction)
		throws PortalException {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmFormInstance.getDDMForm());

		Value localizedValue = createOneLocaleStringLocalizedValue(
			locale, fieldValue);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				fieldValueInstanceId, fieldName, localizedValue);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		serviceContext.setAttribute("status", status);
		serviceContext.setAttribute(
			"validateDDMFormValues", validateDDMFormValues);

		serviceContext.setWorkflowAction(workflowAction);

		DDMFormInstanceRecord ddmFormInstanceRecord =
			DDMFormInstanceRecordLocalServiceUtil.addFormInstanceRecord(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				ddmFormInstance.getFormInstanceId(), ddmFormValues,
				serviceContext);

		return ddmFormInstanceRecord;
	}

	protected LocalizedValue createOneLocaleStringLocalizedValue(
		Locale locale, String string) {

		LocalizedValue title = new LocalizedValue(locale);

		title.addString(locale, string);

		return title;
	}

	protected Map<Locale, String> createOneLocaleStringMap(
		Locale locale, String string) {

		Map<Locale, String> nameMap = new HashMap<>();

		if (Validator.isNotNull(string)) {
			nameMap.put(locale, string);
		}

		return nameMap;
	}

	protected ThemeDisplay createThemeDisplay(
			long companyId, PermissionChecker permissionChecker, User user)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(companyId);

		themeDisplay.setCompany(company);

		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	protected String getBasePath() {
		return "com/liferay/dynamic/data/mapping/dependencies/web/internal" +
			"/display/context/";
	}

	protected String read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(), getBasePath() + fileName);
	}

	protected abstract void setUpOptional() throws Exception;

	protected abstract void tearDownOptional();

	private Locale _originalThemeDisplayLocale;

}