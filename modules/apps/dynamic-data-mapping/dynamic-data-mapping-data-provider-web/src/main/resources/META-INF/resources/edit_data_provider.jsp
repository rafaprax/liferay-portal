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
String redirect = ParamUtil.getString(request, "redirect");

String dataProviderType = ParamUtil.getString(request, "dataProviderType");

DDMDataProvider ddmDataProvider = ddmDataProviderDisplayContext.getDataProvider();

long dataProviderId = BeanParamUtil.getLong(ddmDataProvider, request, "dataProviderId");
long groupId = BeanParamUtil.getLong(ddmDataProvider, request, "groupId", scopeGroupId);
String name = BeanParamUtil.getString(ddmDataProvider, request, "name");
String description = BeanParamUtil.getString(ddmDataProvider, request, "description");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((ddmDataProvider == null) ? LanguageUtil.get(request, dataProviderType) : ddmDataProvider.getName(locale));
%>

<portlet:actionURL name="addDataProvider" var="addDataProviderURL">
	<portlet:param name="mvcPath" value="/edit_data_provider.jsp" />
</portlet:actionURL>

<portlet:actionURL name="updateDataProvider" var="updateDataProviderURL">
	<portlet:param name="mvcPath" value="/edit_data_provider.jsp" />
</portlet:actionURL>

<aui:form action="<%= (ddmDataProvider == null) ? addDataProviderURL : updateDataProviderURL %>" method="post" name="frm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="dataProviderId" type="hidden" value="<%= dataProviderId %>" />
	<aui:input name="definition" type="hidden" />

	<div class="container-fluid-1280">
		<aui:fieldset>
			<h1>
				<liferay-ui:input-editor contents="<%= HtmlUtil.escape(LocalizationUtil.getLocalization(name, themeDisplay.getLanguageId())) %>" editorName="alloyeditor" name="nameEditor" showSource="<%= false %>" />
			</h1>

			<aui:input name="name" type="hidden" />

			<h2>
				<liferay-ui:input-editor contents="<%= HtmlUtil.escape(LocalizationUtil.getLocalization(description, themeDisplay.getLanguageId())) %>" editorName="alloyeditor" name="descriptionEditor" showSource="<%= false %>" />
			</h2>

			<aui:input name="description" type="hidden" />
		</aui:fieldset>

		<aui:fieldset>
			<%= ddmDataProviderDisplayContext.getDataProviderDefinition() %>
		</aui:fieldset>
	</div>

	<div class="container-fluid-1280">
		<aui:button-row>
			<aui:button cssClass="btn-lg" id="submit" label="save" primary="<%= true %>" type="submit" />

			<aui:button cssClass="btn-lg" href="<%= redirect %>" name="cancelButton" type="cancel" />
		</aui:button-row>
	</div>
</aui:form>