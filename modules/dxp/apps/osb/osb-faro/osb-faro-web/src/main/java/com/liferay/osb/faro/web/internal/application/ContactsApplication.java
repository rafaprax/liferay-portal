/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"osgi.jaxrs.application.base=/faro/" + FaroConstants.APPLICATION_CONTACTS,
		"osgi.jaxrs.name=Liferay.Osb.Faro.Web.Contacts",
		"auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=*",
		"auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false",
		"auth.verifier.PortalSessionAuthVerifier.urls.includes=*"
	},
	service = Application.class
)
public class ContactsApplication extends BaseApplication {
}