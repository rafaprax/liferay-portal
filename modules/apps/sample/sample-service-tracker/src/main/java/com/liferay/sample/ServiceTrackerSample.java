package com.liferay.sample;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@Component(immediate = true)
public class ServiceTrackerSample {

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		_serviceTracker = new ServiceTracker<>(
				bundleContext,
				bundleContext.createFilter("(test.property=*)"),
				new ServiceTrackerCustomizer<Object, Object>() {
					@Override
					public Object addingService(
						ServiceReference<Object> serviceReference) {

						Object service =
							bundleContext.getService(serviceReference);

						System.out.println("addingService: " + service.getClass());
						return service;
					}

					@Override
					public void modifiedService(
						ServiceReference<Object> serviceReference, Object o) {

					}

					@Override
					public void removedService(
						ServiceReference<Object> serviceReference, Object service) {

						System.out.println("removedService: " + service.getClass());

						bundleContext.ungetService(serviceReference);
					}
				});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}



	private ServiceTracker _serviceTracker;
}
