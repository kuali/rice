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
package org.kuali.notification.services.ws.impl;

import junit.framework.TestCase;

import org.apache.axis.transport.http.AxisServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * Base class for testing the web service that KEN exposes.
 * @author Aaron Hamid (arh 14 at cornell dot edu)
 */
public abstract class NotificationWebServiceTestCaseBase extends TestCase {
    protected Server server;

    protected boolean shouldStartWebService() {
        return true;
    }

    protected int getWebServicePort() {
        return 8585;
    }

    protected String getWebServiceHost() {
        return "localhost";
    }

    protected String getWebServiceURL() {
        return "http://" + getWebServiceHost() + ":" + getWebServicePort() + "/notification/services/Notification";
    }

    protected ApplicationContext getContext() {
        //return applicationContext;
        return new ClassPathXmlApplicationContext("test-spring.xml");
    }

    protected Server createWebServiceServer(int port) {
        // took forever to find this freaking property: http://ws.apache.org/axis/java/integration-guide.html
        //System.setProperty("axis.ServerConfigFile", "/server-config.wsdd");

        Server server = new Server(port);
        
        // register the Spring application context in the servlet context as would the ContextLoaderListener, so our ServletEndpointSupport can find it
        Context context = new Context(server, "/notification", Context.SESSIONS);
        GenericWebApplicationContext wac = new GenericWebApplicationContext();
        wac.setParent(getContext());
        context.getServletContext().setAttribute("contextConfigLocation", "");
        wac.setServletContext(context.getServletContext());
        context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

        context.setResourceBase("work/wstestcase-tmp");
        // AxisServlet will load config from server-config.wsdd, which is already present and configured in Notification class loader
        context.addServlet(AxisServlet.class, "/services/*");
        
        return server;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (shouldStartWebService()) {
            server = createWebServiceServer(getWebServicePort());
            server.start();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
        super.tearDown();
    }
}