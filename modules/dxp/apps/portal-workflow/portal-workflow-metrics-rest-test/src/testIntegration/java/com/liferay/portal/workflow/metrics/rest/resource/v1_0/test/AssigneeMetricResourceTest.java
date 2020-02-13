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
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.AssigneeMetric;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
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

import org.apache.commons.beanutils.BeanUtils;
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
public class AssigneeMetricResourceTest
	extends BaseAssigneeMetricResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseAssigneeResourceTestCase.setUpClass();

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

		_deleteSLATaskResults();
		_deleteTasks();
		_deleteTokens();
	}

	@Override
	@Test
	public void testGetProcessAssigneeMetricsPage() throws Exception {
		super.testGetProcessAssigneeMetricsPage();

		_deleteSLATaskResults();
		_deleteTasks();
		_deleteTokens();

		AssigneeMetric assigneeMetric1 = randomAssigneeMetric();

		assigneeMetric1.setOnTimeTaskCount(0L);
		assigneeMetric1.setOverdueTaskCount(3L);
		assigneeMetric1.setTaskCount(3L);

		Assignee assignee1 = assigneeMetric1.getAssignee();

		Instance instance1 = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), false, _process.getId());

		_addTaskMetric(
			assignee1.getId(), () -> instance1, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 1L;
					task = new Task() {
						{
							label = "review";
							name = "review";
						}
					};
				}
			});

		_addTaskMetric(
			assignee1.getId(), () -> instance1, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 1L;
					task = new Task() {
						{
							label = "update";
							name = "update";
						}
					};
				}
			});

		Instance instance2 = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), false, _process.getId());

		_addTaskMetric(
			assignee1.getId(), () -> instance2, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 1L;
					task = new Task() {
						{
							label = "update";
							name = "update";
						}
					};
				}
			});

		Role siteAdministrationRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		AssigneeMetric assigneeMetric2 = _randomAssigneeMetric(
			siteAdministrationRole);

		Assignee assignee2 = assigneeMetric2.getAssignee();

		assigneeMetric2.setOnTimeTaskCount(1L);
		assigneeMetric2.setOverdueTaskCount(1L);
		assigneeMetric2.setTaskCount(2L);

		_addTaskMetric(
			assignee2.getId(), () -> instance1, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 1L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "review";
							name = "review";
						}
					};
				}
			});

		_addTaskMetric(
			assignee2.getId(), () -> instance2, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 1L;
					task = new Task() {
						{
							label = "submit";
							name = "submit";
						}
					};
				}
			});

		Page<AssigneeMetric> page =
			assigneeMetricResource.getProcessAssigneeMetricsPage(
				_process.getId(), false, null, null, null, null,
				new String[] {"update"}, Pagination.of(1, 10), "taskCount:asc");

		Assert.assertEquals(1, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				new AssigneeMetric() {
					{
						assignee = assignee1;
						durationTaskAvg = 0L;
						onTimeTaskCount = 0L;
						overdueTaskCount = 2L;
						taskCount = 2L;
					}
				}),
			(List<AssigneeMetric>)page.getItems());

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), false, null, null, null, null,
			new String[] {"review"}, Pagination.of(1, 10),
			"overdueTaskCount:desc");

		Assert.assertEquals(2, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				new AssigneeMetric() {
					{
						assignee = assignee1;
						durationTaskAvg = 0L;
						onTimeTaskCount = 0L;
						overdueTaskCount = 1L;
						taskCount = 1L;
					}
				},
				new AssigneeMetric() {
					{
						assignee = assignee2;
						durationTaskAvg = 0L;
						onTimeTaskCount = 1L;
						overdueTaskCount = 0L;
						taskCount = 1L;
					}
				}),
			(List<AssigneeMetric>)page.getItems());

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), false, null, null, null,
			new Long[] {siteAdministrationRole.getRoleId()},
			new String[] {"review"}, Pagination.of(1, 10),
			"overdueTaskCount:desc");

		Assert.assertEquals(1, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				new AssigneeMetric() {
					{
						assignee = assignee2;
						durationTaskAvg = 0L;
						onTimeTaskCount = 1L;
						overdueTaskCount = 0L;
						taskCount = 1L;
					}
				}),
			(List<AssigneeMetric>)page.getItems());

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), false, null, null, assignee2.getName(),
			new Long[] {siteAdministrationRole.getRoleId()},
			new String[] {"review"}, Pagination.of(1, 10),
			"overdueTaskCount:desc");

		Assert.assertEquals(1, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				new AssigneeMetric() {
					{
						assignee = assignee2;
						durationTaskAvg = 0L;
						onTimeTaskCount = 1L;
						overdueTaskCount = 0L;
						taskCount = 1L;
					}
				}),
			(List<AssigneeMetric>)page.getItems());

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), false, null, null, assignee1.getName(),
			new Long[] {siteAdministrationRole.getRoleId()},
			new String[] {"review"}, Pagination.of(1, 10),
			"overdueTaskCount:desc");

		Assert.assertEquals(0, page.getTotalCount());

		AssigneeMetric assigneeMetric3 = randomAssigneeMetric();

		Assignee assignee3 = assigneeMetric3.getAssignee();

		assigneeMetric3.setOnTimeTaskCount(0L);
		assigneeMetric3.setOverdueTaskCount(0L);
		assigneeMetric3.setTaskCount(1L);

		_addTaskMetric(
			assignee3.getId(), () -> instance1, _process.getId(),
			new TaskMetric() {
				{
					durationAvg = 0L;
					instanceCount = 1L;
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "review";
							name = "review";
						}
					};
				}
			});

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), false, null, null, null, null, null,
			Pagination.of(1, 10), "overdueTaskCount:desc");

		Assert.assertEquals(3, page.getTotalCount());

		assertEquals(
			Arrays.asList(assigneeMetric1, assigneeMetric2, assigneeMetric3),
			(List<AssigneeMetric>)page.getItems());

		Instance instance3 = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), true, _process.getId());

		_addTaskMetric(
			assignee1.getId(), () -> instance3, _process.getId(), "COMPLETED",
			new TaskMetric() {
				{
					durationAvg = 1000L;
					instanceCount = 1L;
					onTimeInstanceCount = 1L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "review";
							name = "review";
						}
					};
				}
			},
			new TaskMetric() {
				{
					durationAvg = 2000L;
					instanceCount = 1L;
					onTimeInstanceCount = 1L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "update";
							name = "update";
						}
					};
				}
			});

		_addTaskMetric(
			assignee2.getId(), () -> instance3, _process.getId(), "COMPLETED",
			new TaskMetric() {
				{
					durationAvg = 2000L;
					instanceCount = 1L;
					onTimeInstanceCount = 1L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "review";
							name = "review";
						}
					};
				}
			},
			new TaskMetric() {
				{
					durationAvg = 4000L;
					instanceCount = 1L;
					onTimeInstanceCount = 1L;
					overdueInstanceCount = 0L;
					task = new Task() {
						{
							label = "update";
							name = "update";
						}
					};
				}
			});

		page = assigneeMetricResource.getProcessAssigneeMetricsPage(
			_process.getId(), true, RandomTestUtil.nextDate(),
			DateUtils.addMinutes(RandomTestUtil.nextDate(), -2), null, null,
			null, Pagination.of(1, 10), "durationTaskAvg:asc");

		Assert.assertEquals(2, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				new AssigneeMetric() {
					{
						assignee = assignee1;
						durationTaskAvg = 1500L;
						onTimeTaskCount = 2L;
						overdueTaskCount = 0L;
						taskCount = 2L;
					}
				},
				new AssigneeMetric() {
					{
						assignee = assignee2;
						durationTaskAvg = 3000L;
						onTimeTaskCount = 2L;
						overdueTaskCount = 0L;
						taskCount = 2L;
					}
				}),
			(List<AssigneeMetric>)page.getItems());
	}

	@Override
	@Test
	public void testGetProcessAssigneeMetricsPageWithSortInteger()
		throws Exception {

		testGetProcessAssigneeMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, assigneeMetric1, assigneeMetric2) -> {
				if (Objects.equals(entityField.getName(), "taskCount")) {
					assigneeMetric1.setTaskCount(1L);
					assigneeMetric2.setTaskCount(3L);
				}
				else if (Objects.equals(
							entityField.getName(), "onTimeTaskCount")) {

					assigneeMetric1.setOnTimeTaskCount(0L);
					assigneeMetric2.setOnTimeTaskCount(1L);
				}
				else if (Objects.equals(
							entityField.getName(), "overdueTaskCount")) {

					assigneeMetric1.setOverdueTaskCount(1L);
					assigneeMetric2.setOverdueTaskCount(2L);
				}
				else {
					BeanUtils.setProperty(
						assigneeMetric1, entityField.getName(), 1);
					BeanUtils.setProperty(
						assigneeMetric2, entityField.getName(), 2);
				}
			});
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"durationTaskAvg", "onTimeTaskCount", "overdueTaskCount",
			"taskCount"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"durationTaskAvg"};
	}

	@Override
	protected AssigneeMetric randomAssigneeMetric() throws Exception {
		User user = UserTestUtil.addUser();

		return new AssigneeMetric() {
			{
				assignee = new Assignee() {
					{
						id = user.getUserId();
						image = user.getPortraitURL(
							new ThemeDisplay() {
								{
									setPathImage(_portal.getPathImage());
								}
							});
						name = user.getFullName();
					}
				};
				durationTaskAvg = 0L;
				onTimeTaskCount = 1L;
				overdueTaskCount = 0L;
				taskCount = 1L;
			}
		};
	}

	@Override
	protected AssigneeMetric
			testGetProcessAssigneeMetricsPage_addAssigneeMetric(
				Long processId, AssigneeMetric assigneeMetric)
		throws Exception {

		Assignee assignee = assigneeMetric.getAssignee();

		_addTaskMetric(
			assignee.getId(),
			() -> _workflowMetricsRESTTestHelper.addInstance(
				testGroup.getCompanyId(), false, _process.getId()),
			processId,
			new TaskMetric() {
				{
					durationAvg = assigneeMetric.getDurationTaskAvg();
					instanceCount = assigneeMetric.getTaskCount();

					String randomString = RandomTestUtil.randomString();

					onTimeInstanceCount = assigneeMetric.getOnTimeTaskCount();
					overdueInstanceCount = assigneeMetric.getOverdueTaskCount();
					task = new Task() {
						{
							label = randomString;
							name = randomString;
						}
					};
				}
			});

		return assigneeMetric;
	}

	@Override
	protected Long testGetProcessAssigneeMetricsPage_getProcessId()
		throws Exception {

		return _process.getId();
	}

	private void _addRoleUser(Role role, long userId) throws Exception {
		_userLocalService.addRoleUser(role.getRoleId(), userId);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {userId}, TestPropsValues.getGroupId(),
			role.getRoleId());
	}

	private void _addTaskMetric(
			long assigneeId,
			UnsafeSupplier<Instance, Exception> instanceSupplier,
			long processId, String status, TaskMetric... taskMetrics)
		throws Exception {

		for (TaskMetric taskMetric : taskMetrics) {
			_workflowMetricsRESTTestHelper.addTaskMetric(
				assigneeId, testGroup.getCompanyId(), instanceSupplier,
				processId, status, taskMetric, "1.0");

			_tasks.add(taskMetric.getTask());
		}
	}

	private void _addTaskMetric(
			long assigneeId,
			UnsafeSupplier<Instance, Exception> instanceSupplier,
			long processId, TaskMetric... taskMetrics)
		throws Exception {

		_addTaskMetric(
			assigneeId, instanceSupplier, processId, "RUNNING", taskMetrics);
	}

	private void _deleteSLATaskResults() throws Exception {
		_workflowMetricsRESTTestHelper.deleteSLATaskResults(
			testGroup.getCompanyId(), _process.getId());
	}

	private void _deleteTasks() throws Exception {
		for (Task task : _tasks) {
			_workflowMetricsRESTTestHelper.deleteTask(
				testGroup.getCompanyId(), _process.getId(), task);
		}

		_tasks.clear();
	}

	private void _deleteTokens() throws Exception {
		_workflowMetricsRESTTestHelper.deleteTokens(
			testGroup.getCompanyId(), _process.getId());
	}

	private AssigneeMetric _randomAssigneeMetric(Role role) throws Exception {
		AssigneeMetric assigneeMetric = randomAssigneeMetric();

		Assignee assignee = assigneeMetric.getAssignee();

		_addRoleUser(role, assignee.getId());

		return assigneeMetric;
	}

	@Inject
	private static DocumentBuilderFactory _documentBuilderFactory;

	@Inject
	private static Queries _queries;

	@Inject(blocking = false, filter = "search.engine.impl=Elasticsearch")
	private static SearchEngineAdapter _searchEngineAdapter;

	private static WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

	@Inject
	private Portal _portal;

	private Process _process;

	@Inject
	private RoleLocalService _roleLocalService;

	private final List<Task> _tasks = new ArrayList<>();

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}