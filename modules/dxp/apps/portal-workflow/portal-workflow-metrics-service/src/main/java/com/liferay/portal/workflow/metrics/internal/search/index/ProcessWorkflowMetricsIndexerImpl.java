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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.metrics.index.ProcessWorkflowMetricsIndexer;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = ProcessWorkflowMetricsIndexer.class)
public class ProcessWorkflowMetricsIndexerImpl extends BaseWorkflowMetricsIndexer implements ProcessWorkflowMetricsIndexer{

	@Override
	public void addDocument(Document document) {
		if (searchEngineAdapter == null) {
			return;
		}

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		bulkDocumentRequest.addBulkableDocumentRequest(
			new IndexDocumentRequest(
				_instanceWorkflowMetricsIndexer.getIndexName(),
				_createWorkflowMetricsInstanceDocument(
					document.getLong("companyId"),
					document.getLong("processId"))) {

				{
					setType(_instanceWorkflowMetricsIndexer.getIndexType());
				}
			});
		bulkDocumentRequest.addBulkableDocumentRequest(
			new IndexDocumentRequest(
				_slaInstanceResultWorkflowMetricsIndexer.getIndexName(),
				_creatWorkflowMetricsSLAInstanceResultDocument(
					document.getLong("companyId"),
					document.getLong("processId"))) {

				{
					setType(
						_slaInstanceResultWorkflowMetricsIndexer.
							getIndexType());
				}
			});
		bulkDocumentRequest.addBulkableDocumentRequest(
			new IndexDocumentRequest(getIndexName(), document) {
				{
					setType(getIndexType());
				}
			});

		if (PortalRunMode.isTestMode()) {
			bulkDocumentRequest.setRefresh(true);
		}

		searchEngineAdapter.execute(bulkDocumentRequest);
	}

	@Override
	public String getIndexName() {
		return "workflow-metrics-processes";
	}

	@Override
	public String getIndexType() {
		return "WorkflowMetricsProcessType";
	}

	@Override
	public void reindex(long companyId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			kaleoDefinitionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property companyIdProperty = PropertyFactoryUtil.forName(
					"companyId");

				dynamicQuery.add(companyIdProperty.eq(companyId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(KaleoDefinition kaleoDefinition) ->
				workflowMetricsPortalExecutor.execute(
					() -> {
						String defaultLanguageId =
							LocalizationUtil.getDefaultLanguageId(
								kaleoDefinition.getTitle());

						add(
							kaleoDefinition.getCompanyId(),
							kaleoDefinition.getActive(),
							kaleoDefinition.getCreateDate(),
							kaleoDefinition.getDescription(),
							kaleoDefinition.getModifiedDate(),
							kaleoDefinition.getKaleoDefinitionId(),
							kaleoDefinition.getName(),
							kaleoDefinition.getTitle(defaultLanguageId),
							kaleoDefinition.getTitleMap(),
							StringBundler.concat(
								kaleoDefinition.getVersion(),
								CharPool.PERIOD, 0));
					}));

		actionableDynamicQuery.performActions();
	}

	private Document _createWorkflowMetricsInstanceDocument(
		long companyId, long kaleoDefinitionId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, kaleoDefinitionId));

		documentBuilder.setLong("companyId", companyId);
		documentBuilder.setBoolean("completed", false);
		documentBuilder.setBoolean("deleted", false);
		documentBuilder.setLong("instanceId", 0L);
		documentBuilder.setLong("processId", kaleoDefinitionId);

		return documentBuilder.build();
	}

	private Document _creatWorkflowMetricsSLAInstanceResultDocument(
		long companyId, long kaleoDefinitionId) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, kaleoDefinitionId));

		documentBuilder.setLong("companyId", companyId);
		documentBuilder.setBoolean("deleted", false);
		documentBuilder.setBoolean("instanceCompleted", false);
		documentBuilder.setLong("instanceId", 0L);
		documentBuilder.setLong("processId", kaleoDefinitionId);
		documentBuilder.setLong("slaDefinitionId", 0L);

		return documentBuilder.build();
	}

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private SLAInstanceResultWorkflowMetricsIndexer
		_slaInstanceResultWorkflowMetricsIndexer;

	@Override
	public Document add(
		long companyId, boolean active, Date createDate, String description,
		Date modifiedDate, long processId, String name, String title,
		Map<Locale, String> titleMap, String version) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, processId)
		).setLong(
			"companyId", companyId
		).setBoolean(
			"active", active
		).setDate(
			"createDate", formatDate(createDate)
		).setBoolean(
			"deleted", false
		).setString(
			"description", description
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setString(
			"name", name
		).setLong(
			"processId", processId
		).setString(
			"title", title
		).setString(
			"version", version
		);

		_setLocalizedTitle(documentBuilder, titleMap);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(() -> addDocument(document));

		return document;
	}

	@Override
	public Document update(
		long companyId, boolean active, String description, Date modifiedDate,
		long processId, String name, String title, Map<Locale, String> titleMap,
		String version) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, processId)
		).setLong(
			"companyId", companyId
		).setBoolean(
			"active", active
		).setBoolean(
			"deleted", false
		).setString(
			"description", description
		).setDate(
			"modifiedDate", formatDate(modifiedDate)
		).setString(
			"name", name
		).setLong(
			"processId", processId
		).setString(
			"title", title
		).setString(
			"version", version
		);

		_setLocalizedTitle(documentBuilder, titleMap);

		Document document = documentBuilder.build();

		workflowMetricsPortalExecutor.execute(() -> updateDocument(document));

		return document;
	}
	
	private void _setLocalizedTitle(
		DocumentBuilder documentBuilder, Map<Locale, String> titleMap) {
		
		Stream.of(
			titleMap.entrySet()
		).flatMap(
			Set::stream
		).forEach(
			entry -> documentBuilder.setString(
				Field.getLocalizedName(entry.getKey(), "title"),
				entry.getValue())
		);
	}

	@Override
	public void delete(long companyId, long processId) {
		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setString(
			Field.UID, digest(companyId, processId));

		workflowMetricsPortalExecutor.execute(
			() -> deleteDocument(documentBuilder));
		
	}

}