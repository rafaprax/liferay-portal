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

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.TaskMetric;
import com.liferay.portal.workflow.metrics.rest.client.http.HttpInvoker;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.client.resource.v1_0.TaskMetricResource;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.TaskMetricSerDes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.time.DateUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public abstract class BaseTaskMetricResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_taskMetricResource.setContextCompany(testCompany);

		TaskMetricResource.Builder builder = TaskMetricResource.builder();

		taskMetricResource = builder.locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		TaskMetric taskMetric1 = randomTaskMetric();

		String json = objectMapper.writeValueAsString(taskMetric1);

		TaskMetric taskMetric2 = TaskMetricSerDes.toDTO(json);

		Assert.assertTrue(equals(taskMetric1, taskMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		TaskMetric taskMetric = randomTaskMetric();

		String json1 = objectMapper.writeValueAsString(taskMetric);
		String json2 = TaskMetricSerDes.toJSON(taskMetric);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		TaskMetric taskMetric = randomTaskMetric();

		String json = TaskMetricSerDes.toJSON(taskMetric);

		Assert.assertFalse(json.contains(regex));

		taskMetric = TaskMetricSerDes.toDTO(json);
	}

	@Test
	public void testGetProcessTaskMetricsPage() throws Exception {
		Page<TaskMetric> page = taskMetricResource.getProcessTaskMetricsPage(
			testGetProcessTaskMetricsPage_getProcessId(), null,
			RandomTestUtil.nextDate(), RandomTestUtil.nextDate(),
			RandomTestUtil.randomString(), Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		Long processId = testGetProcessTaskMetricsPage_getProcessId();
		Long irrelevantProcessId =
			testGetProcessTaskMetricsPage_getIrrelevantProcessId();

		if ((irrelevantProcessId != null)) {
			TaskMetric irrelevantTaskMetric =
				testGetProcessTaskMetricsPage_addTaskMetric(
					irrelevantProcessId, randomIrrelevantTaskMetric());

			page = taskMetricResource.getProcessTaskMetricsPage(
				irrelevantProcessId, null, null, null, null,
				Pagination.of(1, 2), null);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantTaskMetric),
				(List<TaskMetric>)page.getItems());
			assertValid(page);
		}

		TaskMetric taskMetric1 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, randomTaskMetric());

		TaskMetric taskMetric2 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, randomTaskMetric());

		page = taskMetricResource.getProcessTaskMetricsPage(
			processId, null, null, null, null, Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(taskMetric1, taskMetric2),
			(List<TaskMetric>)page.getItems());
		assertValid(page);
	}

	@Test
	public void testGetProcessTaskMetricsPageWithPagination() throws Exception {
		Long processId = testGetProcessTaskMetricsPage_getProcessId();

		TaskMetric taskMetric1 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, randomTaskMetric());

		TaskMetric taskMetric2 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, randomTaskMetric());

		TaskMetric taskMetric3 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, randomTaskMetric());

		Page<TaskMetric> page1 = taskMetricResource.getProcessTaskMetricsPage(
			processId, null, null, null, null, Pagination.of(1, 2), null);

		List<TaskMetric> taskMetrics1 = (List<TaskMetric>)page1.getItems();

		Assert.assertEquals(taskMetrics1.toString(), 2, taskMetrics1.size());

		Page<TaskMetric> page2 = taskMetricResource.getProcessTaskMetricsPage(
			processId, null, null, null, null, Pagination.of(2, 2), null);

		Assert.assertEquals(3, page2.getTotalCount());

		List<TaskMetric> taskMetrics2 = (List<TaskMetric>)page2.getItems();

		Assert.assertEquals(taskMetrics2.toString(), 1, taskMetrics2.size());

		Page<TaskMetric> page3 = taskMetricResource.getProcessTaskMetricsPage(
			processId, null, null, null, null, Pagination.of(1, 3), null);

		assertEqualsIgnoringOrder(
			Arrays.asList(taskMetric1, taskMetric2, taskMetric3),
			(List<TaskMetric>)page3.getItems());
	}

	@Test
	public void testGetProcessTaskMetricsPageWithSortDateTime()
		throws Exception {

		testGetProcessTaskMetricsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taskMetric1, taskMetric2) -> {
				BeanUtils.setProperty(
					taskMetric1, entityField.getName(),
					DateUtils.addMinutes(new Date(), -2));
			});
	}

	@Test
	public void testGetProcessTaskMetricsPageWithSortInteger()
		throws Exception {

		testGetProcessTaskMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taskMetric1, taskMetric2) -> {
				BeanUtils.setProperty(taskMetric1, entityField.getName(), 0);
				BeanUtils.setProperty(taskMetric2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProcessTaskMetricsPageWithSortString() throws Exception {
		testGetProcessTaskMetricsPageWithSort(
			EntityField.Type.STRING,
			(entityField, taskMetric1, taskMetric2) -> {
				Class<?> clazz = taskMetric1.getClass();

				Method method = clazz.getMethod(
					"get" +
						StringUtil.upperCaseFirstLetter(entityField.getName()));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanUtils.setProperty(
						taskMetric1, entityField.getName(),
						Collections.singletonMap("Aaa", "Aaa"));
					BeanUtils.setProperty(
						taskMetric2, entityField.getName(),
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else {
					BeanUtils.setProperty(
						taskMetric1, entityField.getName(), "Aaa");
					BeanUtils.setProperty(
						taskMetric2, entityField.getName(), "Bbb");
				}
			});
	}

	protected void testGetProcessTaskMetricsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, TaskMetric, TaskMetric, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long processId = testGetProcessTaskMetricsPage_getProcessId();

		TaskMetric taskMetric1 = randomTaskMetric();
		TaskMetric taskMetric2 = randomTaskMetric();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, taskMetric1, taskMetric2);
		}

		taskMetric1 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, taskMetric1);

		taskMetric2 = testGetProcessTaskMetricsPage_addTaskMetric(
			processId, taskMetric2);

		for (EntityField entityField : entityFields) {
			Page<TaskMetric> ascPage =
				taskMetricResource.getProcessTaskMetricsPage(
					processId, null, null, null, null, Pagination.of(1, 2),
					entityField.getName() + ":asc");

			assertEquals(
				Arrays.asList(taskMetric1, taskMetric2),
				(List<TaskMetric>)ascPage.getItems());

			Page<TaskMetric> descPage =
				taskMetricResource.getProcessTaskMetricsPage(
					processId, null, null, null, null, Pagination.of(1, 2),
					entityField.getName() + ":desc");

			assertEquals(
				Arrays.asList(taskMetric2, taskMetric1),
				(List<TaskMetric>)descPage.getItems());
		}
	}

	protected TaskMetric testGetProcessTaskMetricsPage_addTaskMetric(
			Long processId, TaskMetric taskMetric)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessTaskMetricsPage_getProcessId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessTaskMetricsPage_getIrrelevantProcessId()
		throws Exception {

		return null;
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaskMetric taskMetric1, TaskMetric taskMetric2) {

		Assert.assertTrue(
			taskMetric1 + " does not equal " + taskMetric2,
			equals(taskMetric1, taskMetric2));
	}

	protected void assertEquals(
		List<TaskMetric> taskMetrics1, List<TaskMetric> taskMetrics2) {

		Assert.assertEquals(taskMetrics1.size(), taskMetrics2.size());

		for (int i = 0; i < taskMetrics1.size(); i++) {
			TaskMetric taskMetric1 = taskMetrics1.get(i);
			TaskMetric taskMetric2 = taskMetrics2.get(i);

			assertEquals(taskMetric1, taskMetric2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaskMetric> taskMetrics1, List<TaskMetric> taskMetrics2) {

		Assert.assertEquals(taskMetrics1.size(), taskMetrics2.size());

		for (TaskMetric taskMetric1 : taskMetrics1) {
			boolean contains = false;

			for (TaskMetric taskMetric2 : taskMetrics2) {
				if (equals(taskMetric1, taskMetric2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taskMetrics2 + " does not contain " + taskMetric1, contains);
		}
	}

	protected void assertEqualsJSONArray(
		List<TaskMetric> taskMetrics, JSONArray jsonArray) {

		for (TaskMetric taskMetric : taskMetrics) {
			boolean contains = false;

			for (Object object : jsonArray) {
				if (equalsJSONObject(taskMetric, (JSONObject)object)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				jsonArray + " does not contain " + taskMetric, contains);
		}
	}

	protected void assertValid(TaskMetric taskMetric) {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"breachedInstanceCount", additionalAssertFieldName)) {

				if (taskMetric.getBreachedInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"breachedInstancePercentage", additionalAssertFieldName)) {

				if (taskMetric.getBreachedInstancePercentage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("durationAvg", additionalAssertFieldName)) {
				if (taskMetric.getDurationAvg() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("instanceCount", additionalAssertFieldName)) {
				if (taskMetric.getInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"onTimeInstanceCount", additionalAssertFieldName)) {

				if (taskMetric.getOnTimeInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"overdueInstanceCount", additionalAssertFieldName)) {

				if (taskMetric.getOverdueInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("task", additionalAssertFieldName)) {
				if (taskMetric.getTask() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<TaskMetric> page) {
		boolean valid = false;

		java.util.Collection<TaskMetric> taskMetrics = page.getItems();

		int size = taskMetrics.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			graphQLFields.add(new GraphQLField(additionalAssertFieldName));
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(TaskMetric taskMetric1, TaskMetric taskMetric2) {
		if (taskMetric1 == taskMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"breachedInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taskMetric1.getBreachedInstanceCount(),
						taskMetric2.getBreachedInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"breachedInstancePercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taskMetric1.getBreachedInstancePercentage(),
						taskMetric2.getBreachedInstancePercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("durationAvg", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskMetric1.getDurationAvg(),
						taskMetric2.getDurationAvg())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("instanceCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskMetric1.getInstanceCount(),
						taskMetric2.getInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"onTimeInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taskMetric1.getOnTimeInstanceCount(),
						taskMetric2.getOnTimeInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"overdueInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taskMetric1.getOverdueInstanceCount(),
						taskMetric2.getOverdueInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("task", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskMetric1.getTask(), taskMetric2.getTask())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equalsJSONObject(
		TaskMetric taskMetric, JSONObject jsonObject) {

		for (String fieldName : getAdditionalAssertFieldNames()) {
			if (Objects.equals("breachedInstanceCount", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getBreachedInstanceCount(),
						jsonObject.getLong("breachedInstanceCount"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("breachedInstancePercentage", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getBreachedInstancePercentage(),
						jsonObject.getDouble("breachedInstancePercentage"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("durationAvg", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getDurationAvg(),
						jsonObject.getLong("durationAvg"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("instanceCount", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getInstanceCount(),
						jsonObject.getLong("instanceCount"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("onTimeInstanceCount", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getOnTimeInstanceCount(),
						jsonObject.getLong("onTimeInstanceCount"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("overdueInstanceCount", fieldName)) {
				if (!Objects.deepEquals(
						taskMetric.getOverdueInstanceCount(),
						jsonObject.getLong("overdueInstanceCount"))) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid field name " + fieldName);
		}

		return true;
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_taskMetricResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taskMetricResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		java.util.Collection<EntityField> entityFields = getEntityFields();

		Stream<EntityField> stream = entityFields.stream();

		return stream.filter(
			entityField ->
				Objects.equals(entityField.getType(), type) &&
				!ArrayUtil.contains(
					getIgnoredEntityFieldNames(), entityField.getName())
		).collect(
			Collectors.toList()
		);
	}

	protected String getFilterString(
		EntityField entityField, String operator, TaskMetric taskMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("breachedInstanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("breachedInstancePercentage")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("durationAvg")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("instanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("onTimeInstanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("overdueInstanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("task")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected TaskMetric randomTaskMetric() throws Exception {
		return new TaskMetric() {
			{
				breachedInstanceCount = RandomTestUtil.randomLong();
				breachedInstancePercentage = RandomTestUtil.randomDouble();
				durationAvg = RandomTestUtil.randomLong();
				instanceCount = RandomTestUtil.randomLong();
				onTimeInstanceCount = RandomTestUtil.randomLong();
				overdueInstanceCount = RandomTestUtil.randomLong();
			}
		};
	}

	protected TaskMetric randomIrrelevantTaskMetric() throws Exception {
		TaskMetric randomIrrelevantTaskMetric = randomTaskMetric();

		return randomIrrelevantTaskMetric;
	}

	protected TaskMetric randomPatchTaskMetric() throws Exception {
		return randomTaskMetric();
	}

	protected TaskMetricResource taskMetricResource;
	protected Group irrelevantGroup;
	protected Company testCompany;
	protected Group testGroup;

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(":");
					sb.append(entry.getValue());
					sb.append(",");
				}

				sb.setLength(sb.length() - 1);

				sb.append(")");
			}

			if (_graphQLFields.length > 0) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(",");
				}

				sb.setLength(sb.length() - 1);

				sb.append("}");
			}

			return sb.toString();
		}

		private final GraphQLField[] _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseTaskMetricResourceTestCase.class);

	private static BeanUtilsBean _beanUtilsBean = new BeanUtilsBean() {

		@Override
		public void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {

			if (value != null) {
				super.copyProperty(bean, name, value);
			}
		}

	};
	private static DateFormat _dateFormat;

	@Inject
	private
		com.liferay.portal.workflow.metrics.rest.resource.v1_0.
			TaskMetricResource _taskMetricResource;

}