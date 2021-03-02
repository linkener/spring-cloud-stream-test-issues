package com.example.demo;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class SpringCloudStreamDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamDemoApplication.class, args);
	}
	
	public static AtomicLong messages = new AtomicLong(0);
	
	@Bean
	public Consumer<Thing> thingCreatedEventListener(){
		return thing->{
			messages.incrementAndGet();
			log.info(thing);
		};
	}
	
	@Data
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class Thing {
		private String id;
		private String description;
		
		public Thing(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return String.format("[%s]: %s", id, description);
		}
	}
	
	@RestController
	@RequestMapping("/thing")
	public static class ThingController{
		@Autowired
		StreamBridge streamBridge;
		
		@PostMapping()
		public Thing createThing(@RequestBody Thing thing) {
			thing.setId(UUID.randomUUID().toString());
			streamBridge.send("thing-created", thing);
			return thing;
		}
		
	}

}
