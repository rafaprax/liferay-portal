/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.charon.integration.internal.constants;

/**
 * @author Rafael Praxedes
 */
public class SCIMCharonConstants {

	public static final String ACCEPT_HEADER = "Accept";

	public static final String ACCEPT_HEADER_DESC =
		"Specify media types which are acceptable for the response.";

	public static final String APPLICATION_JSON = "application/json";

	public static final String APPLICATION_SCIM_JSON = "application/scim+json";

	public static final String ATTRIBUTES = "attributes";

	public static final String ATTRIBUTES_DESC =
		"SCIM defined attributes parameter.";

	public static final String COUNT = "count";

	public static final String COUNT_DESC =
		"Specifies the desired maximum number of query results per page.";

	public static final String DOMAIN = "domain";

	public static final String DOMAIN_DESC = "Domain of the provisioning user";

	public static final String EXCLUDE_ATTRIBUTES = "excludedAttributes";

	public static final String EXCLUDED_ATTRIBUTES_DESC =
		"SCIM defined excludedAttribute parameter.";

	public static final String FILTER = "filter";

	public static final String FILTER_DESC = "Filter expression for filtering";

	public static final String ID = "id";

	public static final String ID_DESC = "Unique id of the resource type.";

	public static final String LIFERAY_USER_EXTENSION_SCHEMA_URI =
		"urn:ietf:params:scim:schemas:extension:liferay:2.0:User";

	public static final String SORT_BY = "sortBy";

	public static final String SORT_BY_DESC =
		"Specifies the attribute whose value\n" +
			"SHALL be used to order the returned responses";

	public static final String SORT_ORDER = "sortOder";

	public static final String SORT_ORDER_DESC =
		"The order in which the \"sortBy\" parameter is applied.";

	public static final String START_INDEX = "startIndex";

	public static final String START_INDEX_DESC =
		"The 1-based index of the first query result";

}