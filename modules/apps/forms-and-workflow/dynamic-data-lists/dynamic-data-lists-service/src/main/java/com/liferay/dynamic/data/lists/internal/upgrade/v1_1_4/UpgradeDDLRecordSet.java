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

package com.liferay.dynamic.data.lists.internal.upgrade.v1_1_4;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Leonardo Barros
 */
public class UpgradeDDLRecordSet extends UpgradeProcess {

	public UpgradeDDLRecordSet(
		CounterLocalService counterLocalService,
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMFormInstanceRecordLocalService
			ddmFormInstanceRecordLocalService,
		DDMFormInstanceRecordVersionLocalService
			ddmFormInstanceRecordVersionLocalService,
		DDMFormInstanceVersionLocalService
			ddmFormInstanceVersionLocalService,
		DDMStructureLinkLocalService
			ddmStructureLinkLocalService,
		PortletPreferencesLocalService
			portletPreferencesLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_counterLocalService = counterLocalService;
		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormInstanceRecordVersionLocalService =
			ddmFormInstanceRecordVersionLocalService;
		_ddmFormInstanceVersionLocalService =
			ddmFormInstanceVersionLocalService;
		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
		_portletPreferencesLocalService = portletPreferencesLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	protected void deleteDDLRecords(long recordSetId) throws Exception {
		deleteDDLRecordVersions(recordSetId);

		try (PreparedStatement ps = connection.prepareStatement(
				"delete from DDLRecord where recordSetId = ?")) {

			ps.setLong(1, recordSetId);

			ps.executeUpdate();
		}
	}

	protected void deleteDDLRecordSet(long ddmStructureId, long recordSetId)
		throws Exception {

		_ddmStructureLinkLocalService.deleteStructureStructureLinks(
			ddmStructureId);
		deleteDDLRecords(recordSetId);
		deleteDDLRecordSetVersions(recordSetId);

		try (PreparedStatement ps = connection.prepareStatement(
				"delete from DDLRecordSet where recordSetId = ?")) {

			ps.setLong(1, recordSetId);

			ps.executeUpdate();
		}
	}

	protected void deleteDDLRecordSetVersions(long recordSetId)
		throws Exception {

		try (PreparedStatement ps = connection.prepareStatement(
				"delete from DDLRecordSetVersion where recordSetId = ?")) {

			ps.setLong(1, recordSetId);

			ps.executeUpdate();
		}
	}

	protected void deleteDDLRecordVersions(long recordSetId) throws Exception {
		try (PreparedStatement ps = connection.prepareStatement(
				"delete from DDLRecordVersion where recordSetId = ?")) {

			ps.setLong(1, recordSetId);

			ps.executeUpdate();
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		StringBundler sb = new StringBundler(2);

		sb.append("select DDLRecordSet.* from DDLRecordSet ");
		sb.append("where DDLRecordSet.scope = ?");

		try (PreparedStatement ps1 =
				connection.prepareStatement(sb.toString())) {

			ps1.setInt(1, DDLRecordSetConstants.SCOPE_FORMS);

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					DDMFormInstance ddmFormInstance =
						_ddmFormInstanceLocalService.createDDMFormInstance(
							_counterLocalService.increment());

					long ddmStructureId = rs.getLong("DDMStructureId");

					ddmFormInstance.setCompanyId(rs.getLong("companyId"));

					ddmFormInstance.setCreateDate(
						rs.getTimestamp("createDate"));
					ddmFormInstance.setDescription(rs.getString("description"));
					ddmFormInstance.setGroupId(rs.getLong("groupId"));
					ddmFormInstance.setLastPublishDate(
						rs.getTimestamp("lastPublishDate"));
					ddmFormInstance.setModifiedDate(
						rs.getTimestamp("modifiedDate"));
					ddmFormInstance.setName(rs.getString("name"));
					ddmFormInstance.setSettings(rs.getString("settings_"));
					ddmFormInstance.setStructureId(ddmStructureId);
					ddmFormInstance.setUserId(rs.getLong("userId"));
					ddmFormInstance.setUserName(rs.getString("userName"));
					ddmFormInstance.setVersion(rs.getString("version"));
					ddmFormInstance.setVersionUserId(
						rs.getLong("versionUserId"));
					ddmFormInstance.setVersionUserName(
						rs.getString("versionUserName"));

					_ddmFormInstanceLocalService.addDDMFormInstance(
						ddmFormInstance);

					long recordSetId = rs.getLong("recordSetId");

					upgradeDDLRecordSetVersions(recordSetId, ddmFormInstance);
					upgradeDDLRecords(recordSetId, ddmFormInstance);
					upgradeResourcePermission(
						String.valueOf(recordSetId),
						"com.liferay.dynamic.data.mapping.model." +
							"DDMFormInstance");

					upgradeResourcePermission(
						String.valueOf(ddmStructureId),
						"com.liferay.dynamic.data.mapping.model." +
							"DDMFormInstance-com.liferay.dynamic.data." +
								"mapping.model.DDMStructure");

					updateInstanceablePortletPreferences(
						ddmFormInstance.getFormInstanceId(), recordSetId,
						"com_liferay_dynamic_data_lists_form_web_portlet_" +
							"DDLFormPortlet",
						"com_liferay_dynamic_data_mapping_form_web_portlet_" +
							"DDMFormPortlet");

					deleteDDLRecordSet(ddmStructureId, recordSetId);
				}
			}
		}
	}

