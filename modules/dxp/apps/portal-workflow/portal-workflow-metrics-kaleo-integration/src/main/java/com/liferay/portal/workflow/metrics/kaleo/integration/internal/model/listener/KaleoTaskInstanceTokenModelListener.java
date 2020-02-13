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

package com.liferay.portal.workflow.metrics.kaleo.integration.internal.model.listener;

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.metrics.index.TaskWorkflowMetricsIndexer;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = ModelListener.class)
public class KaleoTaskInstanceTokenModelListener
	extends BaseModelListener<KaleoTaskInstanceToken> {
	
	public Document createDocument(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {


		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			kaleoTaskAssignmentInstanceLocalService.
				fetchFirstKaleoTaskAssignmentInstance(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
					User.class.getName(), null);

		if (kaleoTaskAssignmentInstance != null) {
			document.addKeyword(
				"assigneeId", kaleoTaskAssignmentInstance.getAssigneeClassPK());
		}

		document.addKeyword("className", kaleoTaskInstanceToken.getClassName());
		document.addKeyword("classPK", kaleoTaskInstanceToken.getClassPK());
		document.addKeyword("companyId", kaleoTaskInstanceToken.getCompanyId());
		document.addKeyword("completed", kaleoTaskInstanceToken.isCompleted());

		Date completionDate = kaleoTaskInstanceToken.getCompletionDate();

		if (kaleoTaskInstanceToken.isCompleted()) {
			document.addDateSortable("completionDate", completionDate);
			document.addKeyword(
				"completionUserId",
				kaleoTaskInstanceToken.getCompletionUserId());
		}

		Date createDate = kaleoTaskInstanceToken.getCreateDate();

		document.addDateSortable("createDate", createDate);

		document.addKeyword("deleted", false);

		if (kaleoTaskInstanceToken.isCompleted()) {
			Duration duration = Duration.between(
				createDate.toInstant(), completionDate.toInstant());

			document.addNumber("duration", duration.toMillis());
		}

		KaleoInstance kaleoInstance =
			kaleoInstanceLocalService.fetchKaleoInstance(
				kaleoTaskInstanceToken.getKaleoInstanceId());

		if (kaleoInstance != null) {
			document.addKeyword(
				"instanceCompleted", kaleoInstance.isCompleted());
		}

		document.addKeyword(
			"instanceId", kaleoTaskInstanceToken.getKaleoInstanceId());
		document.addDateSortable(
			"modifiedDate", kaleoTaskInstanceToken.getModifiedDate());

		KaleoDefinition kaleoDefinition = getKaleoDefinition(
			kaleoTaskInstanceToken.getKaleoDefinitionVersionId());

		if (kaleoDefinition != null) {
			document.addKeyword(
				"processId", kaleoDefinition.getKaleoDefinitionId());
		}

		document.addKeyword("taskId", kaleoTaskInstanceToken.getKaleoTaskId());
		document.addKeyword(
			"taskName", kaleoTaskInstanceToken.getKaleoTaskName());
		document.addKeyword(
			"tokenId", kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		document.addKeyword("userId", kaleoTaskInstanceToken.getUserId());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(
				kaleoTaskInstanceToken.getKaleoDefinitionVersionId());

		if (kaleoDefinitionVersion != null) {
			document.addKeyword("version", kaleoDefinitionVersion.getVersion());
		}

		return document;
	}


	@Override
	public void onAfterCreate(KaleoTaskInstanceToken kaleoTaskInstanceToken) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoTaskInstanceToken.getKaleoDefinitionVersionId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(kaleoDefinitionVersion.getCompanyId());

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinitionVersion.getName(), serviceContext);

		_taskWorkflowMetricsIndexer.add(
			kaleoTaskInstanceToken.getCompanyId(),
			kaleoTaskInstanceToken.getKaleoTaskId(),
			kaleoTaskInstanceToken.getClassName(),
			kaleoTaskInstanceToken.getClassPK(),
			kaleoTaskInstanceToken.getCreateDate(),
			kaleoTaskInstanceToken.getModifiedDate(),
			kaleoTaskInstanceToken.getKaleoInstanceId(),
			kaleoTaskInstanceToken.getKaleoTaskName(),
			kaleoDefinition.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getVersion(),
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
			kaleoTaskInstanceToken.getUserId());
	}

	@Override
	public void onAfterRemove(KaleoTaskInstanceToken kaleoTaskInstanceToken) {
		_workflowMetricsPortalExecutor.execute(
			() -> _tokenWorkflowMetricsIndexer.deleteDocument(
				_tokenWorkflowMetricsIndexer.createDocument(
					kaleoTaskInstanceToken)));
	}

	@Override
	public void onBeforeUpdate(KaleoTaskInstanceToken kaleoTaskInstanceToken) {
		KaleoTaskInstanceToken currentKaleoTaskInstanceToken =
			_kaleoTaskInstanceTokenLocalService.fetchKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoInstanceTokenId());

		if (Objects.isNull(currentKaleoTaskInstanceToken.getCompletionDate()) &&
			!Objects.isNull(kaleoTaskInstanceToken.getCompletionDate())) {

		}
		else {
			KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
				_kaleoTaskAssignmentInstanceLocalService.
					fetchFirstKaleoTaskAssignmentInstance(
						kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
						User.class.getName(), null);

			Long assigneeId = null;

			if (kaleoTaskAssignmentInstance != null) {
				assigneeId = kaleoTaskAssignmentInstance.getAssigneeClassPK();
			}

			_taskWorkflowMetricsIndexer.update(
				kaleoTaskInstanceToken.getCompanyId(),
				kaleoTaskInstanceToken.getKaleoTaskId(), assigneeId,
				className, classPK, completed, modifiedDate, description, duration, instanceId, instanceCompleted, name, processId, processVersion, tokenId, userId)
		}
	}

	@Reference
	private TaskWorkflowMetricsIndexer _taskWorkflowMetricsIndexer;

	@Reference
	private KaleoTaskAssignmentInstanceLocalService _kaleoTaskAssignmentInstanceLocalService;

	@Reference
	private KaleoTaskInstanceTokenLocalService _kaleoTaskInstanceTokenLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService _kaleoDefinitionVersionLocalService;

}