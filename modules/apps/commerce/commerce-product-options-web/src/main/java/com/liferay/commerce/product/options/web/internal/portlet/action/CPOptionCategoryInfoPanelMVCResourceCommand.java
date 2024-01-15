/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"javax.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/cp_option_category_info_panel"
	},
	service = MVCResourceCommand.class
)
public class CPOptionCategoryInfoPanelMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			CPWebKeys.CP_OPTION_CATEGORIES,
			_getCPOptionCategories(resourceRequest));

		include(
			resourceRequest, resourceResponse,
			"/cp_option_category_info_panel.jsp");
	}

	private List<CPOptionCategory> _getCPOptionCategories(
			ResourceRequest resourceRequest)
		throws Exception {

		List<CPOptionCategory> cpOptionCategories = new ArrayList<>();

		long[] cpOptionCategoryIds = ParamUtil.getLongValues(
			resourceRequest, "rowIds");

		for (long cpOptionCategoryId : cpOptionCategoryIds) {
			cpOptionCategories.add(
				_cpOptionCategoryService.getCPOptionCategory(
					cpOptionCategoryId));
		}

		return cpOptionCategories;
	}

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

}