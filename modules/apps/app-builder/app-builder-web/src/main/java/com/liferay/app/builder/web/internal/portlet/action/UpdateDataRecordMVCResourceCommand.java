package com.liferay.app.builder.web.internal.portlet.action;

import com.liferay.app.builder.constants.AppBuilderAppConstants;
import com.liferay.app.builder.model.AppBuilderApp;
import com.liferay.app.builder.model.AppBuilderAppVersion;
import com.liferay.app.builder.service.AppBuilderAppDataRecordLinkLocalService;
import com.liferay.app.builder.service.AppBuilderAppVersionLocalService;
import com.liferay.data.engine.rest.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.resource.v2_0.DataRecordResource;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author Nara Andrade
 */
@Component(
	immediate = true,
	property = {
		"app.builder.app.scope=" + AppBuilderAppConstants.SCOPE_STANDARD,
		"mvc.command.name=/app_builder/update_data_record"
	},
	service = MVCResourceCommand.class
)
public class UpdateDataRecordMVCResourceCommand extends BaseAppBuilderMVCResourceCommand<DataRecord> {

	@Override
	protected Optional<DataRecord> doTransactionalCommand(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		DataRecord dataRecord = _updateDataRecord(
			resourceRequest, themeDisplay);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			themeDisplay.getUserId(),
			ResourceActionsUtil.getCompositeModelName(
				AppBuilderApp.class.getName(), DDLRecord.class.getName()),
			dataRecord.getId(),
			_ddlRecordLocalService.getDDLRecord(dataRecord.getId()),
			new ServiceContext(),
			HashMapBuilder.<String, Serializable>put(
				"plid", themeDisplay.getPlid()
			).put(
				"portletId", portletDisplay.getId()
			).build());

		return Optional.of(dataRecord);
	}

	private DataRecord _updateDataRecord(
		ResourceRequest resourceRequest, ThemeDisplay themeDisplay)
		throws Exception {

		DataRecordResource dataRecordResource = DataRecordResource.builder(
		).user(
			themeDisplay.getUser()
		).build();

		return dataRecordResource.patchDataRecord(
			ParamUtil.getLong(resourceRequest, "dataRecordId"),
			DataRecord.toDTO(
				ParamUtil.getString(resourceRequest, "dataRecord")));
	}

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

}