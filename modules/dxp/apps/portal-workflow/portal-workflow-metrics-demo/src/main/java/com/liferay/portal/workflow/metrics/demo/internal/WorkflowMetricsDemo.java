/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.demo.internal;

import com.liferay.blogs.demo.data.creator.BlogsEntryDemoDataCreator;
import com.liferay.dynamic.data.mapping.demo.data.creator.DDMFormInstanceDemoDataCreator;
import com.liferay.dynamic.data.mapping.demo.data.creator.DDMFormInstanceRecordDemoDataCreator;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.workflow.demo.data.creator.WorkflowDefinitionDemoDataCreator;
import com.liferay.users.admin.demo.data.creator.OmniAdminUserDemoDataCreator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class WorkflowMetricsDemo extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		WorkflowDefinition workflowDefinition =
			_workflowDefinitionDemoDataCreator.create(company.getCompanyId());

		List<Long> userIds = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			User user = _omniAdminUserDemoDataCreator.create(
				company.getCompanyId());

			userIds.add(user.getUserId());
		}

		List<Long> insuranceAgentUserIds = new ArrayList<>();

		Role insuranceAgentRole = _roleLocalService.getRole(
			company.getCompanyId(), "Insurance agent");

		for (int i = 0; i < 10; i++) {
			User user = _omniAdminUserDemoDataCreator.create(
				company.getCompanyId());

			_userLocalService.addRoleUser(
				insuranceAgentRole.getRoleId(), user.getUserId());

			insuranceAgentUserIds.add(user.getUserId());
		}

		List<Long> underwriterUserIds = new ArrayList<>();

		Role underwriterRole = _roleLocalService.getRole(
			company.getCompanyId(), "Underwriter");

		for (int i = 0; i < 10; i++) {
			User user = _omniAdminUserDemoDataCreator.create(
				company.getCompanyId());

			_userLocalService.addRoleUser(
				underwriterRole.getRoleId(), user.getUserId());

			underwriterUserIds.add(user.getUserId());
		}

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), "Guest");

		DDMFormInstance formInstance = _ddmFormInstanceDemoDataCreator.create(
			company.getCompanyId(), group.getGroupId());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			UserConstants.USER_ID_DEFAULT, company.getCompanyId(),
			group.getGroupId(), DDMFormInstance.class.getName(),
			formInstance.getFormInstanceId(), 0, workflowDefinition.getName(),
			workflowDefinition.getVersion());

		for (int i = 0; i < 30; i++) {
			_ddmFormInstanceRecordDemoDataCreator.create(
				company.getCompanyId(), group.getGroupId(),
				_getRandomLong(userIds), formInstance);
		}

		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksByRole(
					company.getCompanyId(), insuranceAgentRole.getRoleId(),
					false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Long userId = _getRandomLong(insuranceAgentUserIds);

			_workflowTaskManager.assignWorkflowTaskToUser(
				company.getCompanyId(), UserConstants.USER_ID_DEFAULT,
				workflowTask.getWorkflowTaskId(), userId, StringPool.BLANK,
				null, null);

			List<String> transitionNames =
				_workflowTaskManager.getNextTransitionNames(
					company.getCompanyId(), userId,
					workflowTask.getWorkflowTaskId());

			_workflowTaskManager.completeWorkflowTask(
				group.getCompanyId(), userId, workflowTask.getWorkflowTaskId(),
				_getRandomString(transitionNames), StringPool.BLANK, null);
		}

		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksByRole(
					company.getCompanyId(), underwriterRole.getRoleId(), false,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Long userId = _getRandomLong(underwriterUserIds);

			_workflowTaskManager.assignWorkflowTaskToUser(
				group.getCompanyId(), UserConstants.USER_ID_DEFAULT,
				workflowTask.getWorkflowTaskId(), userId, StringPool.BLANK,
				null, null);

			List<String> transitionNames =
				_workflowTaskManager.getNextTransitionNames(
					company.getCompanyId(), userId,
					workflowTask.getWorkflowTaskId());

			_workflowTaskManager.completeWorkflowTask(
				group.getCompanyId(), userId, workflowTask.getWorkflowTaskId(),
				_getRandomString(transitionNames), StringPool.BLANK, null);
		}

		for (Long userId : userIds) {
			for (WorkflowTask workflowTask :
					_workflowTaskManager.getWorkflowTasksByUser(
						company.getCompanyId(), userId, false,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

				_workflowTaskManager.assignWorkflowTaskToUser(
					company.getCompanyId(), UserConstants.USER_ID_DEFAULT,
					workflowTask.getWorkflowTaskId(), userId, StringPool.BLANK,
					null, null);

				List<String> transitionNames =
					_workflowTaskManager.getNextTransitionNames(
						company.getCompanyId(), userId,
						workflowTask.getWorkflowTaskId());

				_workflowTaskManager.completeWorkflowTask(
					group.getCompanyId(), userId,
					workflowTask.getWorkflowTaskId(),
					_getRandomString(transitionNames), StringPool.BLANK, null);
			}
		}

		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksByRole(
					company.getCompanyId(), underwriterRole.getRoleId(), false,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Long userId = _getRandomLong(underwriterUserIds);

			_workflowTaskManager.assignWorkflowTaskToUser(
				group.getCompanyId(), UserConstants.USER_ID_DEFAULT,
				workflowTask.getWorkflowTaskId(), userId, StringPool.BLANK,
				null, null);

			List<String> transitionNames =
				_workflowTaskManager.getNextTransitionNames(
					company.getCompanyId(), userId,
					workflowTask.getWorkflowTaskId());

			_workflowTaskManager.completeWorkflowTask(
				group.getCompanyId(), userId, workflowTask.getWorkflowTaskId(),
				_getRandomString(transitionNames), StringPool.BLANK, null);
		}
	}

	@Deactivate
	protected void deactivate() throws PortalException {
		_omniAdminUserDemoDataCreator.delete();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	private Long _getRandomLong(List<Long> list) {
		return list.get(RandomUtil.nextInt(list.size()));
	}

	private String _getRandomString(List<String> list) {
		return list.get(RandomUtil.nextInt(list.size()));
	}

	@Reference
	private BlogsEntryDemoDataCreator _blogsEntryDemoDataCreator;

	@Reference
	private DDMFormInstanceDemoDataCreator _ddmFormInstanceDemoDataCreator;

	@Reference
	private DDMFormInstanceRecordDemoDataCreator
		_ddmFormInstanceRecordDemoDataCreator;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OmniAdminUserDemoDataCreator _omniAdminUserDemoDataCreator;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionDemoDataCreator
		_workflowDefinitionDemoDataCreator;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}