	protected void updateInstanceablePortletPreferences(
			long ddmFormInstanceId, long recordSetId, String oldPortletId,
			String newPortletId)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_portletPreferencesLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			new ActionableDynamicQuery.AddCriteriaMethod() {

				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					Junction junction = RestrictionsFactoryUtil.disjunction();

					Property property = PropertyFactoryUtil.forName(
						"portletId");

					junction.add(property.eq(oldPortletId));
					junction.add(property.like(oldPortletId + "_INSTANCE_%"));
					junction.add(
						property.like(oldPortletId + "_USER_%_INSTANCE_%"));

					dynamicQuery.add(junction);
				}

			});
		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.
				PerformActionMethod<PortletPreferences>() {

				@Override
				public void performAction(PortletPreferences portletPreference)
					throws PortalException {

					updatePortletPreferences(
						ddmFormInstanceId, recordSetId, oldPortletId,
						newPortletId, portletPreference);
				}

			});

		actionableDynamicQuery.performActions();
	}

	protected void updatePortletPreferences(
		long ddmFormInstanceId, long recordSetId, String oldPortletId,
		String newPortletId, PortletPreferences portletPreferences) {

		portletPreferences.setPortletId(
			StringUtil.replace(
				portletPreferences.getPortletId(), oldPortletId, newPortletId));

		String preferences = portletPreferences.getPreferences();

		preferences = StringUtil.replace(
			preferences, String.valueOf(recordSetId),
			String.valueOf(ddmFormInstanceId));

		portletPreferences.setPreferences(preferences);

		_portletPreferencesLocalService.updatePortletPreferences(
			portletPreferences);
	}

	protected void upgradeDDLRecords(
			long recordSetId, DDMFormInstance ddmFormInstance)
		throws Exception {

		StringBundler sb = new StringBundler(2);

		sb.append("select DDLRecord.* from DDLRecord ");
		sb.append("where DDLRecord.recordSetId = ?");

		try (PreparedStatement ps1 =
				connection.prepareStatement(sb.toString())) {

			ps1.setLong(1, recordSetId);

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					DDMFormInstanceRecord ddmFormInstanceRecord =
						_ddmFormInstanceRecordLocalService.
							createDDMFormInstanceRecord(
								_counterLocalService.increment());

					ddmFormInstanceRecord.setCompanyId(rs.getLong("company"));
					ddmFormInstanceRecord.setCreateDate(
						rs.getTimestamp("createDate"));
					ddmFormInstanceRecord.setFormInstanceId(
						ddmFormInstance.getFormInstanceId());
					ddmFormInstanceRecord.setFormInstanceVersion(
						ddmFormInstance.getVersion());
					ddmFormInstanceRecord.setGroupId(rs.getLong("groupId"));
					ddmFormInstanceRecord.setLastPublishDate(
						rs.getTimestamp("lastPublishDate"));
					ddmFormInstanceRecord.setModifiedDate(
						rs.getTimestamp("modifiedDate"));
					ddmFormInstanceRecord.setStorageId(
						rs.getLong("DDMStorageId"));
					ddmFormInstanceRecord.setUserId(rs.getLong("userId"));
					ddmFormInstanceRecord.setUserName(rs.getString("userName"));
					ddmFormInstanceRecord.setVersion(rs.getString("version"));
					ddmFormInstanceRecord.setVersionUserId(
						rs.getLong("versionUserId"));
					ddmFormInstanceRecord.setVersionUserName(
						rs.getString("versionUserName"));

					_ddmFormInstanceRecordLocalService.addDDMFormInstanceRecord(
						ddmFormInstanceRecord);

					long recordId = rs.getLong("recordId");

					upgradeDDLRecordVersions(
						recordId, ddmFormInstance, ddmFormInstanceRecord);
				}
			}
		}
	}

	protected void upgradeDDLRecordSetVersions(
			long recordSetId, DDMFormInstance ddmFormInstance)
		throws Exception {

		StringBundler sb = new StringBundler(2);

		sb.append("select DDLRecordSetVersion.* from DDLRecordSetVersion ");
		sb.append("where DDLRecordSetVersion.recordSetId = ?");

		try (PreparedStatement ps1 =
				connection.prepareStatement(sb.toString())) {

			ps1.setLong(1, recordSetId);

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					DDMFormInstanceVersion ddmFormInstanceVersion =
						_ddmFormInstanceVersionLocalService.
							createDDMFormInstanceVersion(
								_counterLocalService.increment());

					ddmFormInstanceVersion.setCompanyId(
						rs.getLong("companyId"));
					ddmFormInstanceVersion.setCreateDate(
						rs.getTimestamp("createDate"));
					ddmFormInstanceVersion.setDescriptionMap(
						LocalizationUtil.getLocalizationMap(
							rs.getString("description")));
					ddmFormInstanceVersion.setFormInstanceId(
						ddmFormInstance.getFormInstanceId());
					ddmFormInstanceVersion.setGroupId(rs.getLong("groupId"));
					ddmFormInstanceVersion.setNameMap(
						LocalizationUtil.getLocalizationMap(
							rs.getString("name")));
					ddmFormInstanceVersion.setSettings(
						rs.getString("settings_"));
					ddmFormInstanceVersion.setStatus(rs.getInt("status"));
					ddmFormInstanceVersion.setStatusByUserId(
						rs.getLong("statusByUserId"));
					ddmFormInstanceVersion.setStatusByUserName(
						rs.getString("statusByUserName"));
					ddmFormInstanceVersion.setStatusDate(
						rs.getTimestamp("statusDate"));
					ddmFormInstanceVersion.setStructureVersionId(
						rs.getLong("DDMStructureVersionId"));
					ddmFormInstanceVersion.setUserId(rs.getLong("userId"));
					ddmFormInstanceVersion.setUserName(
						rs.getString("userName"));
					ddmFormInstanceVersion.setVersion(rs.getString("version"));

					_ddmFormInstanceVersionLocalService.
						addDDMFormInstanceVersion(ddmFormInstanceVersion);
				}
			}
		}
	}

	protected void upgradeDDLRecordVersions(
			long recordId, DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		StringBundler sb = new StringBundler(2);

		sb.append("select DDLRecordVersion.* from DDLRecordVersion ");
		sb.append("where DDLRecordVersion.recordId = ?");

		try (PreparedStatement ps1 =
				connection.prepareStatement(sb.toString())) {

			ps1.setLong(1, recordId);

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
						_ddmFormInstanceRecordVersionLocalService.
							createDDMFormInstanceRecordVersion(
								_counterLocalService.increment());

					ddmFormInstanceRecordVersion.setCompanyId(
						rs.getLong("companyId"));
					ddmFormInstanceRecordVersion.setCreateDate(
						rs.getTimestamp("createDate"));
					ddmFormInstanceRecordVersion.setFormInstanceId(
						ddmFormInstanceRecord.getFormInstanceId());
					ddmFormInstanceRecordVersion.setFormInstanceRecordId(
						ddmFormInstanceRecord.getFormInstanceRecordId());
					ddmFormInstanceRecordVersion.setFormInstanceVersion(
						ddmFormInstance.getVersion());
					ddmFormInstanceRecordVersion.setGroupId(
						rs.getLong("groupId"));
					ddmFormInstanceRecordVersion.setStorageId(
						rs.getInt("DDMStorageId"));
					ddmFormInstanceRecordVersion.setStatus(rs.getInt("status"));
					ddmFormInstanceRecordVersion.setStatusByUserId(
						rs.getLong("statusByUserId"));
					ddmFormInstanceRecordVersion.setStatusByUserName(
						rs.getString("statusByUserName"));
					ddmFormInstanceRecordVersion.setStatusDate(
						rs.getTimestamp("statusDate"));
					ddmFormInstanceRecordVersion.setUserId(
						rs.getLong("userId"));
					ddmFormInstanceRecordVersion.setUserName(
						rs.getString("userName"));
					ddmFormInstanceRecordVersion.setVersion(
						rs.getString("version"));
				}
			}
		}
	}

	protected void upgradeResourcePermission(String primKey, String name)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_resourcePermissionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			new ActionableDynamicQuery.AddCriteriaMethod() {

				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					Property nameProperty = PropertyFactoryUtil.forName(
						"primKey");

					dynamicQuery.add(nameProperty.eq(primKey));
				}

			});
		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.
				PerformActionMethod<ResourcePermission>() {

				@Override
				public void performAction(ResourcePermission resourcePermission)
					throws PortalException {

					resourcePermission.setName(name);

					_resourcePermissionLocalService.updateResourcePermission(
						resourcePermission);
				}

			});

		actionableDynamicQuery.performActions();
	}

	private final CounterLocalService _counterLocalService;
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;
	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}