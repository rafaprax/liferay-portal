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

package com.liferay.dynamic.data.mapping.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldNameException;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializerUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializerUtil;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageEngineUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.dynamic.data.mapping.verify.DDMServiceVerifyProcess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.test.log.CaptureAppender;
import com.liferay.portal.test.log.Log4JLoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.BaseVerifyProcessTestCase;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class DDMServiceVerifyProcessTest extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmStructureTestHelper = new DDMStructureTestHelper(_group);
	}

	@Test
	public void testDeleteDDMStructureLinkWithoutDDMStructure()
		throws Exception {

		DDMStructure ddmStructure = addStructure();

		DDMStructureLink ddmStructureLink =
			DDMStructureLinkLocalServiceUtil.addStructureLink(
				123, 456, ddmStructure.getStructureId());

		long structureLinkId = ddmStructureLink.getStructureLinkId();

		deleteDDMStructure(ddmStructure);

		Assert.assertNotNull(ddmStructureLink);

		doVerify();

		ddmStructureLink =
			DDMStructureLinkLocalServiceUtil.fetchDDMStructureLink(
				structureLinkId);

		Assert.assertNull(ddmStructureLink);
	}

	@Test
	public void testDeleteDDMTemplateLinkWithoutDDMTemplate() throws Exception {
		DDMTemplate ddmTemplate = addTemplate();

		DDMTemplateLink ddmTemplateLink =
			DDMTemplateLinkLocalServiceUtil.addTemplateLink(
				123, 456, ddmTemplate.getPrimaryKey());

		long templateLinkId = ddmTemplateLink.getTemplateLinkId();

		DDMTemplateLocalServiceUtil.deleteDDMTemplate(ddmTemplate);

		Assert.assertNotNull(ddmTemplateLink);

		doVerify();

		ddmTemplateLink = DDMTemplateLinkLocalServiceUtil.fetchDDMTemplateLink(
			templateLinkId);

		Assert.assertNull(ddmTemplateLink);
	}

	@Test
	public void testInvalidDDMContent() throws Exception {
		try (CaptureAppender captureAppender =
				Log4JLoggerTestUtil.configureLog4JLogger(
						DDMServiceVerifyProcess.class.getName(), Level.ERROR)) {

			DDMContent invalidDDMContent = createInvalidDDMContent();

			doVerify();

			List<LoggingEvent> loggingEvents =
				captureAppender.getLoggingEvents();

			Assert.assertFalse(loggingEvents.isEmpty());

			LoggingEvent loggingEvent = loggingEvents.get(0);

			Assert.assertEquals(
				"There is no such field name defined on DDM Form content_2",
				loggingEvent.getMessage());

			ThrowableInformation throwableInformation =
				loggingEvent.getThrowableInformation();

			Throwable throwable = throwableInformation.getThrowable();

			Assert.assertSame(
				StorageFieldNameException.class, throwable.getClass());

			deleteDDMContent(invalidDDMContent);
		}
	}

	@Test
	public void testInvalidDDMStructure() throws Exception {
		try (CaptureAppender captureAppender =
				Log4JLoggerTestUtil.configureLog4JLogger(
					DDMServiceVerifyProcess.class.getName(), Level.ERROR)) {

			DDMStructure invalidDDMStructure = createInvalidDDMStructure();

			doVerify();

			List<LoggingEvent> loggingEvents =
				captureAppender.getLoggingEvents();

			Assert.assertFalse(loggingEvents.isEmpty());

			LoggingEvent loggingEvent = loggingEvents.get(0);

			Assert.assertEquals(
				"Invalid characters were defined for field name invalid-name",
				loggingEvent.getMessage());

			ThrowableInformation throwableInformation =
				loggingEvent.getThrowableInformation();

			Throwable throwable = throwableInformation.getThrowable();

			Assert.assertSame(
				DDMFormValidationException.class, throwable.getClass());

			deleteDDMStructure(invalidDDMStructure);
		}
	}

	protected VerifyProcess getVerifyProcess() {
		Registry registry = RegistryUtil.getRegistry();

		return registry.getService(DDMServiceVerifyProcess.class);
	}

	protected String read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/dynamic/data/mapping/dependencies/" + fileName);
	}

	private DDMContent addDDMContent() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DDMStructure ddmStructure = addStructure();

		String serializedDDMFormValues = read(
			"ddm-verify-process-content-definition.json");

		DDMFormValues ddmFormValues =
			DDMFormValuesJSONDeserializerUtil.deserialize(
				ddmStructure.getDDMForm(), serializedDDMFormValues);

		long ddmContentId = StorageEngineUtil.create(
			_group.getCompanyId(), ddmStructure.getStructureId(), ddmFormValues,
			serviceContext);

		DDMContent ddmContent = DDMContentLocalServiceUtil.getDDMContent(
			ddmContentId);

		return ddmContent;
	}

	private DDMStructure addStructure() throws Exception {
		String definition = read(
			"ddm-verify-process-structure-definition.json");

		DDMForm ddmForm = DDMFormJSONDeserializerUtil.deserialize(definition);

		return _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.JSON.toString());
	}

	private DDMTemplate addTemplate() throws PortalException {
		Map<Locale, String> mapName = new HashMap<>();

		mapName.put(LocaleUtil.getSiteDefault(), "Name");

		long classNameId = PortalUtil.getClassNameId(DDMStructure.class);

		long classPK = 0;

		long sourceClassNameId = 0;

		String language = TemplateConstants.LANG_TYPE_VM;

		String type = DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY;

		String mode = StringPool.BLANK;

		String script = "#set ($preferences = $renderRequest.getPreferences())";

		return DDMTemplateLocalServiceUtil.addTemplate(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			classPK, sourceClassNameId, null, mapName, mapName, type, mode,
			language, script, false, false, null, null,
			ServiceContextTestUtil.getServiceContext());
	}

	private DDMContent createInvalidDDMContent() throws Exception {
		DDMContent ddmContent = addDDMContent();

		JSONObject ddmFormValuesJSON = JSONFactoryUtil.createJSONObject(
			ddmContent.getData());

		JSONObject fieldJSON = ddmFormValuesJSON.getJSONArray(
			"fieldValues").getJSONObject(0);

		fieldJSON.put("name", "content_2");

		ddmContent.setData(ddmFormValuesJSON.toString());

		return DDMContentLocalServiceUtil.updateDDMContent(ddmContent);
	}

	private DDMStructure createInvalidDDMStructure() throws Exception {
		DDMStructure ddmStructure = addStructure();

		JSONObject ddmFormJSON = JSONFactoryUtil.createJSONObject(
			ddmStructure.getDefinition());

		JSONObject fieldJSON = ddmFormJSON.getJSONArray(
			"fields").getJSONObject(0);

		fieldJSON.put("name", "invalid-name");

		ddmStructure.setDefinition(ddmFormJSON.toString());

		return DDMStructureLocalServiceUtil.updateDDMStructure(ddmStructure);
	}

	private void deleteDDMContent(DDMContent ddmContent)
		throws PortalException {

		DDMStorageLink ddmStorageLink =
			DDMStorageLinkLocalServiceUtil.getClassStorageLink(
				ddmContent.getPrimaryKey());

		DDMStructure ddmStructure = ddmStorageLink.getStructure();

		StorageEngineUtil.deleteByClass(ddmContent.getPrimaryKey());

		deleteDDMStructure(ddmStructure);
	}

	private void deleteDDMStructure(DDMStructure ddmStructure)
		throws StorageException {

		DDMStructureLocalServiceUtil.deleteDDMStructure(ddmStructure);
	}

	private DDMStructureTestHelper _ddmStructureTestHelper;

	@DeleteAfterTestRun
	private Group _group;

}