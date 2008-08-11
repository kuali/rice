/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.ConfigProperties;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This class is the JstlContants implementation of the ServletContextListener.
 */
public class JstlConstantsInitListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(JstlConstantsInitListener.class);

    public void contextInitialized(ServletContextEvent sce) {

        LOG.info("Starting " + JstlConstantsInitListener.class.getName() + "...");
        ServletContext context = sce.getServletContext();

	// publish application constants into JSP app context with name "Constants"
	context.setAttribute("Constants", new KNSConstants());

        // publish configuration properties into JSP app context with name "ConfigProperties"
        context.setAttribute("ConfigProperties", new ConfigProperties());

        // publish dataDictionary property Map into JSP app context with name "DataDictionary"
        context.setAttribute("DataDictionary", KNSServiceLocator.getDataDictionaryService().getDataDictionaryMap());

        // public AuthorizationConstants property Map into JSP app context with name "AuthorizationConstants"
        context.setAttribute("AuthorizationConstants", new AuthorizationConstants());

        // publish property constants into JSP app context with name "PropertyConstants"
        context.setAttribute("PropertyConstants", new KNSPropertyConstants());
    }

    public void contextDestroyed(ServletContextEvent sce) {}

}