/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.soy.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.template.soy.utils.SoyTemplateUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Rafael Praxedes
 */
@Component(service = SoyCapabilityBundleTracker.class)
public class SoyCapabilityBundleTracker {

	public Bundle getBundle(long bundleId) {
		return _bundleMap.get(bundleId);
	}

	public Collection<Bundle> getBundles() {
		return _bundleMap.values();
	}

	public Bundle getTemplateBundle(String templateId) {
		long bundleId = SoyTemplateUtil.getBundleId(templateId);

		Bundle bundle = getBundle(bundleId);

		if (bundle == null) {
			throw new IllegalStateException(
				"There are no bundles providing " + templateId);
		}

		return bundle;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		int stateMask = Bundle.ACTIVE | Bundle.RESOLVED;

		_bundleTracker = new BundleTracker<>(
			bundleContext, stateMask,
			new SoyCapabilityBundleTrackerCustomizer());

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SoyCapabilityBundleTracker.class);

	private static final Map<Long, Bundle> _bundleMap =
		new ConcurrentHashMap<>();

	private BundleTracker<List<BundleCapability>> _bundleTracker;

	private static final class SoyCapabilityBundleTrackerCustomizer
		implements BundleTrackerCustomizer<List<BundleCapability>> {

		@Override
		public List<BundleCapability> addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			List<BundleCapability> bundleCapabilities =
				bundleWiring.getCapabilities("soy");

			if (ListUtil.isEmpty(bundleCapabilities)) {
				return bundleCapabilities;
			}

			for (BundleWire bundleWire : bundleWiring.getRequiredWires("soy")) {
				BundleRevision bundleRevision = bundleWire.getProvider();

				Bundle providerBundle = bundleRevision.getBundle();

				_bundleMap.put(providerBundle.getBundleId(), providerBundle);
			}

			_bundleMap.put(bundle.getBundleId(), bundle);

			return bundleCapabilities;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<BundleCapability> bundleCapabilities) {

			removedBundle(bundle, bundleEvent, bundleCapabilities);

			List<BundleCapability> newBundleCapabilities = addingBundle(
				bundle, bundleEvent);

			bundleCapabilities.clear();

			bundleCapabilities.addAll(newBundleCapabilities);
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<BundleCapability> bundleCapabilities) {

			_bundleMap.remove(bundle.getBundleId());
		}

	}

}