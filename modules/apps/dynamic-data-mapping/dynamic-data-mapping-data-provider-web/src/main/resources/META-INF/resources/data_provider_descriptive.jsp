<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

DDMDataProvider ddmDataProvider = (DDMDataProvider)row.getObject();

String href = (String)request.getAttribute(WebKeys.SEARCH_ENTRY_HREF);
%>

<liferay-ui:app-view-entry
	actionJsp="/data_provider_action.jsp"
	actionJspServletContext="<%= application %>"
	author="<%= ddmDataProvider.getUserName() %>"
	createDate="<%= ddmDataProvider.getCreateDate() %>"
	description="<%= ddmDataProvider.getDescription(locale) %>"
	displayStyle="descriptive"
	groupId="<%= ddmDataProvider.getGroupId() %>"
	markupView="lexicon"
	modifiedDate="<%= ddmDataProvider.getModifiedDate() %>"
	rowCheckerId="<%= String.valueOf(ddmDataProvider.getDataProviderId()) %>"
	rowCheckerName="<%= DDMDataProvider.class.getSimpleName() %>"
	showCheckbox="<%= false %>"
	thumbnailDivStyle="height: 146px; width: 146px;"
	thumbnailSrc='<%= themeDisplay.getPathThemeImages() + "/file_system/large/article.png" %>'
	thumbnailStyle="max-height: 128px; max-width: 128px;"
	title="<%= ddmDataProvider.getName(locale) %>"
	url="<%= href %>"
/>