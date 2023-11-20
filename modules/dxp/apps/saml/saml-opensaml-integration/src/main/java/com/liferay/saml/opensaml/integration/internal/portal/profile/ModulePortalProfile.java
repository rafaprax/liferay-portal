/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.portal.profile;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;
import com.liferay.saml.opensaml.integration.internal.field.expression.handler.MembershipsUserFieldExpressionHandler;

import java.util.Collections;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component(service = PortalProfile.class)
public class ModulePortalProfile extends BaseDSModulePortalProfile {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-180198")) {
			return;
		}

		init(
			componentContext,
			Collections.singleton(PortalProfile.PORTAL_PROFILE_NAME_DXP),
			MembershipsUserFieldExpressionHandler.class.getName());
	}

}