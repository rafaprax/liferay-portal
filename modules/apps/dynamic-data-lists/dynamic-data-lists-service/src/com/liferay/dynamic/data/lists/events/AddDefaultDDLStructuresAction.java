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

package com.liferay.dynamic.data.lists.events;

import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.GroupLocalService;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.util.PortalUtil;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.context.ApplicationContext;

/**
 * @author Michael C. Han
 */
@Component(immediate = true)
public class AddDefaultDDLStructuresAction extends SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		try {
			doRun(GetterUtil.getLong(ids[0]));
		}
		catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Activate
	protected void activate() throws ActionException {

		setUpDefaultDDMStructureHelper();
		Long companyId = CompanyThreadLocal.getCompanyId();

		try {
			List<Company> companies = _companyLocalService.getCompanies();

			for (Company company : companies) {
				CompanyThreadLocal.setCompanyId(company.getCompanyId());

				run(new String[] {String.valueOf(company.getCompanyId())});
			}
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyId);
		}
	}

	protected void doRun(long companyId) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		serviceContext.setScopeGroupId(group.getGroupId());

		long defaultUserId = _userLocalService.getDefaultUserId(companyId);

		serviceContext.setUserId(defaultUserId);

		_ddmDefaultStructureHelper.addDDMStructures(
			defaultUserId, group.getGroupId(),
			PortalUtil.getClassNameId(DDLRecordSet.class),
			AddDefaultDDLStructuresAction.class.getClassLoader(),
			"com/liferay/dynamic/data/lists/events/dependencies" +
				"/default-dynamic-data-lists-structures.xml",
			serviceContext);
	}

	@Reference(
		target =
			"(org.springframework.context.service.name=" +
				"com.liferay.dynamic.data.lists.service)",
		unbind = "-"
	)
	protected void setApplicationContext(
		ApplicationContext applicationContext) {
	}

	@Reference
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Reference
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}
	
	@Reference
	protected void setDDMFormJSONDeserializer(
		DDMFormJSONDeserializer ddmFormJSONDeserializer) {

		_ddmFormJSONDeserializer = ddmFormJSONDeserializer;
	}

	@Reference
	protected void setDDMFormXSDDeserializer(
			DDMFormXSDDeserializer ddmFormXSDDeserializer) {
		
		_ddmFormXSDDeserializer = ddmFormXSDDeserializer;
	}
	
	@Reference
	protected void setDDMFormLayoutJSONDeserializer(
			DDMFormLayoutJSONDeserializer ddmFormLayoutJSONDeserializer) {
		
		_ddmFormLayoutJSONDeserializer = ddmFormLayoutJSONDeserializer;
	}
	
	@Reference
	protected void setDDMStructureLocalService(
			DDMStructureLocalService ddmStructureLocalService) {
		
		_ddmStructureLocalService = ddmStructureLocalService;
	}
	
	@Reference
	protected void setDDMTemplateLocalService(
			DDMTemplateLocalService ddmTemplateLocalService) {
		
		_ddmTemplateLocalService = ddmTemplateLocalService;
	}
	
	private void setUpDefaultDDMStructureHelper(){
		_ddmDefaultStructureHelper = new DefaultDDMStructureHelper(
			_ddmFormJSONDeserializer, _ddmFormLayoutJSONDeserializer, 
			_ddmFormXSDDeserializer, _ddmStructureLocalService,
			_ddmTemplateLocalService);
	}

	private CompanyLocalService _companyLocalService;
	private DDMFormJSONDeserializer _ddmFormJSONDeserializer;
	private DDMFormLayoutJSONDeserializer _ddmFormLayoutJSONDeserializer;
	private DDMFormXSDDeserializer _ddmFormXSDDeserializer;
	private DDMStructureLocalService _ddmStructureLocalService;
	private DDMTemplateLocalService _ddmTemplateLocalService;
	private DefaultDDMStructureHelper _ddmDefaultStructureHelper;
	private GroupLocalService _groupLocalService;
	private UserLocalService _userLocalService;

}