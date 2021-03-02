package com.example.demo;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import com.example.demo.SpringCloudStreamDemoApplication.Thing;
import com.example.demo.SpringCloudStreamDemoApplication.ThingController;
import com.fasterxml.jackson.databind.ObjectMapper;


/* 
 * with config 1 (line 22 uncommented, line 24 commented), the test fails
 * with config 2 (line 22 commented, line 24 uncommented), the test passes 
 */

@SpringBootTest(properties = {
		""
		//config 1, consumer is on
		,"spring.cloud.stream.bindings.thingCreatedEventListener-in-0.destination=thing-created"
		//config 2, consumer is off
		//,"spring.cloud.stream.bindings.thingCreatedEventListener-in-0.destination="
		})

@Import(TestChannelBinderConfiguration.class)
class SpringCloudStreamDemoApplicationTestsWithConsumerOff {

	@Autowired ThingController thingController;

	@Autowired ObjectMapper om;
	@Autowired OutputDestination output;
	
	@Test
	void test01() throws Exception{
		int nCalls = 64;
		output.clear();
		for(int i=0; i<nCalls; i++) {
			Thing thingSent = thingController.createThing(new Thing("a thing"));
			Message<byte[]> message = output.receive(1000, "thing-created");
			assertNotNull(message);
			Thing thingReceived = om.readValue(message.getPayload(), Thing.class);
			assertEquals(thingSent, thingReceived);
		}
	}	
}
