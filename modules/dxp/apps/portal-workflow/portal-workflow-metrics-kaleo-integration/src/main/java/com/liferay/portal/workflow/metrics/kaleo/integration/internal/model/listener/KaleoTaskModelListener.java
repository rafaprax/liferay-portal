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

package com.liferay.portal.workflow.metrics.kaleo.integration.internal.model.listener;

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.metrics.index.NodeWorkflowMetricsIndexer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = ModelListener.class)
public class KaleoTaskModelListener extends BaseModelListener<KaleoTask> {

	@Override
	public void onAfterCreate(KaleoTask kaleoTask) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoTask.getKaleoDefinitionVersionId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(kaleoDefinitionVersion.getCompanyId());

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinitionVersion.getName(), serviceContext);

		_nodeWorkflowMetricsIndexer.add(
			kaleoTask.getCompanyId(), kaleoTask.getCreateDate(), false,
			kaleoTask.getModifiedDate(), kaleoTask.getName(),
			kaleoTask.getKaleoTaskId(), kaleoDefinition.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getVersion(), false, NodeType.TASK.name());
	}

	@Override
	public void onAfterRemove(KaleoTask kaleoTask) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoTask.getKaleoDefinitionVersionId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(kaleoDefinitionVersion.getCompanyId());

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinitionVersion.getName(), serviceContext);

		_nodeWorkflowMetricsIndexer.delete(
			kaleoTask.getCompanyId(), kaleoTask.getKaleoTaskId(),
			kaleoDefinition.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getVersion());
	}

	@Reference
	private NodeWorkflowMetricsIndexer _nodeWorkflowMetricsIndexer;

	@Reference
	protected KaleoDefinitionLocalService kaleoDefinitionLocalService;

	@Reference
	protected KaleoDefinitionVersionLocalService
		kaleoDefinitionVersionLocalService;

}