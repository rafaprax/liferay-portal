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

package com.liferay.portal.workflow.metrics.internal.search;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = WorkflowMetricsReindexer.class)
public class WorkflowMetricsReindexer {

	@Activate
	protected void activate() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_companyLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			(Company company) -> reindex(company.getCompanyId()));

		actionableDynamicQuery.performActions();
	}

	protected void reindex(long companyId) {
		try {
			_instanceWorkflowMetricsIndex.clearIndex(companyId);
			_nodeWorkflowMetricsIndex.clearIndex(companyId);
			_processWorkflowMetricsIndex.clearIndex(companyId);
			_slaInstanceResultWorkflowMetricsIndex.clearIndex(companyId);
			_slaTaskResultWorkflowMetricsIndex.clearIndex(companyId);
			_taskWorkflowMetricsIndex.clearIndex(companyId);
			_transitionWorkflowMetricsIndex.clearIndex(companyId);

			_instanceWorkflowMetricsIndex.createIndex(companyId);
			_nodeWorkflowMetricsIndex.createIndex(companyId);
			_processWorkflowMetricsIndex.createIndex(companyId);
			_slaInstanceResultWorkflowMetricsIndex.createIndex(companyId);
			_slaTaskResultWorkflowMetricsIndex.createIndex(companyId);
			_taskWorkflowMetricsIndex.createIndex(companyId);
			_transitionWorkflowMetricsIndex.createIndex(companyId);

			_instanceWorkflowMetricsReindexer.reindex(companyId);
			_nodeWorkflowMetricsReindexer.reindex(companyId);
			_processWorkflowMetricsReindexer.reindex(companyId);
			_taskWorkflowMetricsReindexer.reindex(companyId);
			_transitionWorkflowMetricsReindexer.reindex(companyId);

			_slaInstanceResultWorkflowMetricsReindexer.reindex(companyId);
			_slaTaskResultWorkflowMetricsReindexer.reindex(companyId);
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsReindexer.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = "(workflow.metrics.index.entity.name=instance)")
	private WorkflowMetricsIndex _instanceWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=instance)")
	private WorkflowMetricsReindexer _instanceWorkflowMetricsReindexer;

	@Reference(target = "(workflow.metrics.index.entity.name=node)")
	private WorkflowMetricsIndex _nodeWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=node)")
	private WorkflowMetricsReindexer _nodeWorkflowMetricsReindexer;

	@Reference(target = "(workflow.metrics.index.entity.name=process)")
	private WorkflowMetricsIndex _processWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=process)")
	private WorkflowMetricsReindexer _processWorkflowMetricsReindexer;

	@Reference(
		target = "(workflow.metrics.index.entity.name=sla-instance-result)"
	)
	private WorkflowMetricsIndex _slaInstanceResultWorkflowMetricsIndex;

	@Reference(
		target = "(workflow.metrics.index.entity.name=sla-instance-result)"
	)
	private WorkflowMetricsReindexer _slaInstanceResultWorkflowMetricsReindexer;

	@Reference(target = "(workflow.metrics.index.entity.name=sla-task-result)")
	private WorkflowMetricsIndex _slaTaskResultWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=sla-task-result)")
	private WorkflowMetricsReindexer _slaTaskResultWorkflowMetricsReindexer;

	@Reference(target = "(workflow.metrics.index.entity.name=task)")
	private WorkflowMetricsIndex _taskWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=task)")
	private WorkflowMetricsReindexer _taskWorkflowMetricsReindexer;

	@Reference(target = "(workflow.metrics.index.entity.name=transition)")
	private WorkflowMetricsIndex _transitionWorkflowMetricsIndex;

	@Reference(target = "(workflow.metrics.index.entity.name=transition)")
	private WorkflowMetricsReindexer _transitionWorkflowMetricsReindexer;

}