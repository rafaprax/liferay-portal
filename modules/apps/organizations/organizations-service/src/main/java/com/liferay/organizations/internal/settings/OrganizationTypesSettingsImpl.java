/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.settings;

import com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.users.admin.kernel.organization.types.OrganizationTypesSettings;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marco Leo
 */
@Component(service = OrganizationTypesSettings.class)
public class OrganizationTypesSettingsImpl
	implements OrganizationTypesSettings {

	@Override
	public String[] getChildrenTypes(String type) {
		OrganizationTypeConfiguration organizationTypeConfiguration =
			_pidTypeMap.get(type);

		if (organizationTypeConfiguration == null) {
			return new String[0];
		}

		return organizationTypeConfiguration.childrenTypes();
	}

	@Override
	public String[] getTypes() {
		return ArrayUtil.toStringArray(_pidTypeMap.keySet());
	}

	@Override
	public boolean isCountryEnabled(String type) {
		OrganizationTypeConfiguration organizationTypeConfiguration =
			_pidTypeMap.get(type);

		if (organizationTypeConfiguration == null) {
			return false;
		}

		return organizationTypeConfiguration.countryEnabled();
	}

	@Override
	public boolean isCountryRequired(String type) {
		OrganizationTypeConfiguration organizationTypeConfiguration =
			_pidTypeMap.get(type);

		if (organizationTypeConfiguration == null) {
			return false;
		}

		return organizationTypeConfiguration.countryRequired();
	}

	@Override
	public boolean isRootable(String type) {
		OrganizationTypeConfiguration organizationTypeConfiguration =
			_pidTypeMap.get(type);

		if (organizationTypeConfiguration == null) {
			return false;
		}

		return organizationTypeConfiguration.rootable();
	}

	public class OrganizationTypeConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			String organizationTypeConfiguration = _map.remove(pid);

			_pidTypeMap.remove(organizationTypeConfiguration);
		}

		@Override
		public String getName() {
			return "com.liferay.organizations.internal.configuration." +
				"OrganizationTypeConfiguration";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			OrganizationTypeConfiguration organizationTypeConfiguration =
				ConfigurableUtil.createConfigurable(
					OrganizationTypeConfiguration.class, dictionary);

			_pidTypeMap.put(
				organizationTypeConfiguration.name(),
				organizationTypeConfiguration);

			_map.put(pid, organizationTypeConfiguration.name());
		}

	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_managedServiceFactoryServiceRegistration =
			bundleContext.registerService(
				ManagedServiceFactory.class,
				new OrganizationTypeConfigurationManagedServiceFactory(),
				HashMapDictionaryBuilder.put(
					Constants.SERVICE_PID,
					"com.liferay.organizations.internal.configuration." +
						"OrganizationTypeConfiguration"
				).build());
	}

	@Deactivate
	protected void deactivate() {
		_managedServiceFactoryServiceRegistration.unregister();
	}

	private ServiceRegistration<ManagedServiceFactory>
		_managedServiceFactoryServiceRegistration;
	private final Map<String, String> _map = new HashMap<>();
	private final Map<String, OrganizationTypeConfiguration> _pidTypeMap =
		new HashMap<>();

}