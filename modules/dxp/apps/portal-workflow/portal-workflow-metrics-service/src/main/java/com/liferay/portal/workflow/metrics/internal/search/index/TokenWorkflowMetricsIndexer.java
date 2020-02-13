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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.metrics.index.TaskWorkflowMetricsIndexer;

import java.text.ParseException;

import java.time.Duration;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = TokenWorkflowMetricsIndexer.class)
public class TokenWorkflowMetricsIndexer extends BaseWorkflowMetricsIndexer implements TaskWorkflowMetricsIndexer {

	@Override
	public String getIndexName() {
		return "workflow-metrics-tokens";
	}

	@Override
	public String getIndexType() {
		return "WorkflowMetricsTokenType";
	}

	@Override
	public void reindex(long companyId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			kaleoTaskInstanceTokenLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoTaskInstanceToken kaleoTaskInstanceToken) ->
				workflowMetricsPortalExecutor.execute(
					() -> {}addDocument(createDocument(kaleoTaskInstanceToken))));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void updateDocument(Document document) {
		super.updateDocument(document);

		BooleanQuery booleanQuery = queries.booleanQuery();

		booleanQuery.addMustQueryClauses(
			queries.term(
				"companyId", document.getLong("companyId")),
			queries.term(
				"instanceId", document.getLong("instanceId")),
			queries.term(
				"processId", document.getLong("processId")),
			queries.term("taskId", document.getLong("taskId")),
			queries.term(
				"tokenId", document.getLong("tokenId")));

		_slaTaskResultWorkflowMetricsIndexer.updateDocuments(
			documentImpl -> {
				DocumentBuilder documentoBuilder =
					documentBuilderFactory.builder();

				if (!Objects.isNull(document.getLong("assigneeId"))) {
					documentoBuilder.setLong(
						"assigneeId", document.getLong("assigneeId"));
				}

				if (!Objects.isNull(document.getDate("completionDate"))) {
					documentoBuilder.setDate(
						"completionDate",
						document.getDate("completionDate"));
				}

				if (!Objects.isNull(document.getLong("completionUserId"))) {
					documentoBuilder.setLong(
						"completionUserId",
						document.getLong("completionUserId"));
				}
				
				if (!Objects.isNull(document.getBoolean("instanceCompleted"))) {
					documentoBuilder.setBoolean(
						"instanceCompleted",
						document.getBoolean("instanceCompleted"));
				}

				documentoBuilder.setString(
					Field.UID, documentImpl.getString(Field.UID));

				return documentoBuilder.build();
			},
			booleanQuery);
	}

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

	@Override
	public Document add(
		long companyId, long taskId, String className, long classPK,
		Date createDate, Date modifiedDate, long instanceId, String name,
		long processId, String processVersion, long tokenId, long userId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(
				companyId, instanceId, processId, processVersion, taskId,
				tokenId)
		).setString(
			"className", className
		).setLong(
			"classPK", classPK
		).setLong(
			"companyId", companyId
		).setDate(
			"createDate", formatDate(createDate)
		).setBoolean(
			"deleted", false
		).setBoolean(
			"instanceCompleted", false
		).setLong(
			"instanceId", instanceId
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setLong(
			"processId", processId
		).setLong(
			"taskId", taskId
		).setString(
			"taskName", name
		).setLong(
			"tokenId", tokenId
		).setLong(
			"userId", userId
		).setString(
			"version", processVersion
		);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(() -> addDocument(document));

		return document;
	}

	@Override
	public Document add(
		long companyId, long taskId, long assigneId, String className, 
		long classPK, boolean completed, long completionUserId,
		Date completionDate, Date createDate, Date modifiedDate,
		String description, long duration, long instanceId,
		boolean instanceCompleted, String name,
		long processId, String processVersion, long tokenId, long userId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(
				companyId, instanceId, processId, processVersion, taskId,
				tokenId)
		).setLong(
			"assigneeId", assigneId
		).setString(
			"className", className
		).setLong(
			"classPK", classPK
		).setLong(
			"companyId", companyId
		).setBoolean(
			"completed", completed
		);

		if (completed) {
			documentBuilder.setDate(
				"completionDate", formatDate(completionDate)
			).setLong(
				"completionUserId", completionUserId
			);
			
		}

		documentBuilder.setDate(
			"createDate", formatDate(createDate)
		).setBoolean(
			"deleted", false
		);

		if (completed) {
			documentBuilder.setLong("duration", duration);
		}

		documentBuilder.setBoolean(
			"instanceCompleted", instanceCompleted
		).setLong(
			"instanceId", instanceId
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setLong(
			"processId", processId
		).setLong(
			"taskId", taskId
		).setString(
			"taskName", name
		).setLong(
			"tokenId", tokenId
		).setLong(
			"userId", userId
		).setString(
			"version", processVersion
		);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(() -> addDocument(document));

		return document;
	}
	
	private long _getDuration(Date completionDate, Date createDate) {
		Duration duration = Duration.between(
			createDate.toInstant(), completionDate.toInstant());

		return duration.toMillis();
	}

	@Override
	public Document update(
		long companyId, long taskId, long assigneId, String className,
		long classPK, boolean completed, long completionUserId,
		Date completionDate, Date modifiedDate, String description,
		long duration, long instanceId, boolean instanceCompleted, String name,
		long processId, String processVersion, long tokenId, long userId) {
		
		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(
				companyId, instanceId, processId, processVersion, taskId,
				tokenId)
		).setLong(
			"assigneeId", assigneId
		).setString(
			"className", className
		).setLong(
			"classPK", classPK
		).setLong(
			"companyId", companyId
		).setBoolean(
			"completed", completed
		);

		if (completed) {
			documentBuilder.setDate(
				"completionDate", formatDate(completionDate)
			).setLong(
				"completionUserId", completionUserId
			);
			
		}

		documentBuilder.setBoolean("deleted", false);

		if (completed) {
			documentBuilder.setLong("duration", duration);
		}

		documentBuilder.setBoolean(
			"instanceCompleted", instanceCompleted
		).setLong(
			"instanceId", instanceId
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setLong(
			"processId", processId
		).setLong(
			"taskId", taskId
		).setString(
			"taskName", name
		).setLong(
			"tokenId", tokenId
		).setLong(
			"userId", userId
		).setString(
			"version", processVersion
		);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(() -> updateDocument(document));

		return document;
	}

	@Override
	public void delete(
		long companyId, long instanceId, long processId, String processVersion,
		long taskId, long tokenId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID,
			digest(
				companyId, instanceId, processId, processVersion, taskId,
				tokenId));

		workflowMetricsPortalExecutor.execute(
			() -> deleteDocument(documentBuilder));

	}

}