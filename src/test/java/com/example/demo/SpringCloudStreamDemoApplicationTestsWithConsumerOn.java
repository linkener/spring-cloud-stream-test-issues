package com.example.demo;

/*
 * This test fails, the consumer receives half the messages it is supposed to
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import com.example.demo.SpringCloudStreamDemoApplication.Thing;
import com.example.demo.SpringCloudStreamDemoApplication.ThingController;

@SpringBootTest(properties = {
		"spring.cloud.stream.bindings.thingCreatedEventListener-in-0.destination=thing-created"
		})
@Import(TestChannelBinderConfiguration.class)
class SpringCloudStreamDemoApplicationTestsWithConsumerOn {

	@Autowired ThingController thingController;

	@Test
	void test01() throws Exception{
		SpringCloudStreamDemoApplication.messages.set(0);
		int nCalls = 64;
		for(int i=0; i<nCalls; i++)
			thingController.createThing(new Thing("a thing"));
		Thread.sleep(1000);
		assertEquals(nCalls, SpringCloudStreamDemoApplication.messages.get());
	}
}
