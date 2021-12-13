package com.acme.f2m9.web.internal.portlet;

import com.acme.f2m9.model.Todo;
import com.acme.f2m9.service.TodoLocalService;

import com.acme.f2m9.service.TodoLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"javax.portlet.display-name=F2M9 Portlet",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class F2M9Portlet extends MVCPortlet {

	public void addTodo(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String item = ParamUtil.getString(actionRequest, "item");

		System.out.println("item" + item);

		User user = _portal.getUser(actionRequest);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Todo.class.getName(), actionRequest);

		_todoLocalService.addTodo(
			user.getCompanyId(), themeDisplay.getSiteGroupId(),
			user.getUserId(), user.getFullName(), item, serviceContext);
	}

	public Long getInstanceTracker(Todo todo) {
		WorkflowInstanceLink workflowDefinitionLink = _workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(todo.getCompanyId(), todo.getGroupId(), Todo.class.getName(), todo.getPrimaryKey());

		if(workflowDefinitionLink != null) {
			return workflowDefinitionLink.getWorkflowInstanceId();
		}
		return 0L;
	}



	@Reference
	private Portal _portal;

	@Reference
	private TodoLocalService _todoLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}