package com.acme.f2m9.workflow;

import com.acme.f2m9.model.Todo;
import com.acme.f2m9.service.TodoLocalService;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = WorkflowHandler.class)
public class TodoWorkflowHandler extends BaseWorkflowHandler<Todo> {

	@Override
	public String getClassName() {
		return Todo.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return _resourceActions.getModelResource(locale, getClassName());
	}

	@Override
	public Todo updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long resourcePrimKey = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		Todo todo = _todoLocalService.getTodo(resourcePrimKey);

		return _todoLocalService.updateStatus(
			userId, todo, status, serviceContext);
	}

	@Reference(unbind = "-")
	private ResourceActions _resourceActions;

	@Reference
	private TodoLocalService _todoLocalService;

}