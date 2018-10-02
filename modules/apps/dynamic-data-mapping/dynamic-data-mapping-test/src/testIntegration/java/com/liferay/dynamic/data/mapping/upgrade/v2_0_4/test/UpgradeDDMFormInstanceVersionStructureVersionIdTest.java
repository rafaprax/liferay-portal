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

package com.liferay.dynamic.data.mapping.upgrade.v2_0_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rodrigo Paulino
 */
@RunWith(Arquillian.class)
public class UpgradeDDMFormInstanceVersionStructureVersionIdTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_structureId = CounterLocalServiceUtil.increment();

		_formInstanceId = CounterLocalServiceUtil.increment();

		setUpUpgradeDDMFormInstanceVersionStructureVersionId();
	}

	@After
	public void tearDown() throws Exception {
		deleteDDMStructureVersion(_structureId);

		deleteDDMFormInstanceVersion(_formInstanceId);
	}

	@Test
	public void testUpgradeDDMFormInstanceVersionAfterConsecutiveDrafts()
		throws Exception {

		long firstStructureVersionId = CounterLocalServiceUtil.increment();
		long firstFormInstanceVersionId = CounterLocalServiceUtil.increment();

		String firstFormInstanceVersion =
			DDMFormInstanceConstants.VERSION_DEFAULT;

		addDDMStructureVersionByPreparedStatement(
			firstStructureVersionId, new Timestamp(System.currentTimeMillis()),
			_structureId, WorkflowConstants.STATUS_APPROVED);

		addDDMFormInstanceVersionByPreparedStatement(
			firstFormInstanceVersionId, _formInstanceId,
			firstStructureVersionId, firstFormInstanceVersion,
			WorkflowConstants.STATUS_APPROVED);

		long secondStructureVersionId = CounterLocalServiceUtil.increment();
		long secondFormInstanceVersionId = CounterLocalServiceUtil.increment();

		String secondFormInstanceVersion = getNextVersion(
			firstFormInstanceVersion);

		addDDMStructureVersionByPreparedStatement(
			secondStructureVersionId, new Timestamp(System.currentTimeMillis()),
			_structureId, WorkflowConstants.STATUS_DRAFT);

		addDDMFormInstanceVersionByPreparedStatement(
			secondFormInstanceVersionId, _formInstanceId,
			firstStructureVersionId, secondFormInstanceVersion,
			WorkflowConstants.STATUS_DRAFT);

		long thirdStructureVersionId = CounterLocalServiceUtil.increment();

		addDDMStructureVersionByPreparedStatement(
			thirdStructureVersionId, new Timestamp(System.currentTimeMillis()),
			_structureId, WorkflowConstants.STATUS_DRAFT);

		_upgradeDDMFormInstanceVersionStructureVersionId.upgrade();

		DDMFormInstanceVersion upgradedDDMFormInstanceVersion =
			DDMFormInstanceVersionLocalServiceUtil.getDDMFormInstanceVersion(
				secondFormInstanceVersionId);

		Assert.assertEquals(
			thirdStructureVersionId,
			upgradedDDMFormInstanceVersion.getStructureVersionId());
	}

	protected void addDDMFormInstanceVersionByPreparedStatement(
			long formInstanceVersionId, long formInstanceId,
			long structureVersionId, String version, int status)
		throws Exception {

		StringBundler sb = new StringBundler(4);

		sb.append("insert into DDMFormInstanceVersion (");
		sb.append("formInstanceVersionId, formInstanceId, ");
		sb.append("structureVersionId, version, status) values (?, ?, ?, ?, ");
		sb.append("?)");

		String sql = sb.toString();

		try (Connection con = DataAccess.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, formInstanceVersionId);
			ps.setLong(2, formInstanceId);
			ps.setLong(3, structureVersionId);
			ps.setString(4, version);
			ps.setInt(5, status);

			ps.executeUpdate();
		}
	}

	protected void addDDMStructureVersionByPreparedStatement(
			Long structureVersionId, Timestamp createDate, long structureId,
			int status)
		throws Exception {

		StringBundler sb = new StringBundler(2);

		sb.append("insert into DDMStructureVersion (structureVersionId, ");
		sb.append("createDate, structureId, status) values (?, ?, ?, ?)");

		String sql = sb.toString();

		try (Connection con = DataAccess.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, structureVersionId);
			ps.setTimestamp(2, createDate);
			ps.setLong(3, structureId);
			ps.setInt(4, status);

			ps.executeUpdate();
		}
	}

	protected void deleteDDMFormInstanceVersion(long formInstanceId)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		StringBundler sb = new StringBundler(2);

		sb.append("delete from DDMFormInstanceVersion where formInstanceId = ");
		sb.append(formInstanceId);

		String sql = sb.toString();

		db.runSQL(sql);
	}

	protected void deleteDDMStructureVersion(long structureId)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		StringBundler sb = new StringBundler(2);

		sb.append("delete from DDMStructureVersion where structureId = ");
		sb.append(structureId);

		String sql = sb.toString();

		db.runSQL(sql);
	}

	protected String getNextVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		versionParts[1]++;

		return versionParts[0] + StringPool.PERIOD + versionParts[1];
	}

	protected void setUpUpgradeDDMFormInstanceVersionStructureVersionId() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String bundleSymbolicName, String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					register(
						fromSchemaVersionString, toSchemaVersionString,
						upgradeSteps);
				}

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						String className = clazz.getName();

						if (className.contains(_CLASS_NAME)) {
							_upgradeDDMFormInstanceVersionStructureVersionId =
								(UpgradeProcess)upgradeStep;
						}
					}
				}

			});
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_4." +
			"UpgradeDDMFormInstanceVersionStructureVersionId";

	@Inject(
		filter = "(&(objectClass=com.liferay.dynamic.data.mapping.internal.upgrade.DDMServiceUpgrade))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private long _formInstanceId;
	private long _structureId;
	private UpgradeProcess _upgradeDDMFormInstanceVersionStructureVersionId;

}