/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.acme.f2m9.service.impl;

import com.acme.f2m9.model.Todo;
import com.acme.f2m9.service.base.TodoLocalServiceBaseImpl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.acme.f2m9.model.Todo",
	service = AopService.class
)
public class TodoLocalServiceImpl extends TodoLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	public Todo addTodo(
			long companyId, long groupId, long userId, String userName,
			String item, ServiceContext serviceContext)
		throws PortalException {

		Todo todo = todoPersistence.create(counterLocalService.increment());

		Date now = new Date();

		todo.setCompanyId(companyId);
		todo.setCreateDate(serviceContext.getCreateDate(now));
		todo.setGroupId(groupId);
		todo.setModifiedDate(serviceContext.getModifiedDate(now));
		todo.setName(item);
		todo.setUserId(userId);
		todo.setUserName(userName);

		todo.setStatus(WorkflowConstants.STATUS_DRAFT);
		todo.setStatusByUserId(userId);
		todo.setStatusByUserName(userName);
		todo.setStatusDate(serviceContext.getModifiedDate(null));

		todo = todoPersistence.update(todo);

		assetEntryLocalService.updateEntry(
			userId, groupId, todo.getCreateDate(), todo.getModifiedDate(),
			Todo.class.getName(), todo.getTodoId(), todo.getUuid(), 0, null,
			null, true, true, null, null, null, null, ContentTypes.TEXT,
			todo.getName(), null, null, null, null, 0, 0, 1.0);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			todo.getCompanyId(), todo.getGroupId(), todo.getUserId(),
			Todo.class.getName(), todo.getPrimaryKey(), todo, serviceContext);

		return todo;
	}

	public Todo updateStatus(
			long userId, Todo todo, int status, ServiceContext serviceContext)
		throws PortalException {

		User user = userLocalService.getUser(userId);

		todo.setStatus(status);
		todo.setStatusByUserId(userId);
		todo.setStatusByUserName(user.getFullName());
		todo.setStatusDate(new Date());

		todo = todoPersistence.update(todo);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			assetEntryLocalService.updateVisible(
				Todo.class.getName(), todo.getTodoId(), true);
		}
		else {
			assetEntryLocalService.updateVisible(
				Todo.class.getName(), todo.getTodoId(), false);
		}

		return todo;
	}

}