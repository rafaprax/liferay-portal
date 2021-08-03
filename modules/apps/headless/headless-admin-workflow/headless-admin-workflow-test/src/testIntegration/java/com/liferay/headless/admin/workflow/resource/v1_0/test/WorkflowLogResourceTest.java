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

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.workflow.client.dto.v1_0.Assignee;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowLog;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.AssigneeTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.ObjectReviewedTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowDefinitionTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowInstanceTestUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowTaskTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ResourceBundle;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class WorkflowLogResourceTest extends BaseWorkflowLogResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseWorkflowLogResourceTestCase.setUpClass();

		_resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					"com.liferay.headless.admin.workflow.impl");

		_workflowDefinition =
			WorkflowDefinitionTestUtil.addWorkflowDefinition();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_siteContentReviewerRole = _roleLocalService.getRole(
			testGroup.getCompanyId(), RoleConstants.SITE_CONTENT_REVIEWER);

		_workflowInstance = WorkflowInstanceTestUtil.addWorkflowInstance(
			testGroup.getGroupId(), ObjectReviewedTestUtil.addObjectReviewed(),
			_workflowDefinition);

		_kaleoLogLocalService.deleteKaleoInstanceKaleoLogs(
			_workflowInstance.getId());
	}


	@Override
	protected Long testGetWorkflowInstanceWorkflowLogsPage_getWorkflowInstanceId()
		throws Exception {

		return _workflowInstance.getId();
	}

	protected WorkflowLog
		testGetWorkflowInstanceWorkflowLogsPage_addWorkflowLog(
			Long workflowInstanceId, WorkflowLog workflowLog)
			throws Exception {

			return _kaleoLogLocalService.addNodeEntryKaleoLog();
	}

	private ResourceBundle _getResourceBundle() {
		return _resourceBundleLoader.loadResourceBundle(
			LocaleUtil.getDefault());
	}

	private static ResourceBundleLoader _resourceBundleLoader;
	private static WorkflowDefinition _workflowDefinition;
	private static WorkflowInstance _workflowInstance;

	@Inject
	private Language _language;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	private Role _siteContentReviewerRole;

	private KaleoLogLocalService _kaleoLogLocalService;
}