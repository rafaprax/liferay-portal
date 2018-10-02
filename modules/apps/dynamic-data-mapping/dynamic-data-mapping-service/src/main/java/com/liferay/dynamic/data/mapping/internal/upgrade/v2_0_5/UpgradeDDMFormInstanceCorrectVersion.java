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

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_5;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Rodrigo Paulino
 */
public class UpgradeDDMFormInstanceCorrectVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws SQLException {
		StringBundler sb = new StringBundler(10);

		sb.append("select DDMFormInstance.formInstanceId, DDMFormInstance.");
		sb.append("version, DDMFormInstanceVersion.version from ");
		sb.append("DDMFormInstance join (select formInstanceId, status, max(");
		sb.append("formInstanceVersionId) as formInstanceVersionId from ");
		sb.append("DDMFormInstanceVersion group by formInstanceId, status ");
		sb.append("having status = 0) sub on sub.formInstanceId = ");
		sb.append("DDMFormInstance.formInstanceId join ");
		sb.append("DDMFormInstanceVersion on sub.formInstanceVersionId = ");
		sb.append("DDMFormInstanceVersion.formInstanceVersionId where ");
		sb.append("DDMFormInstance.version <> DDMFormInstanceVersion.version");

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			ResultSet rs = ps1.executeQuery();
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMFormInstance set version = ? where " +
						"formInstanceId = ?")) {

			while (rs.next()) {
				long formInstanceId = rs.getLong("formInstanceId");
				String version = rs.getString("newVersion");

				ps2.setString(1, version);

				ps2.setLong(2, formInstanceId);

				ps2.addBatch();
			}

			ps2.executeBatch();
		}
	}

}