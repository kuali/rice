/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import edu.iu.uis.eden.messaging.RemotedServiceRegistry;

/**
 * A {@link HandlerMapping} which handles incoming HTTP requests from the bus.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KSBHttpInvokerHandler extends AbstractHandlerMapping {

    private static final Logger LOG = Logger.getLogger(KSBHttpInvokerHandler.class);

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
	QName serviceName = getServiceNameFromRequest(request);
	// load the servlet for handling JMX communication
	if (serviceName.equals(new QName(Core.getCurrentContextConfig().getMessageEntity(), "jmx"))) {
	    return GlobalResourceLoader.getService("jmxWrappingController");
	}
	return ((RemotedServiceRegistry) GlobalResourceLoader.getService("enServiceInvoker")).getService(serviceName);
    }

    public QName getServiceNameFromRequest(HttpServletRequest request) {
	try {
	    String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
	    String serviceName = URLDecoder.decode(lookupPath.substring(1, lookupPath.length()), "UTF-8");
	    //decode the decoded service name in case the client has encoded xfire's encoded name
	    if (serviceName.indexOf("%7B") > -1) {
		serviceName = URLDecoder.decode(serviceName, "UTF-8");
	    }
	    if (LOG.isDebugEnabled()) {
		LOG.debug("#############################################################");
		LOG.debug("Entering Message Entity " + Core.getCurrentContextConfig().getMessageEntity()
			+ ".  Looking up handler for [" + serviceName + "]");
		LOG.debug("#############################################################");
	    }
	    return QName.valueOf(serviceName);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	}
    }
}