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
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.workflow.metrics.internal.messaging.WorkflowMetricsSLAProcessMessageListener;
import com.liferay.portal.workflow.metrics.internal.sla.WorkflowMetricsInstanceSLAStatus;
import com.liferay.portal.workflow.metrics.internal.sla.processor.WorkflowMetricsSLAInstanceResult;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true, service = SLAInstanceResultWorkflowMetricsIndexer.class
)
public class SLAInstanceResultWorkflowMetricsIndexer
	extends BaseSLAWorkflowMetricsIndexer {

	public Document creatDefaultDocument(long companyId, long processId) {
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult =
			new WorkflowMetricsSLAInstanceResult();

		workflowMetricsSLAInstanceResult.setCompanyId(companyId);
		workflowMetricsSLAInstanceResult.setProcessId(processId);

		return createDocument(workflowMetricsSLAInstanceResult);
	}

	public Document createDocument(
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		DocumentBuilder documentBuilder = documentBuilderFactory.builder();

		documentBuilder.setLong(
			"companyId", workflowMetricsSLAInstanceResult.getCompanyId());

		if (workflowMetricsSLAInstanceResult.getCompletionLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"completionDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getCompletionLocalDateTime()));
		}

		documentBuilder.setValue(
			"deleted", false
		).setLong(
			"elapsedTime", workflowMetricsSLAInstanceResult.getElapsedTime()
		).setValue(
			"instanceCompleted",
			workflowMetricsSLAInstanceResult.getCompletionLocalDateTime() !=
				null
		).setLong(
			"instanceId", workflowMetricsSLAInstanceResult.getInstanceId()
		);

		if (workflowMetricsSLAInstanceResult.getLastCheckLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"lastCheckDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getLastCheckLocalDateTime()));
		}

		documentBuilder.setValue(
			"onTime", workflowMetricsSLAInstanceResult.isOnTime());

		if (workflowMetricsSLAInstanceResult.getOverdueLocalDateTime() !=
				null) {

			documentBuilder.setDate(
				"overdueDate",
				formatLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getOverdueLocalDateTime()));
		}

		documentBuilder.setLong(
			"processId", workflowMetricsSLAInstanceResult.getProcessId()
		).setLong(
			"remainingTime", workflowMetricsSLAInstanceResult.getRemainingTime()
		).setLong(
			"slaDefinitionId",
			workflowMetricsSLAInstanceResult.getSLADefinitionId()
		);

		WorkflowMetricsSLAStatus workflowMetricsSLAStatus =
			workflowMetricsSLAInstanceResult.getWorkflowMetricsSLAStatus();

		if (workflowMetricsSLAStatus != null) {
			documentBuilder.setString(
				"status", workflowMetricsSLAStatus.name());
		}

		documentBuilder.setString(
			"uid",
			digest(
				workflowMetricsSLAInstanceResult.getCompanyId(),
				workflowMetricsSLAInstanceResult.getInstanceId(),
				workflowMetricsSLAInstanceResult.getProcessId(),
				workflowMetricsSLAInstanceResult.getSLADefinitionId()));

		return documentBuilder.build();
	}

	@Override
	public void deleteDocuments(
		long companyId, long processId, long slaDefinitionId) {

		super.deleteDocuments(companyId, processId, slaDefinitionId);

		BooleanQuery booleanQuery = queries.booleanQuery();

		BooleanQuery filterBooleanQuery = queries.booleanQuery();

		filterBooleanQuery.addMustNotQueryClauses(
			queries.term("slaDefinitionId", 0));
		filterBooleanQuery.addMustQueryClauses(
			queries.term("completed", false),
			queries.term("processId", processId));

		booleanQuery.addFilterQueryClauses(filterBooleanQuery);

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setQuery(booleanQuery);

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				booleanQuery,
				scripts.script(
					StringBundler.concat(
						"ctx._source.slaStatus = ", StringPool.QUOTE,
						WorkflowMetricsInstanceSLAStatus.UNTRACKED.getValue(),
						StringPool.QUOTE)),
				getIndexName(companyId));

		if (PortalRunMode.isTestMode()) {
			updateByQueryDocumentRequest.setRefresh(true);
		}

		searchEngineAdapter.execute(updateByQueryDocumentRequest);

		try {
			Message message = new Message();

			JSONObject payloadJSONObject = _jsonFactory.createJSONObject();

			payloadJSONObject.put("processId", processId);

			message.setPayload(payloadJSONObject);

			workflowMetricsSLAProcessMessageListener.receive(message);
		}
		catch (MessageListenerException messageListenerException) {
			_log.error(messageListenerException, messageListenerException);
		}
	}

	@Override
	public String getIndexName(long companyId) {
		return _slaInstanceResultWorkflowMetricsIndex.getIndexName(companyId);
	}

	@Override
	public String getIndexType() {
		return _slaInstanceResultWorkflowMetricsIndex.getIndexType();
	}

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile WorkflowMetricsSLAProcessMessageListener
		workflowMetricsSLAProcessMessageListener;

	private static final Log _log = LogFactoryUtil.getLog(
		SLAInstanceResultWorkflowMetricsIndexer.class);

	@Reference(target = "(workflow.metrics.index.entity.name=instance)")
	private WorkflowMetricsIndex _instanceWorkflowMetricsIndex;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(workflow.metrics.index.entity.name=sla-instance-result)"
	)
	private WorkflowMetricsIndex _slaInstanceResultWorkflowMetricsIndex;

}