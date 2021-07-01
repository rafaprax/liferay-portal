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

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v4_0_0.util.KaleoDefinitionVersionTable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jesse Yeh
 */
public class UpgradeKaleoDefinitionVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		StringBundler sb = new StringBundler(3);

		sb.append("select kaleoDefinitionVersionId, name, version, ");
		sb.append("kaleoDefinitionId from KaleoDefinitionVersion order by ");
		sb.append("name, LENGTH(version), cast(version as decimal(4, 2)) asc");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(sb.toString()));
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update KaleoDefinitionVersion set version = ? where " +
					"kaleoDefinitionVersionId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"update KaleoDefinition set version = ? where " +
					"kaleoDefinitionId = ?")) {

			int count = 1;
			String curName = null;
			String previousName = null;

			while (resultSet.next()) {
				long kaleoDefinitionVersionId = resultSet.getLong(1);
				curName = resultSet.getString(2);
				long kaleoDefinitionId = resultSet.getLong(4);

				if (StringUtil.equals(previousName, curName)) {
					count++;
				}
				else {
					count = 1;
					previousName = curName;
				}

				preparedStatement2.setString(1, String.valueOf(count));
				preparedStatement2.setLong(2, kaleoDefinitionVersionId);

				preparedStatement2.addBatch();

				preparedStatement3.setInt(1, count);
				preparedStatement3.setLong(2, kaleoDefinitionId);

				preparedStatement3.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
		catch (SQLException sqlException) {
			throw new UpgradeException(sqlException);
		}

		if (!hasColumnType("KaleoDefinitionVersion", "version", "INTEGER")) {
			alter(
				KaleoDefinitionVersionTable.class,
				new AlterColumnType("version", "INTEGER"));
		}
	}

}