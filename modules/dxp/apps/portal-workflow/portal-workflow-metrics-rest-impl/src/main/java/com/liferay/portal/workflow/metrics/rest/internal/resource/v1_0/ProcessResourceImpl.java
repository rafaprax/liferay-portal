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

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.workflow.metrics.index.ProcessWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util.ProcessUtil;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessResource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rafael Praxedes
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/process.properties",
	scope = ServiceScope.PROTOTYPE, service = ProcessResource.class
)
public class ProcessResourceImpl extends BaseProcessResourceImpl {

	@Override
	public String getProcessTitle(Long processId) throws Exception {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames("workflow-metrics-processes");
		searchSearchRequest.setQuery(_createBooleanQuery(processId));
		searchSearchRequest.setSelectedFieldNames(
			"processId", _getTitleFieldName());

		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::stream
		).map(
			SearchHit::getDocument
		).findFirst(
		).map(
			document -> document.getString(_getTitleFieldName())
		).orElseGet(
			() -> StringPool.BLANK
		);
	}
	
	private String _getTitleFieldName() {
		return Field.getLocalizedName(
			contextAcceptLanguage.getPreferredLocale(), "title");
	}

	@Override
	public void deleteProcess(Long processId) throws Exception {
		_processWorkflowMetricsIndexer.delete(
			contextCompany.getCompanyId(), processId);
	}
	
	private Map<Locale, String> _toLocalizedMap(Map<String, String> titleI18n) {
		Map<Locale, String> map = new HashMap<>();

		for (Map.Entry<String, String> entry : titleI18n.entrySet()) {
			map.put(Locale.forLanguageTag(entry.getKey()), entry.getValue());
		}

		return map;
	}

	@Override
	public Process postProcess(Process process) throws Exception {
		return ProcessUtil.toProcess(
			_processWorkflowMetricsIndexer.update(
				contextCompany.getCompanyId(), process.getActive(),
				process.getDescription(), process.getDateModified(),
				process.getId(), process.getName(), process.getTitle(),
				_toLocalizedMap(process.getTitle_i18n()),
				process.getVersion()));
	}

	private BooleanQuery _createBooleanQuery(Long processId) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustQueryClauses(_queries.term("processId", processId));

		return booleanQuery.addMustQueryClauses(
			_queries.term("companyId", contextCompany.getCompanyId()),
			_queries.term("deleted", Boolean.FALSE));
	}

	@Override
	public Process putProcess(Long processId, Process process) throws Exception {

		return ProcessUtil.toProcess(
			_processWorkflowMetricsIndexer.update(
				contextCompany.getCompanyId(), process.getActive(),
				process.getDescription(), process.getDateModified(),
				process.getId(), process.getName(), process.getTitle(),
				_toLocalizedMap(process.getTitle_i18n()),
				process.getVersion()));
	}

	@Override
	public Process getProcess(Long processId) throws Exception {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames("workflow-metrics-processes");
		searchSearchRequest.setQuery(_createBooleanQuery(processId));
		
		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::stream
		).map(
			SearchHit::getDocument
		).findFirst(
		).map(
			ProcessUtil::toProcess
		).orElseGet(
			() -> null
		);

	}

	@Reference
	private ProcessWorkflowMetricsIndexer _processWorkflowMetricsIndexer;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestExecutor _searchRequestExecutor;

}