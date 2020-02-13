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

package com.liferay.portal.workflow.metrics.internal.search.index;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.metrics.index.NodeWorkflowMetricsIndexer;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = NodeWorkflowMetricsIndexer.class)
public class NodeWorkflowMetricsIndexerImpl extends BaseWorkflowMetricsIndexer implements NodeWorkflowMetricsIndexer{

	@Override
	public String getIndexName() {
		return "workflow-metrics-nodes";
	}

	@Override
	public String getIndexType() {
		return "WorkflowMetricsNodeType";
	}

	@Override
	public void reindex(long companyId) throws PortalException {
		_reindexIndexWithKaleoNode(companyId);
		_reindexIndexWithKaleoTask(companyId);
	}

	private Document _createDocument(
		long companyId, Date createDate, boolean initial,
		long processId, String processVersion, Date modifiedDate, String name,
		long nodeId, boolean terminal, String type) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(companyId, nodeId, processId, processVersion)
		).setLong(
			"companyId", companyId
		).setDate(
			"createDate", formatDate(createDate)
		).setBoolean(
			"deleted", false
		).setBoolean(
			"initial", initial
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setString(
			"name", name
		).setLong(
			"nodeId", nodeId
		).setLong(
			"processId", processId
		).setBoolean(
			"terminal", terminal
		).setString(
			"type", type
		).setString(
			"version", processVersion
		);

		return documentBuilder.build();
	}

	private Document _createWorkflowMetricsTokenDocument(
		long companyId, long processId, long taskId, String taskName,
		String processVersion) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(companyId, processId, processVersion, taskId)
		).setLong(
			"companyId", companyId
		).setBoolean(
			"completed", false
		).setBoolean(
			"deleted", false
		).setLong(
			"instanceId", 0L
		).setBoolean(
			"instanceCompleted", false
		).setLong(
			"processId", processId
		).setLong(
			"taskId", taskId
		).setString(
			"taskName", taskName
		).setLong(
			"tokenId", 0L
		).setString(
			"version", processVersion
		);

		return documentBuilder.build();
	}

	private Document _creatWorkflowMetricsSLATaskResultDocument(
		long companyId, long processId, long taskId, String taskName) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, processId, taskId));
		
		documentBuilder.setLong("companyId", companyId);
		documentBuilder.setBoolean("deleted", false);
		documentBuilder.setBoolean("instanceCompleted", false);
		documentBuilder.setLong("instanceId", 0L);
		documentBuilder.setLong("processId", processId);
		documentBuilder.setLong("slaDefinitionId", 0L);
		documentBuilder.setLong("taskId", taskId);
		documentBuilder.setString("taskName", taskName);

		return documentBuilder.build();
	}

	private void _reindexIndexWithKaleoNode(long companyId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			kaleoNodeLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));

				Property typeProperty = PropertyFactoryUtil.forName("type");

				dynamicQuery.add(typeProperty.eq(NodeType.STATE.name()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoNode kaleoNode) -> workflowMetricsPortalExecutor.execute(
				() -> {
					KaleoDefinition kaleoDefinition =
						getKaleoDefinition(
							kaleoNode.getKaleoDefinitionVersionId());

					KaleoDefinitionVersion kaleoDefinitionVersion =
						getKaleoDefinitionVersion(
							kaleoNode.getKaleoDefinitionVersionId());

					_createDocument(
						kaleoNode.getCompanyId(), kaleoNode.getCreateDate(),
						kaleoNode.isInitial(),
						kaleoDefinition.getKaleoDefinitionId(),
						kaleoDefinitionVersion.getVersion(),
						kaleoNode.getModifiedDate(), kaleoNode.getName(),
						kaleoNode.getKaleoNodeId(), kaleoNode.isTerminal(),
						kaleoNode.getType());
				}));

		actionableDynamicQuery.performActions();
	}

	private void _reindexIndexWithKaleoTask(long companyId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			kaleoTaskLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoTask kaleoTask) -> workflowMetricsPortalExecutor.execute(
				() -> {
					KaleoDefinition kaleoDefinition =
						getKaleoDefinition(
							kaleoTask.getKaleoDefinitionVersionId());

					KaleoDefinitionVersion kaleoDefinitionVersion =
						getKaleoDefinitionVersion(
							kaleoTask.getKaleoDefinitionVersionId());

					add(
						kaleoTask.getCompanyId(), kaleoTask.getCreateDate(),
						false, kaleoTask.getModifiedDate(), kaleoTask.getName(),
						kaleoTask.getKaleoTaskId(),
						kaleoDefinition.getKaleoDefinitionId(),
						kaleoDefinitionVersion.getVersion(),false,
						NodeType.TASK.name());
				}));

		actionableDynamicQuery.performActions();
	}

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

	@Reference
	private TokenWorkflowMetricsIndexer _tokenWorkflowMetricsIndexer;

	@Override
	public Document add(
		long companyId, Date createDate, boolean initial, Date modifiedDate,
		String name, long nodeId, long processId, String processVersion,
		boolean terminal, String type) {

		if (searchEngineAdapter == null) {
			return null;
		}

		Document document = _createDocument(
			companyId, createDate, initial, processId, processVersion,
			modifiedDate, name, nodeId, terminal, type);

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		if (Objects.equals(document.getString("type"), "TASK")) {
			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					_slaTaskResultWorkflowMetricsIndexer.getIndexName(),
					_creatWorkflowMetricsSLATaskResultDocument(
						document.getLong("companyId"),
						document.getLong("processId"),
						document.getLong("nodeId"),
						document.getString("name"))) {

					{
						setType(
							_slaTaskResultWorkflowMetricsIndexer.
								getIndexType());
					}
				});

			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					_tokenWorkflowMetricsIndexer.getIndexName(),
					_createWorkflowMetricsTokenDocument(
						document.getLong("companyId"),
						document.getLong("processId"),
						document.getLong("nodeId"),
						document.getString("name"),
						document.getString("version"))) {

					{
						setType(_tokenWorkflowMetricsIndexer.getIndexType());
					}
				});
		}

		bulkDocumentRequest.addBulkableDocumentRequest(
			new IndexDocumentRequest(getIndexName(), document) {
				{
					setType(getIndexType());
				}
			});

		if (PortalRunMode.isTestMode()) {
			bulkDocumentRequest.setRefresh(true);
		}

		workflowMetricsPortalExecutor.execute(
			() -> searchEngineAdapter.execute(bulkDocumentRequest));

		return document;
	}

	@Override
	public void delete(
		long companyId, long nodeId, long processId, String processVersion) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(companyId, nodeId, processId, processVersion));

		workflowMetricsPortalExecutor.execute(
			() -> deleteDocument(documentBuilder));
	}

}