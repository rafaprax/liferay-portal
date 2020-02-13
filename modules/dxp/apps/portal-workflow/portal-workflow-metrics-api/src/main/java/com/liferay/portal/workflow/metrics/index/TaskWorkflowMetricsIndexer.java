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

package com.liferay.portal.workflow.metrics.index;

import com.liferay.portal.search.document.Document;

import java.util.Date;

/**
 * @author Rafael Praxedes
 */
public interface TaskWorkflowMetricsIndexer {

	public Document add(
		long companyId, long taskId, String className, long classPK,
		Date createDate, Date modifiedDate, long instanceId,
		String name, long processId, String processVersion, long tokenId,
		long userId);

	public Document add(
		long companyId, long taskId, long assigneeId, String className,
		long classPK, boolean completed, long completionUserId,
		Date completionDate, Date createDate, Date modifiedDate,
		long instanceId,
		boolean instanceComplete, String name,
		long processId, String processVersion, long tokenId, long userId);

	public Document complete(
		long companyId, long taskId, long completionUserId,
		Date completionDate, Date modifiedDate, long duration, long processId,
		String processVersion, long tokenId, long userId);

	public Document update(
		long companyId, long taskId, Long assigneeId,Date modifiedDate,
		long processId, String processVersion, long tokenId, long userId);

	public void delete(
		long companyId, long instanceId, long processId, String processVersion,
		long taskId, long tokenId);

}