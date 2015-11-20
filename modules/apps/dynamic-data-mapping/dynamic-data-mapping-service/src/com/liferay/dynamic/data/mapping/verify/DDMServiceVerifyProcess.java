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

package com.liferay.dynamic.data.mapping.verify;

import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializerUtil;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesValidator;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidator;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidator;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.verify.VerifyProcess;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @author Rafael Praxedes
 */
@Component(immediate = true, service = DDMServiceVerifyProcess.class)
public class DDMServiceVerifyProcess extends VerifyProcess {

	protected void checkDDMStructureLinks() throws PortalException {
		List<DDMStructureLink> ddmStructureLinks =
			_ddmStructureLinkLocalService.findAll();

		for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchDDMStructure(
					ddmStructureLink.getStructureId());

			if (ddmStructure == null) {
				_ddmStructureLinkLocalService.deleteStructureLink(
					ddmStructureLink);
			}
		}
	}

	protected void checkDDMTemplateLinks() throws PortalException {
		List<DDMTemplateLink> ddmTemplateLinks =
			_ddmTemplateLinkLocalService.findAll();

		for (DDMTemplateLink ddmTemplateLink : ddmTemplateLinks) {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchDDMTemplate(
				ddmTemplateLink.getTemplateId());

			if (ddmTemplate == null) {
				_ddmTemplateLinkLocalService.deleteTemplateLink(
					ddmTemplateLink);
			}
		}
	}

	@Activate
	@Override
	protected void doVerify() throws Exception {
		checkDDMStructureLinks();
		checkDDMTemplateLinks();
		verifyStructures();
		verifyContents();
	}

	protected DDMFormValues getDDMFormValues(DDMContent ddmContent)
		throws PortalException {

		DDMStorageLink ddmStorageLink =
				DDMStorageLinkLocalServiceUtil.getClassStorageLink(
					ddmContent.getPrimaryKey());

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			ddmStorageLink.getStructureId());

		return DDMFormValuesJSONDeserializerUtil.deserialize(
			ddmStructure.getDDMForm(), ddmContent.getData());
	}

	@Reference
	protected void setDDMContentLocalService(
		DDMContentLocalService ddmContentLocalService) {

		_ddmContentLocalService = ddmContentLocalService;
	}

	@Reference
	protected void setDDMFormLayoutValidator(
		DDMFormLayoutValidator ddmFormLayoutValidator) {

		_ddmFormLayoutValidator = ddmFormLayoutValidator;
	}

	@Reference
	protected void setDDMFormValidator(DDMFormValidator ddmFormValidator) {
		_ddmFormValidator = ddmFormValidator;
	}

	@Reference
	protected void setDDMFormValuesValidator(
		DDMFormValuesValidator ddmFormValuesValidator) {

		_ddmFormValuesValidator = ddmFormValuesValidator;
	}

	@Reference
	protected void setDDMStructureLinkLocalService(
		DDMStructureLinkLocalService ddmStructureLinkLocalService) {

		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
	}

	@Reference
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Reference
	protected void setDDMStructureVersionLocalService(
		DDMStructureVersionLocalService ddmStructureVersionLocalService) {
	}

	@Reference
	protected void setDDMTemplateLinkLocalService(
		DDMTemplateLinkLocalService ddmTemplateLinkLocalService) {

		_ddmTemplateLinkLocalService = ddmTemplateLinkLocalService;
	}

	@Reference
	protected void setDDMTemplateLocalService(
		DDMTemplateLocalService ddmTemplateLocalService) {

		_ddmTemplateLocalService = ddmTemplateLocalService;
	}

	protected void verifyContent(DDMContent content) {
		try {
			DDMFormValues ddmFormValues = getDDMFormValues(content);

			_ddmFormValuesValidator.validate(ddmFormValues);
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	protected void verifyContents() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_ddmContentLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod() {

				@Override
				public void performAction(Object object)
					throws PortalException {

					DDMContent content = (DDMContent)object;

					verifyContent(content);
				}

			});

		actionableDynamicQuery.performActions();
	}

	protected void verifyDDMForm(DDMForm ddmForm) throws PortalException {
		_ddmFormValidator.validate(ddmForm);
	}

	protected void verifyDDMFormLayout(DDMFormLayout ddmFormLayout)
		throws PortalException {

		_ddmFormLayoutValidator.validate(ddmFormLayout);
	}

	protected void verifyStructure(DDMStructure structure) {
		try {
			verifyDDMForm(structure.getDDMForm());
			verifyDDMFormLayout(structure.getDDMFormLayout());
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	protected void verifyStructures() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_ddmStructureLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod() {

				@Override
				public void performAction(Object object)
					throws PortalException {

					DDMStructure structure = (DDMStructure)object;

					verifyStructure(structure);
				}

			});

		actionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMServiceVerifyProcess.class);

	private DDMContentLocalService _ddmContentLocalService;
	private DDMFormLayoutValidator _ddmFormLayoutValidator;
	private DDMFormValidator _ddmFormValidator;
	private DDMFormValuesValidator _ddmFormValuesValidator;
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private DDMStructureLocalService _ddmStructureLocalService;
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;
	private DDMTemplateLocalService _ddmTemplateLocalService;

}