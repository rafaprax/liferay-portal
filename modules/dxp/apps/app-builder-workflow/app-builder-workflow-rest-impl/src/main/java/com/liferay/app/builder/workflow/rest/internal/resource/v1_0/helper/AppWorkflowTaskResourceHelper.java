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

package com.liferay.app.builder.workflow.rest.internal.resource.v1_0.helper;

import com.liferay.app.builder.model.AppBuilderApp;
import com.liferay.app.builder.workflow.rest.dto.v1_0.AppWorkflowTask;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.definition.export.DefinitionExporter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = AppWorkflowTaskResourceHelper.class)
public class AppWorkflowTaskResourceHelper {

	public WorkflowDefinition createWorkflowDefinition(
			AppBuilderApp appBuilderApp, AppWorkflowTask[] appWorkflowTasks,
			long companyId, long userId)
		throws PortalException {

		return _workflowDefinitionManager.deployWorkflowDefinition(
			companyId, userId, _getWorkflowDefinnitionName(appBuilderApp),
			appBuilderApp.getUuid(),
			_toWorkflowDefinitionBytes(appBuilderApp, appWorkflowTasks));
	}

	private String _getWorkflowDefinnitionName(AppBuilderApp appBuilderApp) {
		return "Workflow definition for App with the id = " +
			   appBuilderApp.getAppBuilderAppId();
	}

	private byte[]  _toWorkflowDefinitionBytes(
			AppBuilderApp appBuilderApp, AppWorkflowTask[] appWorkflowTasks)
		throws PortalException {

		Definition definition = new Definition(appBuilderApp.getUuid(),
			StringPool.BLANK, StringPool.BLANK, 0);

		for(AppWorkflowTask appWorkflowTask: appWorkflowTasks) {

		}

		String content = _definitionExporter.export(definition);

		return content.getBytes();
	}

	@Reference
	private DefinitionExporter _definitionExporter;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;
}