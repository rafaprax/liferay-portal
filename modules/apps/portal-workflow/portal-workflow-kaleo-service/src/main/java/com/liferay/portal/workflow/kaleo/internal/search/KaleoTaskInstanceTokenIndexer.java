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

package com.liferay.portal.workflow.kaleo.internal.search;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = Indexer.class)
public class KaleoTaskInstanceTokenIndexer
	extends BaseIndexer<KaleoTaskInstanceToken> {

	public static final String CLASS_NAME =
		KaleoTaskInstanceToken.class.getName();

	public KaleoTaskInstanceTokenIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID);
		setDefaultSelectedLocalizedFieldNames(Field.DESCRIPTION, Field.TITLE);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchLocalizedTerm(searchQuery, searchContext, "assetTitle", true);
		addSearchLocalizedTerm(
			searchQuery, searchContext, "assetDescription", true);
	}

	@Override
	protected void doDelete(KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws Exception {

		deleteDocument(
			kaleoTaskInstanceToken.getCompanyId(),
			kaleoTaskInstanceToken.getKaleoInstanceTokenId());
	}

	@Override
	protected Document doGetDocument(
			KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws Exception {

		Document document = getBaseModelDocument(
			CLASS_NAME, kaleoTaskInstanceToken);

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances();

		Stream<KaleoTaskAssignmentInstance> stream =
			kaleoTaskAssignmentInstances.stream();

		Map<String, List<KaleoTaskAssignmentInstance>> map = stream.collect(
			Collectors.groupingBy(
				KaleoTaskAssignmentInstance::getAssigneeClassName));

		for (Map.Entry<String, List<KaleoTaskAssignmentInstance>> entry :
				map.entrySet()) {

			List<KaleoTaskAssignmentInstance> value = entry.getValue();

			Stream<KaleoTaskAssignmentInstance> valueStream = value.stream();

			document.addKeyword(
				StringUtil.replace(entry.getKey(), '.', '_'),
				valueStream.map(
					KaleoTaskAssignmentInstance::getAssigneeClassPK
				).toArray(
					Long[]::new
				)
			);
		}

		document.addKeyword("completed", kaleoTaskInstanceToken.isCompleted());
		document.addDate("dueDate", kaleoTaskInstanceToken.getDueDate());
		document.addKeyword(
			"kaleoInstanceId", kaleoTaskInstanceToken.getKaleoInstanceId());
		document.addText(
			"kaleoTaskName", kaleoTaskInstanceToken.getKaleoTaskName());

		AssetRendererFactory<?> assetRendererFactory = getAssetRendererFactory(
			kaleoTaskInstanceToken.getClassName());

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			kaleoTaskInstanceToken.getClassPK());

		AssetEntry assetEntry = assetEntryLocalService.getEntry(
			assetRenderer.getClassName(), assetRenderer.getClassPK());

		document.addKeyword("assetClassName", assetEntry.getClassName());
		document.addKeyword("assetClassNameId", assetEntry.getClassNameId());
		document.addKeyword("assetClassPK", assetEntry.getClassPK());

		Locale siteDefaultLocale = portal.getSiteDefaultLocale(
			kaleoTaskInstanceToken.getGroupId());

		addLocalizedField(
			document, "assetTitle", siteDefaultLocale,
			assetEntry.getTitleMap());
		addLocalizedField(
			document, "assetDescription", siteDefaultLocale,
			assetEntry.getDescriptionMap());

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws Exception {

		Document document = getDocument(kaleoTaskInstanceToken);

		indexWriterHelper.updateDocument(
			getSearchEngineId(), kaleoTaskInstanceToken.getCompanyId(),
			document, isCommitImmediately());
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceToken(
				classPK);

		doReindex(kaleoTaskInstanceToken);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexKaleoTaskInstanceTokens(companyId);
	}

	protected AssetRendererFactory<?> getAssetRendererFactory(
		String className) {

		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(className);
	}

	protected void reindexKaleoTaskInstanceTokens(long companyId)
		throws PortalException {

		final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			kaleoTaskInstanceTokenLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.
				PerformActionMethod<KaleoTaskInstanceToken>() {

				@Override
				public void performAction(
					KaleoTaskInstanceToken kaleoTaskInstanceToken) {

					try {
						Document document = getDocument(kaleoTaskInstanceToken);

						indexableActionableDynamicQuery.addDocuments(document);
					}
					catch (PortalException pe) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to index kaleoTaskInstanceToken " +
									kaleoTaskInstanceToken.
										getKaleoInstanceTokenId(),
								pe);
						}
					}
				}

			});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected IndexWriterHelper indexWriterHelper;

	@Reference
	protected KaleoTaskInstanceTokenLocalService
		kaleoTaskInstanceTokenLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected SearchPermissionChecker searchPermissionChecker;

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskInstanceTokenIndexer.class);

}