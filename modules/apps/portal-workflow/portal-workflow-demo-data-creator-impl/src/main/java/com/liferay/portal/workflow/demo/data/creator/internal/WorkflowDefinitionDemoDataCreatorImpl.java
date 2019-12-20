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

package com.liferay.portal.workflow.demo.data.creator.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.demo.data.creator.WorkflowDefinitionDemoDataCreator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = WorkflowDefinitionDemoDataCreator.class)
public class WorkflowDefinitionDemoDataCreatorImpl
	implements WorkflowDefinitionDemoDataCreator {

	@Override
	public WorkflowDefinition create(long companyId) throws WorkflowException {
		try {
			WorkflowDefinition workflowDefinition =
				_workflowDefinitionManager.getWorkflowDefinition(
					companyId, "Auto Insurance Application", 1);

			if (!workflowDefinition.isActive()) {
				workflowDefinition = _workflowDefinitionManager.updateActive(
					workflowDefinition.getCompanyId(),
					UserConstants.USER_ID_DEFAULT, workflowDefinition.getName(),
					workflowDefinition.getVersion(), true);
			}

			return workflowDefinition;
		}
		catch (WorkflowException we) {
			String content = StringUtil.read(
				WorkflowDefinitionDemoDataCreatorImpl.class,
				"dependencies/auto-insurance-application-definition.xml");

			WorkflowDefinition workflowDefinition =
				_workflowDefinitionManager.deployWorkflowDefinition(
					companyId, UserConstants.USER_ID_DEFAULT,
					"Auto Insurance Application", "Auto Insurance Application",
					content.getBytes());

			_workflowDefinitions.add(workflowDefinition);

			return workflowDefinition;
		}
	}

	@Override
	public void deleted() throws PortalException {
		for (WorkflowDefinition workflowDefinition : _workflowDefinitions) {
			_workflowDefinitions.remove(workflowDefinition);

			_workflowDefinitionManager.updateActive(
				workflowDefinition.getCompanyId(),
				UserConstants.USER_ID_DEFAULT, workflowDefinition.getName(),
				workflowDefinition.getVersion(), false);

			_workflowDefinitionManager.undeployWorkflowDefinition(
				workflowDefinition.getCompanyId(),
				UserConstants.USER_ID_DEFAULT, workflowDefinition.getName(),
				workflowDefinition.getVersion());
		}
	}

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	private final List<WorkflowDefinition> _workflowDefinitions =
		new CopyOnWriteArrayList<>();

}