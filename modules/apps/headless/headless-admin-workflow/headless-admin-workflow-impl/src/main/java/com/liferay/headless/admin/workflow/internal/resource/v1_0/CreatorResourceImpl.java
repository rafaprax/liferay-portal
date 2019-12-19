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

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Creator;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskCreators;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.util.ResourceUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.CreatorResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/creator.properties",
	scope = ServiceScope.PROTOTYPE, service = CreatorResource.class
)
public class CreatorResourceImpl extends BaseCreatorResourceImpl {

	@Override
	public Page<Creator> getWorkflowTaskAssignableUsersPage(
			Long workflowTaskId, Pagination pagination)
		throws Exception {

		List<User> users = _workflowTaskManager.getPooledActors(
			contextUser.getCompanyId(), workflowTaskId);

		return Page.of(
			transform(
				ListUtil.subList(
					users, ResourceUtil.getStartPosition(pagination),
					ResourceUtil.getEndPosition(pagination)),
				user -> CreatorUtil.toCreator(_portal, user)),
			pagination, users.size());
	}

	@Override
	public Page<WorkflowTaskCreators> getWorkflowTaskAssignableUsersPage(
			Long[] workflowTaskIds)
		throws Exception {

		List<WorkflowTaskCreators> workflowTaskCreators = new ArrayList<>();

		Set<User> commonPooledActors = null;

		for (Long workflowTaskId : workflowTaskIds) {
			List<User> pooledActors = _workflowTaskManager.getPooledActors(
				contextUser.getCompanyId(), workflowTaskId);

			if (commonPooledActors == null) {
				commonPooledActors = new HashSet<>(pooledActors);
			}
			else {
				commonPooledActors.retainAll(pooledActors);
			}

			workflowTaskCreators.add(
				_createWorkflowTaskCreators(pooledActors, workflowTaskId));
		}

		if (workflowTaskCreators.size() > 1) {
			workflowTaskCreators.add(
				_createWorkflowTaskCreators(commonPooledActors, 0L));
		}

		return Page.of(workflowTaskCreators);
	}

	private WorkflowTaskCreators _createWorkflowTaskCreators(
		Collection<User> users, Long workflowTaskId) {

		return new WorkflowTaskCreators() {
			{
				creators = transformToArray(
					users, user -> CreatorUtil.toCreator(_portal, user),
					Creator.class);
				taskId = workflowTaskId;
			}
		};
	}

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}