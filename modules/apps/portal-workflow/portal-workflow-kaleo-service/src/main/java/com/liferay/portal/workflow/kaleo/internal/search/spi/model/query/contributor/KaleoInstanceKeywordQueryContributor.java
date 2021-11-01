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

package com.liferay.portal.workflow.kaleo.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.workflow.kaleo.model.KaleoInstance",
	service = KeywordQueryContributor.class
)
public class KaleoInstanceKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		KaleoInstanceQuery kaleoInstanceQuery = getKaleoInstanceQuery(
			keywordQueryContributorHelper);

		if (kaleoInstanceQuery == null) {
			return;
		}

		appendAssetDescriptionTerm(
			booleanQuery, kaleoInstanceQuery.getAssetDescription(),
			keywordQueryContributorHelper);
		appendAssetTitleTerm(
			booleanQuery, kaleoInstanceQuery.getAssetTitle(),
			keywordQueryContributorHelper);
		appendCurrentKaleoNodeNameTerm(
			booleanQuery, kaleoInstanceQuery.getCurrentKaleoNodeName(),
			keywordQueryContributorHelper);
		appendKaleoDefinitionNameTerm(
			booleanQuery, kaleoInstanceQuery.getKaleoDefinitionName(),
			keywordQueryContributorHelper);
	}

	protected void appendAssetDescriptionTerm(
		BooleanQuery booleanQuery, String assetDescription,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		if (Validator.isNull(assetDescription)) {
			return;
		}

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		String assetDescriptionLocalizedName =
			LocalizationUtil.getLocalizedName(
				"assetDescription", searchContext.getLanguageId());

		searchContext.setAttribute(
			assetDescriptionLocalizedName, assetDescription);

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "assetDescription", true);
	}

	protected void appendAssetTitleTerm(
		BooleanQuery booleanQuery, String assetTitle,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		if (Validator.isNull(assetTitle)) {
			return;
		}

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		String assetTitleLocalizedName = LocalizationUtil.getLocalizedName(
			"assetTitle", searchContext.getLanguageId());

		searchContext.setAttribute(assetTitleLocalizedName, assetTitle);

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "assetTitle", false);
	}

	protected void appendCurrentKaleoNodeNameTerm(
		BooleanQuery booleanQuery, String currentKaleoNodeName,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		if (Validator.isNull(currentKaleoNodeName)) {
			return;
		}

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		searchContext.setAttribute(
			"currentKaleoNodeName", currentKaleoNodeName);

		queryHelper.addSearchTerm(
			booleanQuery, keywordQueryContributorHelper.getSearchContext(),
			"currentKaleoNodeName", false);
	}

	protected void appendKaleoDefinitionNameTerm(
		BooleanQuery booleanQuery, String kaleoDefinitionName,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		if (Validator.isNull(kaleoDefinitionName)) {
			return;
		}

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		searchContext.setAttribute("kaleoDefinitionName", kaleoDefinitionName);

		queryHelper.addSearchTerm(
			booleanQuery, keywordQueryContributorHelper.getSearchContext(),
			"kaleoDefinitionName", false);
	}

	protected KaleoInstanceQuery getKaleoInstanceQuery(
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		return (KaleoInstanceQuery)searchContext.getAttribute(
			"kaleoInstanceQuery");
	}

	@Reference
	protected QueryHelper queryHelper;

}