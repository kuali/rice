package edu.iu.uis.eden.server;

import javax.servlet.ServletContextEvent;

import org.kuali.rice.core.Core;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class KSBTestContextLoaderListener extends ContextLoaderListener {
	
	 
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		String testClientName = event.getServletContext().getInitParameter("test.client.spring.context.name");
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		Core.getCurrentContextConfig().getObjects().put(testClientName, appContext);
	}
}