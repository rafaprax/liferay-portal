/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.fips.CompanyKeyStoreUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

/**
 * Migrates company encryption keys from raw Base64 strings in the CompanyInfo
 * table to a FIPS-compliant PKCS12 KeyStore. Only runs when FIPS mode is
 * enabled. After migration, the CompanyInfo.key_ column contains a KeyStore
 * alias instead of raw key bytes.
 *
 * @author Liferay
 */
public class UpgradeCompanyKeyToKeyStore extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!FIPSModeUtil.isFIPSModeEnabled()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"FIPS mode is not enabled. Skipping company key " +
						"migration to KeyStore.");
			}

			return;
		}

		String keyAlgorithm = StringUtil.toUpperCase(
			GetterUtil.getString(
				PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_ALGORITHM)));

		if (_log.isInfoEnabled()) {
			_log.info(
				"Migrating company encryption keys from database to " +
					"FIPS-compliant KeyStore...");
		}

		int migratedCount = 0;

		try (PreparedStatement selectPS = connection.prepareStatement(
				"SELECT companyInfoId, companyId, key_ FROM CompanyInfo");

			ResultSet resultSet = selectPS.executeQuery()) {

			while (resultSet.next()) {
				long companyInfoId = resultSet.getLong("companyInfoId");
				long companyId = resultSet.getLong("companyId");
				String keyValue = resultSet.getString("key_");

				if ((keyValue == null) || keyValue.isEmpty()) {
					continue;
				}

				if (CompanyKeyStoreUtil.isKeyStoreAlias(keyValue)) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Company " + companyId +
								" key is already a KeyStore alias: " +
									keyValue);
					}

					continue;
				}

				try {
					byte[] keyBytes = Base64.decode(keyValue);

					SecretKeySpec secretKeySpec = new SecretKeySpec(
						keyBytes, keyAlgorithm);

					Arrays.fill(keyBytes, (byte)0);

					String alias = CompanyKeyStoreUtil.generateAlias(
						companyId);

					CompanyKeyStoreUtil.setKey(alias, secretKeySpec);

					try (PreparedStatement updatePS =
							connection.prepareStatement(
								"UPDATE CompanyInfo SET key_ = ? WHERE " +
									"companyInfoId = ?")) {

						updatePS.setString(1, alias);
						updatePS.setLong(2, companyInfoId);

						updatePS.executeUpdate();
					}

					migratedCount++;

					if (_log.isInfoEnabled()) {
						_log.info(
							"Migrated company " + companyId +
								" key to KeyStore alias: " + alias);
					}
				}
				catch (Exception exception) {
					_log.error(
						"Unable to migrate company " + companyId +
							" key to KeyStore",
						exception);
				}
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Company key migration complete. Migrated " + migratedCount +
					" keys to KeyStore.");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeCompanyKeyToKeyStore.class);

}
