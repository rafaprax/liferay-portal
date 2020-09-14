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

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.script.ScriptBuilder;
import com.liferay.portal.search.script.ScriptType;
import com.liferay.portal.workflow.metrics.internal.search.index.util.WorkflowMetricsIndexerUtil;
import com.liferay.portal.workflow.metrics.search.index.TaskWorkflowMetricsIndexer;

import java.time.Duration;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = TaskWorkflowMetricsIndexer.class)
public class TaskWorkflowMetricsIndexerImpl
	extends BaseWorkflowMetricsIndexer implements TaskWorkflowMetricsIndexer {

	@Override
	public Document addTask(
		Map<Locale, String> assetTitleMap, Map<Locale, String> assetTypeMap,
		Long[] assigneeIds, String assigneeType, String className, long classPK,
		long companyId, boolean completed, Date completionDate,
		Long completionUserId, Date createDate, boolean instanceCompleted,
		Date instanceCompletionDate, long instanceId, Date modifiedDate,
		String name, long nodeId, long processId, String processVersion,
		long taskId, long userId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		if (assigneeIds != null) {
			documentBuilder.setLongs("assigneeIds", assigneeIds);
			documentBuilder.setString("assigneeType", assigneeType);
		}

		documentBuilder.setString(
			"className", className
		).setLong(
			"classPK", classPK
		).setLong(
			"companyId", companyId
		).setValue(
			"completed", completed
		);

		if (completed) {
			documentBuilder.setDate(
				"completionDate", getDate(completionDate)
			).setLong(
				"completionUserId", completionUserId
			);
		}

		documentBuilder.setDate(
			"createDate", getDate(createDate)
		).setValue(
			Field.getSortableFieldName(
				StringBundler.concat(
					"createDate", StringPool.UNDERLINE, "Number")),
			createDate.getTime()
		).setValue(
			"deleted", false
		);

		if (completed) {
			documentBuilder.setLong(
				"duration", _getDuration(completionDate, createDate));
		}

		documentBuilder.setValue(
			"instanceCompleted", instanceCompleted
		).setDate(
			"instanceCompletionDate", getDate(instanceCompletionDate)
		).setLong(
			"instanceId", instanceId
		).setDate(
			"modifiedDate", getDate(modifiedDate)
		).setString(
			"name", name
		).setLong(
			"nodeId", nodeId
		).setLong(
			"processId", processId
		).setLong(
			"taskId", taskId
		).setString(
			"uid", digest(companyId, taskId)
		).setLong(
			"userId", userId
		).setString(
			"version", processVersion
		);

		setLocalizedField(documentBuilder, "assetTitle", assetTitleMap);
		setLocalizedField(documentBuilder, "assetType", assetTypeMap);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(
			() -> {
				addDocument(document);

				if (completed) {
					return;
				}

				SearchSearchRequest searchSearchRequest =
					new SearchSearchRequest();

				searchSearchRequest.setQuery(
					queries.term("instanceId", instanceId));
				searchSearchRequest.setIndexNames(
					_instanceWorkflowMetricsIndex.getIndexName(companyId));

				SearchSearchResponse searchSearchResponse =
					searchEngineAdapter.execute(searchSearchRequest);

				System.out.println(
					"[ADD TASK] searchSearchResponse.getSearchHits().getTotalHits() = " +
						searchSearchResponse.getSearchHits(
						).getTotalHits());

				ScriptBuilder builder = scripts.builder();

				HashMap<String, Object> taskAttributesMap =
					HashMapBuilder.<String, Object>put(
						"assigneeIds",
						Stream.of(
							assigneeIds
						).map(
							String::valueOf
						).toArray(
							String[]::new
						)
					).put(
						"assigneeType", assigneeType
					).put(
						"taskId", taskId
					).put(
						"taskName", name
					).build();

				UpdateDocumentRequest updateDocumentRequest =
					new UpdateDocumentRequest(
						_instanceWorkflowMetricsIndex.getIndexName(companyId),
						WorkflowMetricsIndexerUtil.digest(
							_instanceWorkflowMetricsIndex.getIndexType(),
							companyId, instanceId),
						builder.idOrCode(
							StringUtil.read(
								getClass(),
								"dependencies/workflow-metrics-add-task-" +
									"script.painless")
						).language(
							"painless"
						).putParameter(
							"task", taskAttributesMap
						).scriptType(
							ScriptType.INLINE
						).build());

				Document instanceDocument = _createInstanceDocument(
					companyId, instanceId, taskAttributesMap);

				updateDocumentRequest.setIndexDocumentRequest(
					new IndexDocumentRequest(
						_instanceWorkflowMetricsIndex.getIndexName(companyId),
						instanceDocument.getString("uid"), instanceDocument));

				UpdateDocumentResponse updateDocumentResponse =
					searchEngineAdapter.execute(updateDocumentRequest);

				System.out.println("##### ADD TASK ######");
				System.out.println(
					"title=" + assetTitleMap.get(LocaleUtil.getSiteDefault()));
				System.out.println("taskId=" + taskId);
			});

		return document;
	}

	@Override
	public Document completeTask(
		long companyId, Date completionDate, long completionUserId,
		long duration, Date modifiedDate, long taskId, long userId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", companyId
		).setValue(
			"completed", true
		).setDate(
			"completionDate", getDate(completionDate)
		).setLong(
			"completionUserId", completionUserId
		).setLong(
			"duration", duration
		).setDate(
			"modifiedDate", getDate(modifiedDate)
		).setLong(
			"taskId", taskId
		).setString(
			"uid", digest(companyId, taskId)
		).setLong(
			"userId", userId
		);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(
			() -> {
				updateDocument(document);

				_deleteInstanceTask(companyId, taskId);

				BooleanQuery booleanQuery = queries.booleanQuery();

				booleanQuery.addMustQueryClauses(
					queries.term("companyId", companyId),
					queries.term("taskId", taskId));

				_slaTaskResultWorkflowMetricsIndexer.updateDocuments(
					companyId,
					HashMapBuilder.<String, Object>put(
						"completionDate", document.getDate("completionDate")
					).put(
						"completionUserId", document.getLong("completionUserId")
					).build(),
					booleanQuery);
			});

		return document;
	}

	@Override
	public void deleteTask(long companyId, long taskId) {
		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", companyId
		).setLong(
			"taskId", taskId
		).setString(
			"uid", digest(companyId, taskId)
		);

		workflowMetricsPortalExecutor.execute(
			() -> {
				deleteDocument(documentBuilder);

				_deleteInstanceTask(companyId, taskId);
			});
	}

	@Override
	public String getIndexName(long companyId) {
		return _taskWorkflowMetricsIndex.getIndexName(companyId);
	}

	@Override
	public String getIndexType() {
		return _taskWorkflowMetricsIndex.getIndexType();
	}

	@Override
	public Document updateTask(
		Map<Locale, String> assetTitleMap, Map<Locale, String> assetTypeMap,
		Long[] assigneeIds, String assigneeType, long companyId,
		Date modifiedDate, long taskId, long userId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		if (assigneeIds != null) {
			documentBuilder.setLongs("assigneeIds", assigneeIds);
			documentBuilder.setString("assigneeType", assigneeType);
		}

		documentBuilder.setLong(
			"companyId", companyId
		).setDate(
			"modifiedDate", getDate(modifiedDate)
		).setLong(
			"taskId", taskId
		).setString(
			"uid", digest(companyId, taskId)
		).setLong(
			"userId", userId
		);

		setLocalizedField(documentBuilder, "assetTitle", assetTitleMap);
		setLocalizedField(documentBuilder, "assetType", assetTypeMap);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(
			() -> {
				updateDocument(document);

				if (Objects.isNull(document.getLongs("assigneeIds"))) {
					return;
				}

				BooleanQuery booleanQuery = queries.booleanQuery();

				booleanQuery.addMustQueryClauses(
					queries.term("companyId", document.getLong("companyId")),
					queries.term("taskId", document.getLong("taskId")));

				_slaTaskResultWorkflowMetricsIndexer.updateDocuments(
					companyId,
					HashMapBuilder.<String, Object>put(
						"assigneeIds",
						Stream.of(
							assigneeIds
						).map(
							String::valueOf
						).toArray(
							String[]::new
						)
					).put(
						"assigneeType", assigneeType
					).build(),
					booleanQuery);

				ScriptBuilder builder = scripts.builder();

				UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
					new UpdateByQueryDocumentRequest(
						queries.nested(
							"tasks", queries.term("tasks.taskId", taskId)),
						builder.idOrCode(
							StringUtil.read(
								getClass(),
								"dependencies/workflow-metrics-update-task-" +
									"script.painless")
						).language(
							"painless"
						).putParameter(
							"assigneeIds",
							Stream.of(
								assigneeIds
							).map(
								String::valueOf
							).toArray(
								String[]::new
							)
						).putParameter(
							"assigneeType", assigneeType
						).putParameter(
							"taskId", taskId
						).scriptType(
							ScriptType.INLINE
						).build(),
						_instanceWorkflowMetricsIndex.getIndexName(companyId));

				UpdateByQueryDocumentResponse updateByQueryDocumentResponse =
					searchEngineAdapter.execute(updateByQueryDocumentRequest);

				System.out.println("##### UPDATE TASK ######");
				System.out.println("taskId=" + taskId);
				System.out.println(
					"updated=" + updateByQueryDocumentResponse.getUpdated());
			});

		return document;
	}

	private Document _createInstanceDocument(
		long companyId, long instanceId,
		HashMap<String, Object> taskAttributesMap) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", companyId
		).setValue(
			"task", taskAttributesMap
		).setString(
			"uid",
			WorkflowMetricsIndexerUtil.digest(
				_instanceWorkflowMetricsIndex.getIndexType(), companyId,
				instanceId)
		);

		return documentBuilder.build();
	}

	private void _deleteInstanceTask(long companyId, long taskId) {
		ScriptBuilder builder = scripts.builder();

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				queries.nested("tasks", queries.term("tasks.taskId", taskId)),
				builder.idOrCode(
					StringUtil.read(
						getClass(),
						"dependencies/workflow-metrics-delete-task-" +
							"script.painless")
				).language(
					"painless"
				).putParameter(
					"taskId", taskId
				).scriptType(
					ScriptType.INLINE
				).build(),
				_instanceWorkflowMetricsIndex.getIndexName(companyId));

		UpdateByQueryDocumentResponse updateByQueryDocumentResponse =
			searchEngineAdapter.execute(updateByQueryDocumentRequest);

		System.out.println("##### DELETE TASK ######");
		System.out.println("taskId=" + taskId);
		System.out.println(
			"updated=" + updateByQueryDocumentResponse.getUpdated());
	}

	private long _getDuration(Date completionDate, Date createDate) {
		Duration duration = Duration.between(
			createDate.toInstant(), completionDate.toInstant());

		return duration.toMillis();
	}

	@Reference(target = "(workflow.metrics.index.entity.name=instance)")
	private WorkflowMetricsIndex _instanceWorkflowMetricsIndex;

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

	@Reference(target = "(workflow.metrics.index.entity.name=task)")
	private WorkflowMetricsIndex _taskWorkflowMetricsIndex;

}