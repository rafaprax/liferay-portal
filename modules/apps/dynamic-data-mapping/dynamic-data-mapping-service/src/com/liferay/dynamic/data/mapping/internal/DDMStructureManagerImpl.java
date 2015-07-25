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

package com.liferay.dynamic.data.mapping.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.StagedModel;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.dynamicdatamapping.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.DDMStructureManager;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.portlet.dynamicdatamapping.RequiredStructureException;
import com.liferay.portlet.dynamicdatamapping.StructureDefinitionException;
import com.liferay.portlet.dynamicdatamapping.StructureDuplicateElementException;
import com.liferay.portlet.dynamicdatamapping.StructureNameException;
import com.liferay.portlet.dynamicdatamapping.StructureFieldException;
import com.liferay.portlet.dynamicdatamapping.StructureDuplicateStructureKeyException;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portlet.dynamicdatamapping.storage.DDMFormValues;
import com.liferay.portlet.dynamicdatamapping.storage.Field;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMIndexerUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.dynamic.data.mapping.util.comparator.StructureIdComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureStructureKeyComparator;
import com.liferay.portlet.exportimport.lar.PortletDataContext;
import com.liferay.portlet.exportimport.lar.PortletDataException;
import com.liferay.portlet.exportimport.lar.StagedModelDataHandlerUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverterUtil;
import com.liferay.dynamic.data.mapping.storage.FieldRenderer;
import com.liferay.dynamic.data.mapping.storage.FieldRendererFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(immediate = true)
public class DDMStructureManagerImpl implements DDMStructureManager {

