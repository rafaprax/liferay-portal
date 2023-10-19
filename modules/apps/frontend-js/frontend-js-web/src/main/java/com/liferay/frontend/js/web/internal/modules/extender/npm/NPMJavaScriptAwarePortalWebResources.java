/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.modules.extender.npm;

import com.liferay.frontend.js.loader.modules.extender.npm.JavaScriptAwarePortalWebResources;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(service = JavaScriptAwarePortalWebResources.class)
public class NPMJavaScriptAwarePortalWebResources
	extends NPMPortalWebResources implements JavaScriptAwarePortalWebResources {

	@Override
	public void updateLastModified(long lastModified) {
		this.lastModified.accumulateAndGet(lastModified, Math::max);
	}

}