/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.servlet;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.web.internal.configuration.CacheControlConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.repository.http.header.customizer.FileEntryHttpHeaderCustomizer;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.document.library.web.internal.configuration.CacheControlConfiguration",
	property = "http.header.name=" + HttpHeaders.CACHE_CONTROL,
	service = FileEntryHttpHeaderCustomizer.class
)
public class CacheControlFileEntryHttpHeaderCustomizer
	implements FileEntryHttpHeaderCustomizer {

	@Override
	public String getHttpHeaderValue(FileEntry fileEntry, String currentValue) {
		try {
			return _getHttpHeaderValue(fileEntry, currentValue);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return currentValue;
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new CacheControlFileEntryHttpHeaderCustomizer.
				CacheControlConfigurationManagedServiceFactory(),
			MapUtil.singletonDictionary(
				Constants.SERVICE_PID,
				"com.liferay.document.library.web.internal.configuration." +
					"CacheControlConfiguration.scoped"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_systemCacheControlConfiguration = ConfigurableUtil.createConfigurable(
			CacheControlConfiguration.class, properties);
	}

	private String _getHttpHeaderValue(FileEntry fileEntry, String currentValue)
		throws PortalException {

		CacheControlConfiguration cacheControlConfiguration =
			_companyConfigurationBeans.getOrDefault(
				fileEntry.getCompanyId(), _systemCacheControlConfiguration);

		if (ArrayUtil.contains(
				cacheControlConfiguration.notCacheableMimeTypes(),
				fileEntry.getMimeType())) {

			return HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
		}

		Company company = _companyLocalService.getCompany(
			fileEntry.getCompanyId());

		if (!_dlFileEntryModelResourcePermission.contains(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()),
				fileEntry.getPrimaryKey(), ActionKeys.VIEW)) {

			return currentValue;
		}

		if (cacheControlConfiguration.maxAge() <= 0) {
			return cacheControlConfiguration.cacheControl();
		}

		return String.format(
			"%s, max-age=%d", cacheControlConfiguration.cacheControl(),
			cacheControlConfiguration.maxAge());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CacheControlFileEntryHttpHeaderCustomizer.class);

	private final Map<Long, CacheControlConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;
	private volatile CacheControlConfiguration _systemCacheControlConfiguration;

	private class CacheControlConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.document.library.web.internal.configuration." +
				"CacheControlConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			_unmapPid(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			if (companyId != CompanyConstants.SYSTEM) {
				_companyConfigurationBeans.put(
					companyId,
					ConfigurableUtil.createConfigurable(
						CacheControlConfiguration.class, dictionary));
				_companyIds.put(pid, companyId);
			}
		}

		private void _unmapPid(String pid) {
			Long companyId = _companyIds.remove(pid);

			if (companyId != null) {
				_companyConfigurationBeans.remove(companyId);
			}
		}

		private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();

	}

}