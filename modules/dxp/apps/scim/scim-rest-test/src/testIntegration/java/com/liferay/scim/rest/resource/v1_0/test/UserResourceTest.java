/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.dto.v1_0.Name;
import com.liferay.scim.rest.client.dto.v1_0.User;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Olivér Kecskeméty
 */
@RunWith(Arquillian.class)
public class UserResourceTest extends BaseUserResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseUserResourceTestCase.setUpClass();

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.scim.rest.internal.configuration." +
				"ScimClientOAuth2ApplicationConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"matcherField", "email"
			).put(
				"oAuth2ApplicationName", "scim-client-test"
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@Override
	@Test
	public void testGetV2User() throws Exception {
		Object response = userResource.getV2User(2, 0);

		_assertListResponse(
			_jsonFactory.createJSONObject(response.toString()), 0);

		User user1 = testDeleteV2User_addUser();
		User user2 = testDeleteV2User_addUser();

		response = userResource.getV2User(2, 0);

		_assertListResponse(
			_jsonFactory.createJSONObject(response.toString()), 2, user1,
			user2);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"emails", "externalId", "name", "title", "userName"
		};
	}

	@Override
	protected User randomUser() throws Exception {
		User user = super.randomUser();

		user.setActive(true);
		user.setEmails(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						primary = true;
						type = "default";
						value = user.getUserName() + "@liferay.com";
					}
				}
			});
		user.setId((String)null);
		user.setName(
			new Name() {
				{
					familyName = RandomTestUtil.randomString();
					givenName = RandomTestUtil.randomString();
					middleName = RandomTestUtil.randomString();
				}
			});
		user.setSchemas(
			new String[] {"urn:ietf:params:scim:schemas:core:2.0:User"});

		return user;
	}

	@Override
	protected User testDeleteV2User_addUser() throws Exception {
		User user = randomUser();

		userResource.postV2User(user);

		return user;
	}

	private void _assertListResponse(
		JSONObject listResponseJSONObject, long expectedTotalResults,
		User... users) {

		JSONArray schemasJSONArray = listResponseJSONObject.getJSONArray(
			"schemas");

		Assert.assertEquals(
			"urn:ietf:params:scim:api:messages:2.0:ListResponse",
			schemasJSONArray.get(0));

		Assert.assertEquals(
			expectedTotalResults,
			listResponseJSONObject.getLong("totalResults"));

		if (ArrayUtil.isEmpty(users)) {
			return;
		}

		JSONArray resourcesJSONArray = listResponseJSONObject.getJSONArray(
			"Resources");

		Assert.assertEquals(users.length, resourcesJSONArray.length());

		for (int i = 0; i < resourcesJSONArray.length(); i++) {
			JSONObject userJSONObject = resourcesJSONArray.getJSONObject(i);

			assertContains(
				User.toDTO(userJSONObject.toString()), Arrays.asList(users));
		}
	}

	private static String _pid;

	@Inject
	private JSONFactory _jsonFactory;

}