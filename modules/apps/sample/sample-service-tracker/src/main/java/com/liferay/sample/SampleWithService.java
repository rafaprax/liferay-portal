package com.liferay.sample;

import org.osgi.service.component.annotations.Component;

@Component(
	property = "test.property=test",
	service = SampleWithService.class
)
public class SampleWithService {
}
