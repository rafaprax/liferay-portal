package com.acme.f2m9.web.internal.asset.model;

import com.acme.f2m9.model.Todo;
import com.acme.f2m9.service.TodoLocalService;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = AssetRendererFactory.class)
public class TodoAssetRendererFactory extends BaseAssetRendererFactory<Todo> {

	public static final String TYPE = "todo";

	public TodoAssetRendererFactory() {
		setClassName(Todo.class.getName());
		setLinkable(true);
		setPortletId("com.acme.f2m9.todo");
		setSearchable(true);
	}

	@Override
	public AssetRenderer<Todo> getAssetRenderer(long classPK, int type)
		throws PortalException {

		Todo todo = _todoLocalService.getTodo(classPK);

		TodoAssetRenderer todoAssetRenderer = new TodoAssetRenderer(todo);

		todoAssetRenderer.setAssetRendererType(type);
		todoAssetRenderer.setServletContext(_servletContext);

		return todoAssetRenderer;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getTypeName(Locale locale) {
		return "todo";
	}

	@Reference(target = "(osgi.web.symbolicname=com.acme.f2m9.web)")
	private ServletContext _servletContext;

	@Reference
	private TodoLocalService _todoLocalService;

}