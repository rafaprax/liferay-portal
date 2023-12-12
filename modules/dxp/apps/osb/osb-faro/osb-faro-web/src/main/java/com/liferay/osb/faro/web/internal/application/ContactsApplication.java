/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.FaroController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_CONTACTS)
@Component(property = "jaxrs.application=true", service = Application.class)
public class ContactsApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_accountController);
		controllers.add(_activityController);
		controllers.add(_activityGroupController);
		controllers.add(_contactsCardController);
		controllers.add(_contactsCardTemplateController);
		controllers.add(_contactsLayoutController);
		controllers.add(_contactsLayoutTemplateController);
		controllers.add(_dataSourceController);
		controllers.add(_fieldController);
		controllers.add(_fieldMappingController);
		controllers.add(_individualController);
		controllers.add(_individualSegmentController);
		controllers.add(_interestController);
		controllers.add(_pagesVisitedController);
		controllers.add(_sessionController);

		return controllers;
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.AccountController)"
	)
	private FaroController _accountController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ActivityController)"
	)
	private FaroController _activityController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ActivityGroupController)"
	)
	private FaroController _activityGroupController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardController)"
	)
	private FaroController _contactsCardController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardTemplateController)"
	)
	private FaroController _contactsCardTemplateController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutController)"
	)
	private FaroController _contactsLayoutController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutTemplateController)"
	)
	private FaroController _contactsLayoutTemplateController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.DataSourceController)"
	)
	private FaroController _dataSourceController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.FieldController)"
	)
	private FaroController _fieldController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingController)"
	)
	private FaroController _fieldMappingController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.IndividualController)"
	)
	private FaroController _individualController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.IndividualSegmentController)"
	)
	private FaroController _individualSegmentController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.InterestController)"
	)
	private FaroController _interestController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.PagesVisitedController)"
	)
	private FaroController _pagesVisitedController;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.web.internal.controller.contacts.SessionController)"
	)
	private FaroController _sessionController;

}