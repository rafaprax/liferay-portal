/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Liferay
 */
public class FIPSModeUtil {

	public static boolean isFIPSModeEnabled() {
		return PropsValues.PORTAL_SECURITY_FIPS_MODE_ENABLED;
	}

}
