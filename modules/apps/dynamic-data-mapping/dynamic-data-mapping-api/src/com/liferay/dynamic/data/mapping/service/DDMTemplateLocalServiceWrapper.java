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

package com.liferay.dynamic.data.mapping.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DDMTemplateLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTemplateLocalService
 * @generated
 */
@ProviderType
public class DDMTemplateLocalServiceWrapper implements DDMTemplateLocalService,
	ServiceWrapper<DDMTemplateLocalService> {
	public DDMTemplateLocalServiceWrapper(
		DDMTemplateLocalService ddmTemplateLocalService) {
		_ddmTemplateLocalService = ddmTemplateLocalService;
	}

	/**
	* Adds the d d m template to the database. Also notifies the appropriate model listeners.
	*
	* @param ddmTemplate the d d m template
	* @return the d d m template that was added
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate addDDMTemplate(
		com.liferay.dynamic.data.mapping.model.DDMTemplate ddmTemplate) {
		return _ddmTemplateLocalService.addDDMTemplate(ddmTemplate);
	}

	/**
	* Creates a new d d m template with the primary key. Does not add the d d m template to the database.
	*
	* @param templateId the primary key for the new d d m template
	* @return the new d d m template
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate createDDMTemplate(
		long templateId) {
		return _ddmTemplateLocalService.createDDMTemplate(templateId);
	}

	/**
	* Deletes the d d m template from the database. Also notifies the appropriate model listeners.
	*
	* @param ddmTemplate the d d m template
	* @return the d d m template that was removed
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate deleteDDMTemplate(
		com.liferay.dynamic.data.mapping.model.DDMTemplate ddmTemplate) {
		return _ddmTemplateLocalService.deleteDDMTemplate(ddmTemplate);
	}

	/**
	* Deletes the d d m template with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param templateId the primary key of the d d m template
	* @return the d d m template that was removed
	* @throws PortalException if a d d m template with the primary key could not be found
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate deleteDDMTemplate(
		long templateId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmTemplateLocalService.deleteDDMTemplate(templateId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.model.PersistedModel deletePersistedModel(
		com.liferay.portal.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmTemplateLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmTemplateLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _ddmTemplateLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _ddmTemplateLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _ddmTemplateLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _ddmTemplateLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return _ddmTemplateLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate fetchDDMTemplate(
		long templateId) {
		return _ddmTemplateLocalService.fetchDDMTemplate(templateId);
	}

	/**
	* Returns the d d m template matching the UUID and group.
	*
	* @param uuid the d d m template's UUID
	* @param groupId the primary key of the group
	* @return the matching d d m template, or <code>null</code> if a matching d d m template could not be found
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate fetchDDMTemplateByUuidAndGroupId(
		java.lang.String uuid, long groupId) {
		return _ddmTemplateLocalService.fetchDDMTemplateByUuidAndGroupId(uuid,
			groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _ddmTemplateLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _ddmTemplateLocalService.getBeanIdentifier();
	}

	/**
	* Returns the d d m template with the primary key.
	*
	* @param templateId the primary key of the d d m template
	* @return the d d m template
	* @throws PortalException if a d d m template with the primary key could not be found
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate getDDMTemplate(
		long templateId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmTemplateLocalService.getDDMTemplate(templateId);
	}

	/**
	* Returns the d d m template matching the UUID and group.
	*
	* @param uuid the d d m template's UUID
	* @param groupId the primary key of the group
	* @return the matching d d m template
	* @throws PortalException if a matching d d m template could not be found
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate getDDMTemplateByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmTemplateLocalService.getDDMTemplateByUuidAndGroupId(uuid,
			groupId);
	}

	/**
	* Returns a range of all the d d m templates.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of d d m templates
	* @param end the upper bound of the range of d d m templates (not inclusive)
	* @return the range of d d m templates
	*/
	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate> getDDMTemplates(
		int start, int end) {
		return _ddmTemplateLocalService.getDDMTemplates(start, end);
	}

	/**
	* Returns all the d d m templates matching the UUID and company.
	*
	* @param uuid the UUID of the d d m templates
	* @param companyId the primary key of the company
	* @return the matching d d m templates, or an empty list if no matches were found
	*/
	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		java.lang.String uuid, long companyId) {
		return _ddmTemplateLocalService.getDDMTemplatesByUuidAndCompanyId(uuid,
			companyId);
	}

	/**
	* Returns a range of d d m templates matching the UUID and company.
	*
	* @param uuid the UUID of the d d m templates
	* @param companyId the primary key of the company
	* @param start the lower bound of the range of d d m templates
	* @param end the upper bound of the range of d d m templates (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the range of matching d d m templates, or an empty list if no matches were found
	*/
	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		java.lang.String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.dynamic.data.mapping.model.DDMTemplate> orderByComparator) {
		return _ddmTemplateLocalService.getDDMTemplatesByUuidAndCompanyId(uuid,
			companyId, start, end, orderByComparator);
	}

	/**
	* Returns the number of d d m templates.
	*
	* @return the number of d d m templates
	*/
	@Override
	public int getDDMTemplatesCount() {
		return _ddmTemplateLocalService.getDDMTemplatesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery getExportActionableDynamicQuery(
		com.liferay.portlet.exportimport.lar.PortletDataContext portletDataContext) {
		return _ddmTemplateLocalService.getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _ddmTemplateLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_ddmTemplateLocalService.setBeanIdentifier(beanIdentifier);
	}

	/**
	* Updates the d d m template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param ddmTemplate the d d m template
	* @return the d d m template that was updated
	*/
	@Override
	public com.liferay.dynamic.data.mapping.model.DDMTemplate updateDDMTemplate(
		com.liferay.dynamic.data.mapping.model.DDMTemplate ddmTemplate) {
		return _ddmTemplateLocalService.updateDDMTemplate(ddmTemplate);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public DDMTemplateLocalService getWrappedDDMTemplateLocalService() {
		return _ddmTemplateLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedDDMTemplateLocalService(
		DDMTemplateLocalService ddmTemplateLocalService) {
		_ddmTemplateLocalService = ddmTemplateLocalService;
	}

	@Override
	public DDMTemplateLocalService getWrappedService() {
		return _ddmTemplateLocalService;
	}

	@Override
	public void setWrappedService(
		DDMTemplateLocalService ddmTemplateLocalService) {
		_ddmTemplateLocalService = ddmTemplateLocalService;
	}

	private DDMTemplateLocalService _ddmTemplateLocalService;
}