/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.converter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.definition.converter.WorkflowDefinitionContentConverter;
import com.liferay.portal.workflow.kaleo.definition.internal.converter.constants.WorkflowDefinitionContentConverterConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 */
public class XMLToJSONWorkflowDefinitionContentConverterTest
	extends BaseWorkflowDefinitionContentConverterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_xmlToJSONWorkflowDefinitionConverter =
			new XMLToJSONWorkflowDefinitionContentConverter();

		ReflectionTestUtil.setFieldValue(
			_xmlToJSONWorkflowDefinitionConverter, "_jsonFactory",
			_jsonFactory);
	}

	@Test
	public void testConvertCDataContent() throws Exception {
		JSONObject jsonObject = convert("cdata.xml");

		Assert.assertEquals(
			"metadata",
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));

		Assert.assertFalse(
			jsonObject.has(
				WorkflowDefinitionContentConverterConstants.CHILD_NODES));
		Assert.assertFalse(
			jsonObject.has(WorkflowDefinitionContentConverterConstants.VALUE));
		Assert.assertTrue(
			jsonObject.has(
				WorkflowDefinitionContentConverterConstants.CDATA_VALUE));

		JSONArray jsonArray = jsonObject.getJSONArray(
			WorkflowDefinitionContentConverterConstants.CDATA_VALUE);

		Assert.assertEquals(jsonArray.toString(), 8, jsonArray.length());

		Assert.assertTrue(
			StringUtil.contains(
				jsonArray.getString(2), "xy", StringPool.BLANK));
		Assert.assertTrue(
			StringUtil.contains(
				jsonArray.getString(3), "168,", StringPool.BLANK));
		Assert.assertTrue(
			StringUtil.contains(
				jsonArray.getString(4), "36", StringPool.BLANK));
	}

	@Test
	public void testConvertRepeatableTags() throws Exception {
		JSONObject jsonObject = convert("repeatable-tag.xml");

		Assert.assertEquals(
			"container-tag",
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));

		JSONArray childJSONArray = jsonObject.getJSONArray(
			WorkflowDefinitionContentConverterConstants.CHILD_NODES);

		Assert.assertEquals(2, childJSONArray.length());

		JSONObject childJSONObject = childJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"repeatable-tag",
			childJSONObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));
		Assert.assertEquals(
			"first",
			childJSONObject.getString(
				WorkflowDefinitionContentConverterConstants.VALUE));

		childJSONObject = childJSONArray.getJSONObject(1);

		Assert.assertEquals(
			"repeatable-tag",
			childJSONObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));
		Assert.assertEquals(
			"second",
			childJSONObject.getString(
				WorkflowDefinitionContentConverterConstants.VALUE));
	}

	@Test
	public void testConvertSimpleNode() throws Exception {
		JSONObject jsonObject = convert("simple-tag.xml");

		Assert.assertEquals(
			"test",
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));

		Assert.assertEquals(
			"simple tag",
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.VALUE));
	}

	@Test
	public void testConvertTagWithAttributesAndContent() throws Exception {
		JSONObject jsonObject = convert("repeatable-tag-with-attributes.xml");

		Assert.assertEquals(
			"labels",
			jsonObject.getString(
				WorkflowDefinitionContentConverterConstants.TAG_NAME));

		JSONArray childJSONArray = jsonObject.getJSONArray(
			WorkflowDefinitionContentConverterConstants.CHILD_NODES);

		Assert.assertEquals(1, childJSONArray.length());

		JSONObject labelJSONObject = childJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"Label",
			labelJSONObject.getString(
				WorkflowDefinitionContentConverterConstants.VALUE));

		Assert.assertEquals("en_US", labelJSONObject.getString("language-id"));
	}

	@Override
	protected WorkflowDefinitionContentConverter
		getWorkflowDefinitionContentConverter() {

		return _xmlToJSONWorkflowDefinitionConverter;
	}

	@Override
	protected JSONObject processContent(String content) throws Exception {
		return _jsonFactory.createJSONObject(content);
	}

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private XMLToJSONWorkflowDefinitionContentConverter
		_xmlToJSONWorkflowDefinitionConverter;

}