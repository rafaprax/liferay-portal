/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_0_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Rafael Praxedes
 */
public class KaleoDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"select content, kaleoDefinitionId from KaleoDefinition");
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update KaleoDefinition set content = ? where " +
						"kaleoDefinitionId = ?")) {

			while (resultSet.next()) {
				preparedStatement.setString(
					1,
					WorkflowDefinitionContentUtil.toJSON(
						resultSet.getString("content")));
				preparedStatement.setLong(
					2, resultSet.getLong("kaleoDefinitionId"));

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

}