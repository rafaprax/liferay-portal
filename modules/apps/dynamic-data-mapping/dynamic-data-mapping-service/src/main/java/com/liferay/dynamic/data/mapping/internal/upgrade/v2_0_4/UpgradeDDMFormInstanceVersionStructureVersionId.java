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

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_4;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Paulino
 */
public class UpgradeDDMFormInstanceVersionStructureVersionId
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws SQLException {
		StringBundler sb1 = new StringBundler(10);

		/*Select the form instance versions that might need update, i.e. only
		drafts
		  */
		sb1.append("select DDMFormInstanceVersion.formInstanceVersionId, ");
		sb1.append("DDMFormInstanceVersion.structureVersionId, ");
		sb1.append("DDMStructureVersion.structureId from ");
		sb1.append("DDMFormInstanceVersion join DDMStructureVersion on ");
		sb1.append("DDMStructureVersion.structureVersionId = ");
		sb1.append("DDMFormInstanceVersion.structureVersionId where ");
		sb1.append("DDMFormInstanceVersion.status = ");
		sb1.append(WorkflowConstants.STATUS_DRAFT);
		sb1.append(" order by DDMStructureVersion.structureId, ");
		sb1.append("DDMFormInstanceVersion.structureVersionId");

		try (PreparedStatement ps1 = connection.prepareStatement(
				sb1.toString());
			ResultSet rs1 = ps1.executeQuery()) {

			List<Long> list = new ArrayList<>();

			String comma = "";
			String parameters = "";

			/*
			Populate the list with unique values of structure IDs selected from
			the original query
			 */
			while (rs1.next()) {
				long structureId = rs1.getLong("structureId");

				if (!list.contains(structureId)) {
					list.add(structureId);

					parameters += comma + "?";
					comma = ", ";
				}
			}

			if (ListUtil.isNotEmpty(list)) {
				StringBundler sb2 = new StringBundler(16);

				/*
				Select the respective structure versions that were created after
				the first version of a form instance and only if that form
				instance has a draft version
				 */
				sb2.append("select DDMStructureVersion.structureVersionId, ");
				sb2.append("DDMStructureVersion.status, DDMStructureVersion.");
				sb2.append("structureId from DDMStructureVersion join (");
				sb2.append("select DDMStructureVersion.structureId, ");
				sb2.append("DDMStructureVersion.createDate from ");
				sb2.append("DDMFormInstanceVersion join DDMStructureVersion ");
				sb2.append("on DDMFormInstanceVersion.version = '1.0' and ");
				sb2.append("DDMFormInstanceVersion.structureVersionId = ");
				sb2.append("DDMStructureVersion.structureVersionId) sub on ");
				sb2.append("sub.structureId = DDMStructureVersion.");
				sb2.append("structureId where sub.createDate <= ");
				sb2.append("DDMStructureVersion.createDate and ");
				sb2.append("DDMStructureVersion.structureId in (");
				sb2.append(parameters);
				sb2.append(") order by DDMStructureVersion.structureId, ");
				sb2.append("DDMStructureVersion.structureVersionId");

				try (PreparedStatement ps2 = connection.prepareStatement(
						sb2.toString());
					PreparedStatement ps3 =
						AutoBatchPreparedStatementUtil.concurrentAutoBatch(
							connection,
							"update DDMFormInstanceVersion set " +
								"structureVersionId = ? where " +
									"formInstanceVersionId = ?")) {

					for (int i = 0; i < list.size(); i++) {
						ps2.setLong(i + 1, list.get(i));
					}

					try (ResultSet rs2 = ps2.executeQuery()) {

						/* Reiterate the result of all form instance version
						drafts*/
						rs1.beforeFirst();

						while (rs1.next()) {
							long structureId1 = rs1.getLong("structureId");
							long structureVersionId1 = rs1.getLong(
								"structureVersionId");

							long newStructureVersionId = structureVersionId1;

							boolean currentStructureVersionIdFound = false;

							while (rs2.next()) {
								long structureVersionId2 = rs2.getLong(
									"structureVersionId");

								if (structureVersionId1 ==
										structureVersionId2) {

									currentStructureVersionIdFound = true;
								}

								if (currentStructureVersionIdFound) {

									/*
									Examine the following row to determine if
									current structure version is the last draft
									created for the form instance version
									 */
									if (rs2.next()) {
										int status = rs2.getInt("status");
										long structureId2 = rs2.getLong(
											"structureId");

										rs2.previous();

										if ((structureId2 != structureId1) ||
											(status ==
												WorkflowConstants.
													STATUS_APPROVED)) {

											newStructureVersionId =
												structureVersionId2;

											break;
										}
									}
									else {
										newStructureVersionId =
											structureVersionId2;
									}
								}
							}

							/*
							Only updates the form instance version if its
							structure version ID has changed
							 */
							if (newStructureVersionId != structureVersionId1) {
								long formInstanceVersionId = rs1.getLong(
									"formInstanceVersionId");

								ps3.setLong(1, newStructureVersionId);
								ps3.setLong(2, formInstanceVersionId);

								ps3.addBatch();
							}
						}

						ps3.executeBatch();
					}
				}
			}
		}
	}

}