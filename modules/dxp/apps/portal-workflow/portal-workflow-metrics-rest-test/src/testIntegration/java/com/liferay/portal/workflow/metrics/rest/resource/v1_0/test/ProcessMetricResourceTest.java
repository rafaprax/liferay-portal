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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ProcessMetric;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@Ignore
@RunWith(Arquillian.class)
public class ProcessMetricResourceTest
	extends BaseProcessMetricResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseProcessMetricResourceTestCase.setUpClass();

		_workflowMetricsRESTTestHelper = new WorkflowMetricsRESTTestHelper(
			_documentBuilderFactory, _queries, _searchEngineAdapter);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_documents = _workflowMetricsRESTTestHelper.getDocuments(
			testGroup.getCompanyId());

		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.deleteProcess(document);
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.restoreProcess(document);
		}

		_deleteProcesses();
	}

	@Override
	@Test
	public void testGetProcessMetric() throws Exception {
		super.testGetProcessMetric();

		_testGetProcessMetric(
			true,
			(processMetric1, processMetric2) -> assertEquals(
				processMetric1, processMetric2));
		_testGetProcessMetric(
			false,
			(processMetric1, processMetric2) -> assertEquals(
				processMetric1, processMetric2));
	}

	@Override
	@Test
	public void testGetProcessMetricsPage() throws Exception {
		super.testGetProcessMetricsPage();

		_deleteProcesses();

		ProcessMetric processMetric = randomProcessMetric();

		testGetProcessMetricsPage_addProcessMetric(processMetric);

		testGetProcessMetricsPage_addProcessMetric(randomProcessMetric());

		Process process = processMetric.getProcess();

		Page<ProcessMetric> page = processMetricResource.getProcessMetricsPage(
			process.getTitle(), Pagination.of(1, 2), null);

		assertEquals(
			Collections.singletonList(processMetric),
			(List<ProcessMetric>)page.getItems());
	}

	@Override
	@Test
	public void testGetProcessMetricsPageWithSortInteger() throws Exception {
		testGetProcessMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, processMetric1, processMetric2) -> {
				processMetric1.setInstanceCount(0L);
				processMetric1.setOnTimeInstanceCount(0L);
				processMetric1.setOverdueInstanceCount(0L);
				processMetric1.setUntrackedInstanceCount(0L);

				processMetric2.setInstanceCount(3L);
				processMetric2.setOnTimeInstanceCount(1L);
				processMetric2.setOverdueInstanceCount(1L);
				processMetric2.setUntrackedInstanceCount(1L);
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProcessMetric() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProcessMetricsPage() throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"instanceCount", "process", "onTimeInstanceCount",
			"overdueInstanceCount", "untrackedInstanceCount"
		};
	}

	@Override
	protected ProcessMetric randomProcessMetric() throws Exception {
		return new ProcessMetric() {
			{
				instanceCount = (long)RandomTestUtil.randomInt(0, 20);

				onTimeInstanceCount = (long)RandomTestUtil.randomInt(
					0, instanceCount.intValue());

				overdueInstanceCount = (long)RandomTestUtil.randomInt(
					0,
					instanceCount.intValue() - onTimeInstanceCount.intValue());

				process = new Process() {
					{
						id = RandomTestUtil.randomLong();

						title = RandomTestUtil.randomString();

						title_i18n = HashMapBuilder.put(
							LocaleUtil.US.toLanguageTag(), title
						).build();
					}
				};

				untrackedInstanceCount =
					instanceCount - onTimeInstanceCount - overdueInstanceCount;
			}
		};
	}

	@Override
	protected ProcessMetric testGetProcessMetricsPage_addProcessMetric(
			ProcessMetric processMetric)
		throws Exception {

		processMetric = _workflowMetricsRESTTestHelper.addProcessMetric(
			testGroup.getCompanyId(), processMetric);

		_processes.add(processMetric.getProcess());

		return processMetric;
	}

	private void _deleteProcesses() throws Exception {
		for (Process process : _processes) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), process);
		}
	}

	private void _testGetProcessMetric(
			Boolean completed,
			UnsafeBiConsumer<ProcessMetric, ProcessMetric, Exception>
				unsafeBiConsumer)
		throws Exception {

		_deleteProcesses();

		ProcessMetric postProcessMetric = randomProcessMetric();

		postProcessMetric.setInstanceCount(0L);
		postProcessMetric.setOnTimeInstanceCount(0L);
		postProcessMetric.setOverdueInstanceCount(0L);
		postProcessMetric.setUntrackedInstanceCount(0L);

		testGetProcessMetricsPage_addProcessMetric(postProcessMetric);

		Process postProcess = postProcessMetric.getProcess();

		_workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), completed, postProcess.getId());

		postProcessMetric.setInstanceCount(1L);
		postProcessMetric.setOnTimeInstanceCount(0L);
		postProcessMetric.setOverdueInstanceCount(0L);
		postProcessMetric.setUntrackedInstanceCount(1L);

		ProcessMetric getProcessMetric = processMetricResource.getProcessMetric(
			postProcess.getId(), completed, null, null);

		unsafeBiConsumer.accept(postProcessMetric, getProcessMetric);
	}

	@Inject
	private static DocumentBuilderFactory _documentBuilderFactory;

	private static Document[] _documents;

	@Inject
	private static Queries _queries;

	@Inject(blocking = false, filter = "search.engine.impl=Elasticsearch")
	private static SearchEngineAdapter _searchEngineAdapter;

	private static WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

	private final List<Process> _processes = new ArrayList<>();

}