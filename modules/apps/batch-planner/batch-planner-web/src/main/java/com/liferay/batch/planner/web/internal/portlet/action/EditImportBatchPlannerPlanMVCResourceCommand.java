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

package com.liferay.batch.planner.web.internal.portlet.action;

import com.liferay.batch.planner.batch.engine.broker.BatchEngineBroker;
import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerLog;
import com.liferay.batch.planner.model.BatchPlannerMapping;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.service.BatchPlannerMappingService;
import com.liferay.batch.planner.service.BatchPlannerPlanService;
import com.liferay.batch.planner.service.BatchPlannerPolicyService;
import com.liferay.batch.planner.service.persistence.BatchPlannerMappingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Beslic
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
		"mvc.command.name=/batch_planner/edit_import_batch_planner_plan"
	},
	service = MVCResourceCommand.class
)
public class EditImportBatchPlannerPlanMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

		if (cmd.equals(Constants.IMPORT)) {
			_submitBatchPlannerPlan(resourceRequest, resourceResponse);
		}
		else if (cmd.equals(Constants.SAVE)) {
			_addBatchPlannerPlan(resourceRequest, resourceResponse);
		}
	}

	private void _addBatchPlannerPlan(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			BatchPlannerPlan batchPlannerPlan = _addBatchPlannerPlan(
				resourceRequest, StringPool.BLANK);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"batchPlannerPlanId",
					batchPlannerPlan.getBatchPlannerPlanId()
				).put(
					"name", batchPlannerPlan.getName()
				).put(
					"success", StringPool.TRUE
				));
		}
		catch (Exception exception) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error", exception.getMessage()
				).put(
					"success", StringPool.FALSE
				));
		}
	}

	private BatchPlannerPlan _addBatchPlannerPlan(
			ResourceRequest resourceRequest, String importFileURI)
		throws Exception {

		boolean export = ParamUtil.getBoolean(resourceRequest, "export");
		String externalType = ParamUtil.getString(
			resourceRequest, "externalType", "CSV");
		String internalClassName = ParamUtil.getString(
			resourceRequest, "internalClassName");
		String name = ParamUtil.getString(resourceRequest, "name");
		String taskItemDelegateName = ParamUtil.getString(
			resourceRequest, "taskItemDelegateName");
		boolean template = ParamUtil.getBoolean(resourceRequest, "template");

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.addBatchPlannerPlan(
				export, externalType, importFileURI, internalClassName, name,
				taskItemDelegateName, template);

		_batchPlannerPolicyService.addBatchPlannerPolicy(
			batchPlannerPlan.getBatchPlannerPlanId(), "containsHeaders",
			_getCheckboxValue(resourceRequest, "containsHeaders"));
		_batchPlannerPolicyService.addBatchPlannerPolicy(
			batchPlannerPlan.getBatchPlannerPlanId(), "headlessEndpoint",
			ParamUtil.getString(resourceRequest, "headlessEndpoint"));

		List<BatchPlannerMapping> batchPlannerMappings =
			_getBatchPlannerMappings(resourceRequest);

		for (BatchPlannerMapping batchPlannerMapping : batchPlannerMappings) {
			_batchPlannerMappingService.addBatchPlannerMapping(
				batchPlannerPlan.getBatchPlannerPlanId(),
				batchPlannerMapping.getExternalFieldName(), "String",
				batchPlannerMapping.getInternalFieldName(), "String",
				StringPool.BLANK);
		}

		return batchPlannerPlan;
	}

	private List<BatchPlannerMapping> _getBatchPlannerMappings(
		ResourceRequest resourceRequest) {

		List<BatchPlannerMapping> batchPlannerMappings = new ArrayList<>();

		Enumeration<String> enumeration = resourceRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();

			if (!parameterName.startsWith("externalFieldName_") ||
				Validator.isNull(
					ParamUtil.getString(resourceRequest, parameterName))) {

				continue;
			}

			String suffix = StringUtil.extractLast(
				parameterName, StringPool.UNDERLINE);

			if (Validator.isNull(
					ParamUtil.getString(
						resourceRequest, "internalFieldName_" + suffix))) {

				continue;
			}

			BatchPlannerMapping batchPlannerMapping =
				BatchPlannerMappingUtil.create(0);

			batchPlannerMapping.setExternalFieldName(
				ParamUtil.getString(resourceRequest, parameterName));
			batchPlannerMapping.setInternalFieldName(
				ParamUtil.getString(
					resourceRequest, "internalFieldName_" + suffix));

			batchPlannerMappings.add(batchPlannerMapping);
		}

		return batchPlannerMappings;
	}

	private String _getCheckboxValue(
		ResourceRequest resourceRequest, String name) {

		String value = resourceRequest.getParameter(name);

		if (value == null) {
			return Boolean.FALSE.toString();
		}

		return Boolean.TRUE.toString();
	}

	private void _submitBatchPlannerPlan(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String externalType = ParamUtil.getString(
			resourceRequest, "externalType", "CSV");

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(resourceRequest);

		File importFile = _toBatchPlannerFile(
			externalType, uploadPortletRequest.getFileAsStream("importFile"));

		try {
			URI importFileURI = importFile.toURI();

			BatchPlannerPlan batchPlannerPlan = _addBatchPlannerPlan(
				resourceRequest, importFileURI.toString());

			_batchEngineBroker.submit(batchPlannerPlan.getBatchPlannerPlanId());

			BatchPlannerLog batchPlannerLog =
				batchPlannerPlan.getBatchPlannerLog();

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"importTaskId",
					batchPlannerLog.getBatchEngineImportTaskERC()));
		}
		finally {
			FileUtil.delete(importFile);
		}
	}

	private File _toBatchPlannerFile(
			String externalType, InputStream inputStream)
		throws Exception {

		UUID uuid = UUID.randomUUID();

		File file = FileUtil.createTempFile(uuid.toString(), externalType);

		try {
			Files.copy(inputStream, file.toPath());

			return file;
		}
		catch (IOException ioException) {
			if (file.exists()) {
				file.delete();
			}

			throw ioException;
		}
	}

	@Reference
	private BatchEngineBroker _batchEngineBroker;

	@Reference
	private BatchPlannerMappingService _batchPlannerMappingService;

	@Reference
	private BatchPlannerPlanService _batchPlannerPlanService;

	@Reference
	private BatchPlannerPolicyService _batchPlannerPolicyService;

	@Reference
	private Portal _portal;

}