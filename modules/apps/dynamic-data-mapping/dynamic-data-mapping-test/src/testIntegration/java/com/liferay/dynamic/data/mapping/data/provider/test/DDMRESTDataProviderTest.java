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

package com.liferay.dynamic.data.mapping.data.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class DDMRESTDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetCountries() throws Exception {
		Class<?> ddmDataProviderSettings = _ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProviderSettings);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"cacheable", Boolean.FALSE.toString()));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterable", Boolean.FALSE.toString()));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterParameterName", StringPool.BLANK));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"password", "test"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"url",
				"http://localhost:8080/api/jsonws/country/get-countries"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"username", "test@liferay.com"));

		DDMFormFieldValue outputParameters =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameters", StringPool.BLANK);

		ddmFormValues.addDDMFormFieldValue(outputParameters);

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterName", "output"));

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterPath", "nameCurrentValue;countryId"));

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterType", "[\"list\"]"));

		long ddmDataProviderInstanceId = saveDDMDataProviderInstance(
			ddmFormValues);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest =
			builder.withDDMDataProviderId(
				String.valueOf(ddmDataProviderInstanceId)
			).build();

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(ddmDataProviderRequest);

		Assert.assertNotNull(ddmDataProviderResponse);

		Optional<List<KeyValuePair>> optionalKeyValuePairs =
			ddmDataProviderResponse.getOutput("output", List.class);

		Assert.assertTrue(optionalKeyValuePairs.isPresent());

		List<KeyValuePair> actualKeyValuePairs = optionalKeyValuePairs.get();

		List<KeyValuePair> expectedKeyValuePairs = createKeyValuePairs();

		Assert.assertTrue(
			actualKeyValuePairs.containsAll(expectedKeyValuePairs));

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			ddmDataProviderInstanceId);
	}

	@Test
	public void testGetCountryByName() throws Exception {
		Class<?> ddmDataProviderSettings = _ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProviderSettings);

		String url =
			"http://localhost:8080/api/jsonws/country/get-country-by-name";

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"cacheable", Boolean.FALSE.toString()));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterable", Boolean.TRUE.toString()));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterParameterName", "name"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"password", "test"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"url", url));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"username", "test@liferay.com"));

		DDMFormFieldValue outputParameters =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameters", StringPool.BLANK);

		ddmFormValues.addDDMFormFieldValue(outputParameters);

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterName", "output"));

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterPath", "nameCurrentValue;countryId"));

		outputParameters.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterType", "[\"list\"]"));

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest = builder.withParameter(
			"filterParameterValue", "brazil"
		).build();

		long ddmDataProviderInstanceId = saveDDMDataProviderInstance(
			ddmFormValues);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(ddmDataProviderRequest);

		Assert.assertNotNull(ddmDataProviderResponse);

		Optional<List<KeyValuePair>> optionalKeyValuePairs =
			ddmDataProviderResponse.getOutput("output", List.class);

		Assert.assertTrue(optionalKeyValuePairs.isPresent());

		List<KeyValuePair> expectedKeyValuePairs = optionalKeyValuePairs.get();

		int actualSize = expectedKeyValuePairs.size();

		Assert.assertEquals(1, actualSize);

		KeyValuePair actualKeyValuePair = expectedKeyValuePairs.get(0);

		Assert.assertEquals("48", actualKeyValuePair.getKey());
		Assert.assertEquals("Brazil", (String)actualKeyValuePair.getValue());

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			ddmDataProviderInstanceId);
	}

	protected List<KeyValuePair> createKeyValuePairs() {
		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		keyValuePairs.add(new KeyValuePair("3", "France"));
		keyValuePairs.add(new KeyValuePair("15", "Spain"));
		keyValuePairs.add(new KeyValuePair("19", "United States"));
		keyValuePairs.add(new KeyValuePair("48", "Brazil"));

		return keyValuePairs;
	}

	protected long saveDDMDataProviderInstance(DDMFormValues ddmFormValues)
		throws PortalException {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.BRAZIL, "Teste");

		DDMDataProviderInstance ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				nameMap, null, ddmFormValues, "rest", new ServiceContext());

		return ddmDataProviderInstance.getDataProviderInstanceId();
	}

	@Inject(
		filter = "ddm.data.provider.type=rest", type = DDMDataProvider.class
	)
	private DDMDataProvider _ddmDataProvider;

	@Inject(type = DDMDataProviderInstanceLocalService.class)
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

}