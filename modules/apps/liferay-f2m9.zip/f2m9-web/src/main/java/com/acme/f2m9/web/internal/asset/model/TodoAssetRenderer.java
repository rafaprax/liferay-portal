package com.acme.f2m9.web.internal.asset.model;

import com.acme.f2m9.model.Todo;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

public class TodoAssetRenderer extends BaseJSPAssetRenderer<Todo> {

	public TodoAssetRenderer(Todo todo) {
		_todo = todo;
	}

	@Override
	public Todo getAssetObject() {
		return _todo;
	}

	@Override
	public String getClassName() {
		return Todo.class.getName();
	}

	@Override
	public long getClassPK() {
		return _todo.getTodoId();
	}

	@Override
	public long getGroupId() {
		return _todo.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return null;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return "Todo application";
	}

	@Override
	public String getTitle(Locale locale) {
		return _todo.getName();
	}

	//    @Override
	//    public int getStatus() {
	//        return _todo.getStatus();
	//    }

	@Override
	public long getUserId() {
		return _todo.getUserId();
	}

	@Override
	public String getUserName() {
		return _todo.getUserName();
	}

	@Override
	public String getUuid() {
		return _todo.getUuid();
	}

	private Todo _todo;

}