	@Override
	public void addAttributes(
			long structureId, Document document, DDMFormValues ddmFormValues)
		throws PortalException {

		try{
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getStructure(structureId);
	
			DDMIndexerUtil.addAttributes(document, ddmStructure, ddmFormValues);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, String parentStructureKey,
			long classNameId, String structureKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, DDMForm ddmForm,
			DDMFormLayout ddmFormLayout, String storageType, int type,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.addStructure(
					userId, groupId, parentStructureKey, classNameId, structureKey,
					nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType,
					type, serviceContext);
	
			return new DDMStructureImpl(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDefinitionException e){
			throw new StructureDefinitionException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException e){
			throw new StructureDuplicateElementException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureNameException e){
			throw new StructureNameException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException e){
			throw new StructureDuplicateStructureKeyException(e.getMessage(), e.getCause());
		}
	}
	
	@Override
	public Fields convertDDMFormValues(
		long structureId, DDMFormValues ddmFormValues) 
		throws PortalException {
		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.getStructure(structureId);
		return DDMFormValuesToFieldsConverterUtil.convert(
				ddmStructure, ddmFormValues);
	}

	@Override
	public void deleteStructure(long structureId) throws PortalException {
		try{
			_ddmStructureLocalService.deleteStructure(structureId);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.RequiredStructureException e){
			throw new RequiredStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public <T extends StagedModel> Element exportDDMStructureStagedModel(
			PortletDataContext portletDataContext, T referrerStagedModel,
			long structureId, String referenceType)
		throws PortletDataException {

		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure
				ddmStructure = _ddmStructureLocalService.getStructure(
					structureId);

			return StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, referrerStagedModel, ddmStructure,
				referenceType);
		}
		catch (PortalException pe) {
			throw new PortletDataException(pe);
		}
	}

	@Override
	public String extractAttributes(
			long structureId, DDMFormValues ddmFormValues, Locale locale)
		throws PortalException {

		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getStructure(structureId);
	
			return DDMIndexerUtil.extractAttributes(
				ddmStructure, ddmFormValues, locale);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMStructure fetchStructure(long structureId) {
		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.fetchDDMStructure(structureId);

		if (ddmStructure == null) {
			return null;
		}

		return new DDMStructureImpl(ddmStructure);
	}

	@Override
	public DDMStructure fetchStructure(
		long groupId, long classNameId, String structureKey) {

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.fetchStructure(
				groupId, classNameId, structureKey);

		if (ddmStructure == null) {
			return null;
		}

		return new DDMStructureImpl(ddmStructure);
	}

	@Override
	public DDMStructure fetchStructureByUuidAndGroupId(
		String uuid, long groupId) {

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
				uuid, groupId);

		if (ddmStructure == null) {
			return null;
		}

		return new DDMStructureImpl(ddmStructure);
	}

	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId) {

		List<DDMStructure> ddmStructures = new ArrayList<>();

		List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			structures = _ddmStructureLocalService.getClassStructures(
				companyId, classNameId);

		for (com.liferay.dynamic.data.mapping.model.DDMStructure
				structure : structures) {

			ddmStructures.add(new DDMStructureImpl(structure));
		}

		return ddmStructures;
	}

	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId, int structureComparator) {

		List<DDMStructure> ddmStructures = new ArrayList<>();

		for (com.liferay.dynamic.data.mapping.model.DDMStructure
				ddmStructure :
					_ddmStructureLocalService.getClassStructures(
						companyId, classNameId,
						getStructureOrderByComparator(structureComparator))) {

			ddmStructures.add(new DDMStructureImpl(ddmStructure));
		}

		return ddmStructures;
	}

	@Override
	public List<DDMStructure> getClassStructures(
		long companyId, long classNameId, int start, int end) {

		List<DDMStructure> ddmStructures = new ArrayList<>();

		for (com.liferay.dynamic.data.mapping.model.DDMStructure
				ddmStructure :
					_ddmStructureLocalService.getClassStructures(
						companyId, classNameId, start, end)) {

			ddmStructures.add(new DDMStructureImpl(ddmStructure));
		}

		return ddmStructures;
	}
	
	@Override
	public DDMForm getDDMForm(PortletRequest portletRequest) 
		throws PortalException {

		try{
			return DDMUtil.getDDMForm(portletRequest);
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDefinitionException e){
			throw new StructureDefinitionException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMForm getDDMForm(long classNameId, long classPk) 
			throws PortalException {
		return DDMUtil.getDDMForm(classNameId, classPk);
	}

	@Override
	public JSONArray getDDMFormFieldsJSONArray(long structureId, String script)
		throws PortalException {

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureLocalService.fetchDDMStructure(structureId);

		return DDMUtil.getDDMFormFieldsJSONArray(ddmStructure, script);
	}

	@Override
	public Class<?> getDDMStructureModelClass() {
		return com.liferay.dynamic.data.mapping.model.DDMStructure.class;
	}

	@Override
	public DDMFormLayout getDefaultDDMFormLayout(DDMForm ddmForm) {
		return DDMUtil.getDefaultDDMFormLayout(ddmForm);
	}
	
	@Override
	public String getFieldRenderedValue(
		Field field, Locale locale, int valueIndex)
		throws PortalException {
		
		DDMStructure ddmStructure = field.getDDMStructure();

		String dataType = null;

		if (ddmStructure != null) {
			dataType = field.getDataType();
		}
		
		FieldRenderer fieldRenderer = 
			FieldRendererFactory.getFieldRenderer(dataType);
		
		if(valueIndex >= 0) {
			return fieldRenderer.render(field, locale, valueIndex);
		} 
		else {
			return fieldRenderer.render(field, locale);
		}
	}

	@Override
	public Serializable getIndexedFieldValue(
			Serializable fieldValue, String fieldType)
		throws Exception {
		try {
			return DDMUtil.getIndexedFieldValue(fieldValue, fieldType);
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureFieldException e){
			throw new StructureFieldException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMStructure getStructure(long structureId) throws PortalException {
		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getStructure(structureId);
	
			return new DDMStructureImpl(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey)
		throws PortalException {

		try{
			com.liferay.dynamic.data.mapping.model.DDMStructure structure =
				_ddmStructureLocalService.getStructure(
					groupId, classNameId, structureKey);
	
			return new DDMStructureImpl(structure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public DDMStructure getStructureByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		try{
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructureByUuidAndGroupId(
					uuid, groupId);
	
			return new DDMStructureImpl(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<DDMStructure> getStructures(long[] groupIds, long classNameId) {
		List<DDMStructure> ddmStructures = new ArrayList<>();

		for (com.liferay.dynamic.data.mapping.model.DDMStructure
				ddmStructure :
					_ddmStructureLocalService.getStructures(
						groupIds, classNameId)) {

			ddmStructures.add(new DDMStructureImpl(ddmStructure));
		}

		return ddmStructures;
	}

	@Override
	public int getStructureStorageLinksCount(long structureId) {
		return _ddmStorageLinkLocalService.getStructureStorageLinksCount(
					structureId);
	}

	@Override
	public DDMStructure updateStructure(
			long userId, long structureId, long parentStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.updateStructure(
					userId, structureId, parentStructureId, nameMap, descriptionMap,
					ddmForm, ddmFormLayout, serviceContext);
	
			return new DDMStructureImpl(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDefinitionException e){
			throw new StructureDefinitionException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException e){
			throw new StructureDuplicateElementException(e.getMessage(), e.getCause());
		}
		catch(com.liferay.dynamic.data.mapping.exception.StructureNameException e){
			throw new StructureNameException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void updateStructureDefinition(long structureId, String definition)
		throws PortalException {

		try {
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(structureId);
	
			ddmStructure.setDefinition(definition);
	
			_ddmStructureLocalService.updateDDMStructure(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void updateStructureKey(long structureId, String structureKey)
		throws PortalException {

		try{
			com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(structureId);
	
			ddmStructure.setStructureKey(structureKey);
	
			_ddmStructureLocalService.updateDDMStructure(ddmStructure);
		}
		catch(com.liferay.dynamic.data.mapping.exception.NoSuchStructureException e){
			throw new NoSuchStructureException(e.getMessage(), e.getCause());
		}
	}

	protected OrderByComparator
		<com.liferay.dynamic.data.mapping.model.DDMStructure>
		getStructureOrderByComparator(
			int structureComparator) {

		if (structureComparator ==
				DDMStructureManager.STRUCTURE_COMPARATOR_STRUCTURE_KEY) {

			return new StructureStructureKeyComparator();
		}

		return new StructureIdComparator();
	}

	@Reference
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Reference
	protected void setDDMStorageLinkLocalService(
			DDMStorageLinkLocalService ddmStorageLinkLocalService) {
		
		_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
	}

	private DDMStructureLocalService _ddmStructureLocalService;
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;


}
