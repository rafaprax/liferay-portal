/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.content.web.internal.security.resource.permission;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceVirtualOrderItemPermissionUtil {

	public static boolean contains(
			PermissionChecker permissionChecker,
			CommerceVirtualOrderItem commerceVirtualOrderItem,
			ModelResourcePermission<CommerceVirtualOrderItem>
				commerceVirtualOrderItemModelResourcePermission,
			String actionId)
		throws PortalException {

		return commerceVirtualOrderItemModelResourcePermission.contains(
			permissionChecker, commerceVirtualOrderItem, actionId);
	}

}