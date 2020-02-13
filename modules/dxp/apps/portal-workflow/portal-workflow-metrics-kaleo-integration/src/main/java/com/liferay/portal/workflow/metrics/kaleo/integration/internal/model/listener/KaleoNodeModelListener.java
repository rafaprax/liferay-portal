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
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.metrics.index.NodeWorkflowMetricsIndexer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = ModelListener.class)
public class KaleoNodeModelListener extends BaseModelListener<KaleoNode> {

	@Override
	public void onAfterCreate(KaleoNode kaleoNode) {
		if (!Objects.equals(kaleoNode.getType(), NodeType.STATE.name())) {
			return;
		}

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoNode.getKaleoDefinitionVersionId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(kaleoDefinitionVersion.getCompanyId());

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinitionVersion.getName(), serviceContext);

		_nodeWorkflowMetricsIndexer.add(
			kaleoNode.getCompanyId(), kaleoNode.getCreateDate(),
			kaleoNode.getInitial(), kaleoNode.getModifiedDate(),
			kaleoNode.getName(), kaleoNode.getKaleoNodeId(),
			kaleoDefinition.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getVersion(), kaleoNode.getTerminal(),
			kaleoNode.getType());
	}

	@Override
	public void onAfterRemove(KaleoNode kaleoNode) {
		if (!Objects.equals(kaleoNode.getType(), NodeType.STATE.name())) {
			return;
		}

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoNode.getKaleoDefinitionVersionId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(kaleoDefinitionVersion.getCompanyId());

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinitionVersion.getName(), serviceContext);

		_nodeWorkflowMetricsIndexer.delete(
			kaleoNode.getCompanyId(), kaleoNode.getKaleoNodeId(),
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