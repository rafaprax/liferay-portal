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

package com.liferay.dynamic.data.mapping.data.provider.instance;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderException;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.storage.StorageAdapter;
import com.liferay.dynamic.data.mapping.storage.StorageAdapterRegistry;
import com.liferay.portal.kernel.util.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true,
	property = "ddm.data.provider.instance.id=ddm-storage-types"
)
public class DDMStorageTypesDataProvider implements DDMDataProvider {

	@Override
	public DDMDataProviderResponse getData(
			DDMDataProviderRequest ddmDataProviderRequest)
		throws DDMDataProviderException {

		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		StorageAdapter storageAdapter =
			storageAdapterRegistry.getDefaultStorageAdapter();

		String storageTypeDefault = storageAdapter.getStorageType();

		keyValuePairs.add(
			new KeyValuePair(storageTypeDefault, storageTypeDefault));

		Set<String> storageTypes = storageAdapterRegistry.getStorageTypes();

		for (String storageType : storageTypes) {
			if (storageType.equals(storageTypeDefault)) {
				continue;
			}

			keyValuePairs.add(new KeyValuePair(storageType, storageType));
		}

		DDMDataProviderResponse.Builder builder =
			DDMDataProviderResponse.Builder.newBuilder();

		builder.withOutput("Default-Output", keyValuePairs);

		return builder.build();
	}

	@Override
	public Class<?> getSettings() {
		throw new UnsupportedOperationException();
	}

	@Reference
	protected StorageAdapterRegistry storageAdapterRegistry;

}