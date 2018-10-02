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

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceLocalServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddFormInstanceShouldCreateApprovedFormInstanceVersion()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion latestFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			latestFormInstanceVersion.getStatus());
	}

	@Test
	public void testAddFormInstanceShouldCreateDraftFormInstanceVersion()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion latestFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			latestFormInstanceVersion.getStatus());
	}

	@Test
	public void testUpdateFormInstanceShouldCreateNewFormInstanceVersion1()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		serviceContext.setAttribute(
			"status", WorkflowConstants.STATUS_APPROVED);

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertNotEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			firstFormInstanceVersion.getStatus());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			secondFormInstanceVersion.getStatus());

		Assert.assertEquals(
			secondFormInstanceVersion.getVersion(), formInstance.getVersion());
	}

	@Test
	public void testUpdateFormInstanceShouldCreateNewFormInstanceVersion2()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertNotEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			firstFormInstanceVersion.getStatus());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			secondFormInstanceVersion.getStatus());

		Assert.assertEquals(
			secondFormInstanceVersion.getVersion(), formInstance.getVersion());
	}

	@Test
	public void testUpdateFormInstanceShouldCreateNewFormInstanceVersion3()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			DDMFormInstanceVersionLocalServiceUtil.getLatestFormInstanceVersion(
				formInstance.getFormInstanceId());

		Assert.assertNotEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			firstFormInstanceVersion.getStatus());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			secondFormInstanceVersion.getStatus());

		Assert.assertEquals(
			firstFormInstanceVersion.getVersion(), formInstance.getVersion());
	}

	@Test
	public void testUpdateFormInstanceShouldKeepFormInstanceVersion()
		throws Exception {

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance = addDDMFormInstance(
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			firstFormInstanceVersion.getStatus());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			secondFormInstanceVersion.getStatus());

		Assert.assertEquals(
			firstFormInstanceVersion.getVersion(), formInstance.getVersion());
	}

	protected DDMFormInstance addDDMFormInstance(
			DDMFormValues settingsDDMFormValues, ServiceContext serviceContext)
		throws Exception {

		String definition = read("test-structure.xsd");

		DDMForm ddmForm = toDDMForm(definition);

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		return DDMFormInstanceLocalServiceUtil.addFormInstance(
			TestPropsValues.getUserId(), group.getGroupId(),
			DDMStructureTestHelper.getDefaultLocaleMap("Test Structure"),
			DDMStructureTestHelper.getDefaultLocaleMap(null), ddmForm,
			ddmFormLayout, settingsDDMFormValues, serviceContext);
	}

}