/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.charon.integration.internal.jaxrs.application;

import com.liferay.scim.charon.integration.internal.constants.SCIMCharonConstants;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Rafael Praxedes
 */
@OpenAPIDefinition(
	info = @Info(description = "SCIM 2.0 /Users endpoint", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0"), title = "/Users Endpoint Swagger Definition", version = "1.0")
)
@Path("/v2/Users")
public interface UserResource {

	@ApiResponses(
		{
			@ApiResponse(
				description = "Valid user is created", responseCode = "201"
			),
			@ApiResponse(
				description = "User is not found", responseCode = "404"
			)
		}
	)
	@Consumes("application/scim+json")
	@Operation(description = "Return the user which was created")
	@Parameters(
		{
			@Parameter(
				description = SCIMCharonConstants.ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "resourceString",
				required = false
			),
			@Parameter(
				description = SCIMCharonConstants.EXCLUDED_ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "excludedAttributes",
				required = false
			)
		}
	)
	@POST
	@Produces({"application/json", "application/scim+json"})
	public Response createUser(
		@QueryParam(SCIMCharonConstants.ATTRIBUTES) String attribute,
		@QueryParam(SCIMCharonConstants.EXCLUDE_ATTRIBUTES) String
			excludedAttributes,
		String resourceString);

	@ApiResponses(
		{
			@ApiResponse(description = "User is deleted", responseCode = "204"),
			@ApiResponse(
				description = "Valid user is not found", responseCode = "404"
			)
		}
	)
	@DELETE
	@Operation(description = "Delete the user with the given id")
	@Parameters(
		@Parameter(
			description = SCIMCharonConstants.ID_DESC, in = ParameterIn.PATH,
			name = "id", required = true
		)
	)
	@Path("/{id}")
	@Produces({"application/json", "application/scim+json"})
	public Response deleteUser(@PathParam(SCIMCharonConstants.ID) String id);

	@ApiResponses(
		{
			@ApiResponse(
				description = "Valid user is found", responseCode = "200"
			),
			@ApiResponse(
				description = "Valid user is not found", responseCode = "404"
			)
		}
	)
	@GET
	@Operation(description = "Return the user with the given id")
	@Parameters(
		{
			@Parameter(
				description = SCIMCharonConstants.ID_DESC,
				in = ParameterIn.PATH, name = "id", required = true
			),
			@Parameter(
				description = SCIMCharonConstants.ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "attribute", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.EXCLUDED_ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "excludedAttributes",
				required = false
			)
		}
	)
	@Path("/{id}")
	@Produces({"application/json", "application/scim+json"})
	public Response getUser(
		@Parameter(hidden = true) @PathParam(SCIMCharonConstants.ID) String id,
		@Parameter(hidden = true) @QueryParam(SCIMCharonConstants.ATTRIBUTES)
			String attribute,
		@Parameter(hidden = true)
		@QueryParam(SCIMCharonConstants.EXCLUDE_ATTRIBUTES)
		String excludedAttributes);

	@ApiResponses(
		{
			@ApiResponse(
				description = "Valid users are found", responseCode = "200"
			),
			@ApiResponse(
				description = "Valid users are not found", responseCode = "404"
			)
		}
	)
	@GET
	@Operation(
		description = "Return users according to the filter, sort and pagination parameters"
	)
	@Parameters(
		{
			@Parameter(
				description = SCIMCharonConstants.ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "attribute", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.EXCLUDED_ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "excludedAttributes",
				required = false
			),
			@Parameter(
				description = SCIMCharonConstants.FILTER_DESC,
				in = ParameterIn.QUERY, name = "filter", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.START_INDEX_DESC,
				in = ParameterIn.QUERY, name = "startIndex", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.COUNT_DESC,
				in = ParameterIn.QUERY, name = "count", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.SORT_BY_DESC,
				in = ParameterIn.QUERY, name = "sortBy", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.SORT_ORDER_DESC,
				in = ParameterIn.QUERY, name = "sortOrder", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.DOMAIN_DESC,
				in = ParameterIn.QUERY, name = "domainName", required = false
			)
		}
	)
	@Produces({"application/json", "application/scim+json"})
	public Response getUser(
		@QueryParam(SCIMCharonConstants.ATTRIBUTES) String attribute,
		@QueryParam(SCIMCharonConstants.EXCLUDE_ATTRIBUTES) String
			excludedAttributes,
		@QueryParam(SCIMCharonConstants.FILTER) String filter,
		@QueryParam(SCIMCharonConstants.START_INDEX) int startIndex,
		@QueryParam(SCIMCharonConstants.COUNT) int count,
		@QueryParam(SCIMCharonConstants.SORT_BY) String sortBy,
		@QueryParam(SCIMCharonConstants.SORT_ORDER) String sortOrder,
		@QueryParam(SCIMCharonConstants.DOMAIN) String domainName);

	@ApiResponses(
		{
			@ApiResponse(
				description = "Valid users are found", responseCode = "200"
			),
			@ApiResponse(
				description = "Valid users are not found", responseCode = "404"
			)
		}
	)
	@Consumes("application/scim+json")
	@Operation(
		description = "Return users according to the filter, sort and pagination parameters"
	)
	@Path("/.search")
	@POST
	@Produces({"application/json", "application/scim+json"})
	public Response getUsersByPost(String resourceString);

	@ApiResponses(
		{
			@ApiResponse(description = "User is updated", responseCode = "200"),
			@ApiResponse(
				description = "Valid user is not found", responseCode = "404"
			)
		}
	)
	@Consumes("application/scim+json")
	@Operation(description = "Return the updated user")
	@Parameters(
		{
			@Parameter(
				description = SCIMCharonConstants.ID_DESC,
				in = ParameterIn.PATH, name = "id", required = true
			),
			@Parameter(
				description = SCIMCharonConstants.ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "attribute", required = false
			),
			@Parameter(
				description = SCIMCharonConstants.EXCLUDED_ATTRIBUTES_DESC,
				in = ParameterIn.QUERY, name = "excludedAttributes",
				required = false
			)
		}
	)
	@Path("{id}")
	@Produces({"application/json", "application/scim+json"})
	@PUT
	public Response updateUser(
		@PathParam(SCIMCharonConstants.ID) String id,
		@QueryParam(SCIMCharonConstants.ATTRIBUTES) String attribute,
		@QueryParam(SCIMCharonConstants.EXCLUDE_ATTRIBUTES) String
			excludedAttributes,
		String resourceString);

}