package edu.sampleu.travel.infrastructure;

import org.kuali.core.util.spring.ClassPathXmlApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class TravelServiceLocator {
	
	private static ConfigurableApplicationContext appContext;

	private static void initialize() throws Exception {
		if (appContext == null) {
			appContext = new ClassPathXmlApplicationContext("classpath:SpringBeans.xml");
		}
	}
	
	
	
}
