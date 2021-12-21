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

package com.liferay.image.internal.activator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Rafael Praxedes
 */
public class ImageImplBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		_register();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_unregisterImageReaderSpi();
	}

	private void _register() {
		IIORegistry iioRegistry = IIORegistry.getDefaultInstance();

		Iterator<ImageReaderSpi> iterator = ServiceRegistry.lookupProviders(
			ImageReaderSpi.class,
			ImageImplBundleActivator.class.getClassLoader());

		while (iterator.hasNext()) {
			_imageReaderSpiSet.add(iterator.next());
		}

		iioRegistry.registerServiceProvider(_imageReaderSpiSet.iterator());
	}

	private void _unregisterImageReaderSpi() {
		IIORegistry iioRegistry = IIORegistry.getDefaultInstance();

		for (ImageReaderSpi provider : _imageReaderSpiSet) {
			iioRegistry.deregisterServiceProvider(provider);
		}

		_imageReaderSpiSet.clear();
	}

	private final Set<ImageReaderSpi> _imageReaderSpiSet = new HashSet<>();

}