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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.TaskMetric;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.time.DateUtils;

import org.junit.After;
import org.junit.Assert;
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
public class TaskMetricResourceTest extends BaseTaskMetricResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseTaskMetricResourceTestCase.setUpClass();

		_workflowMetricsRESTTestHelper = new WorkflowMetricsRESTTestHelper(
			_documentBuilderFactory, _queries, _searchEngineAdapter);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_process != null) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), _process);
		}

		_deleteTasks();
	}

	@Override
	@Test
	public void testGetProcessTaskMetricsPage() throws Exception {
		super.testGetProcessTaskMetricsPage();

		_deleteTasks();

		_workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process.getId(), "2.0");

		TaskMetric taskMetric1 = randomTaskMetric();

		taskMetric1.setBreachedInstanceCount(0L);
		taskMetric1.setDurationAvg(1000L);
		taskMetric1.setInstanceCount(1L);
		taskMetric1.setOnTimeInstanceCount(0L);
		taskMetric1.setOverdueInstanceCount(0L);

		testGetProcessTaskMetricsPage_addTaskMetric(
			_process.getId(), "COMPLETED", taskMetric1, "2.0");

		TaskMetric taskMetric2 = randomTaskMetric();

		taskMetric2.setBreachedInstanceCount(0L);
		taskMetric2.setDurationAvg(2000L);
		taskMetric2.setInstanceCount(1L);
		taskMetric2.setOnTimeInstanceCount(0L);
		taskMetric2.setOverdueInstanceCount(0L);

		testGetProcessTaskMetricsPage_addTaskMetric(
			_process.getId(), "COMPLETED", taskMetric2, "2.0");

		Page<TaskMetric> page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true, null, null, null, Pagination.of(1, 2),
			"durationAvg:asc");

		Task task1 = taskMetric1.getTask();

		Task task2 = taskMetric2.getTask();

		assertEquals(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric1.getBreachedInstancePercentage();
						durationAvg = taskMetric1.getDurationAvg();
						instanceCount = taskMetric1.getInstanceCount();
						task = new Task() {
							{
								label = task1.getLabel();
								name = task1.getName();
							}
						};
					}
				},
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric2.getBreachedInstancePercentage();
						durationAvg = taskMetric2.getDurationAvg();
						instanceCount = taskMetric2.getInstanceCount();
						task = new Task() {
							{
								label = task2.getLabel();
								name = task2.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());

		page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true, null, null, null, Pagination.of(1, 2),
			"overdueInstanceCount:asc");

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric1.getBreachedInstancePercentage();
						durationAvg = taskMetric1.getDurationAvg();
						instanceCount = taskMetric1.getInstanceCount();
						task = new Task() {
							{
								label = task1.getLabel();
								name = task1.getName();
							}
						};
					}
				},
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric2.getBreachedInstancePercentage();
						durationAvg = taskMetric2.getDurationAvg();
						instanceCount = taskMetric2.getInstanceCount();
						task = new Task() {
							{
								label = task2.getLabel();
								name = task2.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());

		page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true, null, null, task1.getName(),
			Pagination.of(1, 2), null);

		assertEquals(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric1.getBreachedInstancePercentage();
						durationAvg = taskMetric1.getDurationAvg();
						instanceCount = taskMetric1.getInstanceCount();
						task = new Task() {
							{
								label = task1.getLabel();
								name = task1.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());

		page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true, RandomTestUtil.nextDate(),
			DateUtils.addMinutes(RandomTestUtil.nextDate(), -2), null,
			Pagination.of(1, 2), "durationAvg:desc");

		assertEquals(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric2.getBreachedInstancePercentage();
						durationAvg = taskMetric2.getDurationAvg();
						instanceCount = taskMetric2.getInstanceCount();
						task = new Task() {
							{
								label = task2.getLabel();
								name = task2.getName();
							}
						};
					}
				},
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric1.getBreachedInstancePercentage();
						durationAvg = taskMetric1.getDurationAvg();
						instanceCount = taskMetric1.getInstanceCount();
						task = new Task() {
							{
								label = task1.getLabel();
								name = task1.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());

		page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true,
			DateUtils.addHours(RandomTestUtil.nextDate(), -1),
			DateUtils.addHours(RandomTestUtil.nextDate(), -2), null,
			Pagination.of(1, 2), "durationAvg:desc");

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount = 0L;
						durationAvg = 0L;
						instanceCount = 0L;
						task = new Task() {
							{
								label = task1.getLabel();
								name = task1.getName();
							}
						};
					}
				},
				new TaskMetric() {
					{
						breachedInstanceCount = 0L;
						durationAvg = 0L;
						instanceCount = 0L;
						task = new Task() {
							{
								label = task2.getLabel();
								name = task2.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());

		TaskMetric taskMetric3 = randomTaskMetric();

		taskMetric3.setBreachedInstanceCount(2L);
		taskMetric3.setBreachedInstancePercentage(100.0);
		taskMetric3.setDurationAvg(3000L);
		taskMetric3.setInstanceCount(2L);
		taskMetric3.setOnTimeInstanceCount(0L);
		taskMetric3.setOverdueInstanceCount(2L);

		testGetProcessTaskMetricsPage_addTaskMetric(
			_process.getId(), "COMPLETED", taskMetric3, "2.0");

		TaskMetric taskMetric4 = randomTaskMetric();

		taskMetric4.setBreachedInstanceCount(1L);
		taskMetric4.setBreachedInstancePercentage(50.0);
		taskMetric4.setDurationAvg(4000L);
		taskMetric4.setInstanceCount(2L);
		taskMetric4.setOnTimeInstanceCount(1L);
		taskMetric4.setOverdueInstanceCount(1L);

		testGetProcessTaskMetricsPage_addTaskMetric(
			_process.getId(), "COMPLETED", taskMetric4, "2.0");

		page = taskMetricResource.getProcessTaskMetricsPage(
			_process.getId(), true, null, null, null, Pagination.of(1, 2),
			"breachedInstancePercentage:desc");

		Task task3 = taskMetric3.getTask();

		Task task4 = taskMetric4.getTask();

		assertEquals(
			Arrays.asList(
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric3.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric3.getBreachedInstancePercentage();
						durationAvg = taskMetric3.getDurationAvg();
						instanceCount = taskMetric3.getInstanceCount();
						task = new Task() {
							{
								label = task3.getLabel();
								name = task3.getName();
							}
						};
					}
				},
				new TaskMetric() {
					{
						breachedInstanceCount =
							taskMetric4.getBreachedInstanceCount();
						breachedInstancePercentage =
							taskMetric4.getBreachedInstancePercentage();
						durationAvg = taskMetric4.getDurationAvg();
						instanceCount = taskMetric4.getInstanceCount();
						task = new Task() {
							{
								label = task4.getLabel();
								name = task4.getName();
							}
						};
					}
				}),
			(List<TaskMetric>)page.getItems());
	}

	@Override
	@Test
	public void testGetProcessTaskMetricsPageWithSortInteger()
		throws Exception {

		testGetProcessTaskMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taskMetric1, taskMetric2) -> {
				taskMetric1.setInstanceCount(0L);
				taskMetric1.setOnTimeInstanceCount(0L);
				taskMetric1.setOverdueInstanceCount(0L);

				taskMetric2.setInstanceCount(3L);
				taskMetric2.setOnTimeInstanceCount(1L);
				taskMetric2.setOverdueInstanceCount(1L);
			});
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"durationAvg", "instanceCount", "onTimeInstanceCount",
			"overdueInstanceCount", "task"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"durationAvg"};
	}

	@Override
	protected TaskMetric randomTaskMetric() throws Exception {
		return new TaskMetric() {
			{
				breachedInstanceCount = 0L;
				breachedInstancePercentage = 0.0;
				durationAvg = 0L;

				instanceCount = (long)RandomTestUtil.randomInt(0, 20);

				onTimeInstanceCount = (long)RandomTestUtil.randomInt(
					0, instanceCount.intValue());

				overdueInstanceCount = (long)RandomTestUtil.randomInt(
					0,
					instanceCount.intValue() - onTimeInstanceCount.intValue());

				task = new Task() {
					{
						id = RandomTestUtil.randomLong();
						label = RandomTestUtil.randomString();
						name = RandomTestUtil.randomString();
					}
				};
			}
		};
	}

	protected TaskMetric testGetProcessTaskMetricsPage_addTaskMetric(
			Long processId, String status, TaskMetric taskMetric,
			String version)
		throws Exception {

		User adminUser = UserTestUtil.getAdminUser(testGroup.getCompanyId());

		taskMetric = _workflowMetricsRESTTestHelper.addTaskMetric(
			adminUser.getUserId(), testGroup.getCompanyId(),
			() -> _workflowMetricsRESTTestHelper.addInstance(
				testGroup.getCompanyId(), Objects.equals(status, "COMPLETED"),
				processId),
			processId, status, taskMetric, version);

		_tasks.add(taskMetric.getTask());

		return taskMetric;
	}

	@Override
	protected TaskMetric testGetProcessTaskMetricsPage_addTaskMetric(
			Long processId, TaskMetric taskMetric)
		throws Exception {

		return testGetProcessTaskMetricsPage_addTaskMetric(
			processId, taskMetric, "1.0");
	}

	protected TaskMetric testGetProcessTaskMetricsPage_addTaskMetric(
			Long processId, TaskMetric task, String version)
		throws Exception {

		return testGetProcessTaskMetricsPage_addTaskMetric(
			processId, "RUNNING", task, version);
	}

	@Override
	protected Long testGetProcessTaskMetricsPage_getProcessId()
		throws Exception {

		return _process.getId();
	}

	private void _deleteTasks() throws Exception {
		for (Task task : _tasks) {
			_workflowMetricsRESTTestHelper.deleteTask(
				testGroup.getCompanyId(), _process.getId(), task);
		}
	}

	@Inject
	private static DocumentBuilderFactory _documentBuilderFactory;

	@Inject
	private static Queries _queries;

	@Inject(blocking = false, filter = "search.engine.impl=Elasticsearch")
	private static SearchEngineAdapter _searchEngineAdapter;

	private static WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

	private Process _process;
	private final List<Task> _tasks = new ArrayList<>();

}