/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.user.manager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.scim.user.SCIMUser;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Rafael Praxedes
 */
@ProviderType
public interface SCIMUserManager {

	public SCIMUser addOrUpdateUser(SCIMUser scimUser) throws PortalException;

	public void deleteUser(SCIMUser scimUser) throws PortalException;

	public SCIMUser fetchUser(long companyId, long userId);
}