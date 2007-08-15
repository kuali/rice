/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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