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
import com.liferay.portal.kernel.json.JSONArray;
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

		_ddmStructureTestHelper = new DDMStructureTestHelper(
			PortalUtil.getClassNameId(_DDL_RECORD_SET_CLASS_NAME), _group);
	}

	@Test
	public void testVerifyInvalidContent() throws Exception {
		try (CaptureAppender captureAppender =
				Log4JLoggerTestUtil.configureLog4JLogger(
						DDMServiceVerifyProcess.class.getName(), Level.WARN)) {

			DDMContent invalidContent = createInvalidContent();

			doVerify();

			List<LoggingEvent> loggingEvents =
				captureAppender.getLoggingEvents();

			Assert.assertFalse(loggingEvents.isEmpty());

			LoggingEvent loggingEvent = loggingEvents.get(0);

			String loggingMessage = String.valueOf(loggingEvent.getMessage());

			Assert.assertTrue(
				StringUtil.contains(
					loggingMessage,
					"There is no such field name defined on DDM Form " +
						"content_2"));

			ThrowableInformation throwableInformation =
				loggingEvent.getThrowableInformation();

			Throwable throwable = throwableInformation.getThrowable();

			Assert.assertSame(
				StorageFieldNameException.class, throwable.getClass());

			deleteContent(invalidContent);
		}
	}

	@Test
	public void testVerifyInvalidStructure() throws Exception {
		try (CaptureAppender captureAppender =
				Log4JLoggerTestUtil.configureLog4JLogger(
					DDMServiceVerifyProcess.class.getName(), Level.ERROR)) {

			DDMStructure invalidDDMStructure = createInvalidStructure();

			doVerify();

			List<LoggingEvent> loggingEvents =
				captureAppender.getLoggingEvents();

			Assert.assertFalse(loggingEvents.isEmpty());

			LoggingEvent loggingEvent = loggingEvents.get(0);

			String loggingMessage = String.valueOf(loggingEvent.getMessage());

			Assert.assertEquals(
				"Invalid characters were defined for field name invalid-name",
				loggingEvent.getMessage());

			ThrowableInformation throwableInformation =
				loggingEvent.getThrowableInformation();

			Throwable throwable = throwableInformation.getThrowable();

			Assert.assertSame(
				DDMFormValidationException.class, throwable.getClass());

			deleteStructure(invalidDDMStructure);
		}
	}

	@Test
	public void testVerifyOrphanedStructureLink() throws Exception {
		DDMStructure structure = addStructure();

		DDMStructureLink structureLink =
			DDMStructureLinkLocalServiceUtil.addStructureLink(
				123, 456, structure.getStructureId());

		long structureLinkId = structureLink.getStructureLinkId();

		deleteStructure(structure);

		Assert.assertNotNull(structureLink);

		doVerify();

		structureLink = DDMStructureLinkLocalServiceUtil.fetchDDMStructureLink(
			structureLinkId);

		Assert.assertNull(structureLink);
	}

	@Test
	public void testVerifyOrphanedTemplateLink() throws Exception {
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

	protected DDMContent addContent() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DDMStructure structure = addStructure();

		String serializedDDMFormValues = read(
			"ddm-verify-process-content-definition.json");

		DDMFormValues ddmFormValues =
			DDMFormValuesJSONDeserializerUtil.deserialize(
				structure.getDDMForm(), serializedDDMFormValues);

		long contentId = StorageEngineUtil.create(
			_group.getCompanyId(), structure.getStructureId(), ddmFormValues,
			serviceContext);

		return DDMContentLocalServiceUtil.getContent(contentId);
	}

	protected DDMStructure addStructure() throws Exception {
		String definition = read(
			"ddm-verify-process-structure-definition.json");

		DDMForm ddmForm = DDMFormJSONDeserializerUtil.deserialize(definition);

		return _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.JSON.toString());
	}

	protected DDMTemplate addTemplate() throws PortalException {
		Map<Locale, String> mapName = new HashMap<>();

		mapName.put(LocaleUtil.getSiteDefault(), "Name");

		String script = "#set ($preferences = $renderRequest.getPreferences())";

		return DDMTemplateLocalServiceUtil.addTemplate(
			TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(DDMStructure.class), 0, 0, null, mapName,
			mapName, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			StringPool.BLANK, TemplateConstants.LANG_TYPE_VM, script, false,
			false, null, null, ServiceContextTestUtil.getServiceContext());
	}

	protected DDMContent createInvalidContent() throws Exception {
		DDMContent content = addContent();

		JSONObject ddmFormValuesJSONObject = JSONFactoryUtil.createJSONObject(
			content.getData());

		JSONArray ddmFormFieldValuesJSONArray =
			ddmFormValuesJSONObject.getJSONArray("fieldValues");

		JSONObject ddmFormFieldValueJSONObject =
			ddmFormFieldValuesJSONArray.getJSONObject(0);

		ddmFormFieldValueJSONObject.put("name", "content_2");

		content.setData(ddmFormValuesJSONObject.toString());

		return DDMContentLocalServiceUtil.updateDDMContent(content);
	}

	protected DDMStructure createInvalidStructure() throws Exception {
		DDMStructure structure = addStructure();

		JSONObject ddmFormJSONObject = JSONFactoryUtil.createJSONObject(
			structure.getDefinition());

		JSONArray ddmFormFieldsJSONArray = ddmFormJSONObject.getJSONArray(
			"fields");

		JSONObject ddmFormFieldJONObject = ddmFormFieldsJSONArray.getJSONObject(
			0);

		ddmFormFieldJONObject.put("name", "invalid-name");

		structure.setDefinition(ddmFormJSONObject.toString());

		return DDMStructureLocalServiceUtil.updateDDMStructure(structure);
	}

	protected void deleteContent(DDMContent content) throws PortalException {
		DDMStorageLink storageLink =
			DDMStorageLinkLocalServiceUtil.getClassStorageLink(
				content.getPrimaryKey());

		DDMStructure structure = storageLink.getStructure();

		StorageEngineUtil.deleteByClass(content.getPrimaryKey());

		deleteStructure(structure);
	}

	protected void deleteStructure(DDMStructure structure)
		throws PortalException {

		DDMStructureLocalServiceUtil.deleteDDMStructure(structure);
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

	private static final String _DDL_RECORD_SET_CLASS_NAME =
		"com.liferay.dynamic.data.lists.model.DDLRecordSet";

	private DDMStructureTestHelper _ddmStructureTestHelper;

	@DeleteAfterTestRun
	private Group _group;

}