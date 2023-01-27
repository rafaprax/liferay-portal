package com.liferay.sample;

import org.osgi.service.component.annotations.Component;

@Component(
	property = "test.property=without.service",
	service = {}
)
public class SampleWithoutService implements Runnable{


	@Override
	public void run() {

	}
}
