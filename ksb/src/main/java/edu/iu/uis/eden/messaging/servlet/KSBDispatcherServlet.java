/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import edu.iu.uis.eden.security.SignatureSigningResponseWrapper;
import edu.iu.uis.eden.security.SignatureVerifyingRequestWrapper;

/**
 * A {@link DispatcherServlet} which dispatches incoming requests to the appropriate
 * service endpoint.
 *
 * @author rkirkend
 * @author ewestfal
 * @author natjohns
 */
public class KSBDispatcherServlet extends DispatcherServlet {

	private static final Logger LOG = Logger.getLogger(KSBDispatcherServlet.class);
	
	private static final long serialVersionUID = 6790121225857950019L;
	private KSBHttpInvokerHandler httpInvokerHandler;
	
	/**
	 * Instantiate the WebApplicationContext for this servlet, either a default
	 * XmlWebApplicationContext or a custom context class if set. This implementation
	 * expects custom contexts to implement ConfigurableWebApplicationContext.
	 * Can be overridden in subclasses.
	 * @param parent the parent ApplicationContext to use, or <code>null</code> if none
	 * @return the WebApplicationContext for this servlet
	 * @throws BeansException if the context couldn't be initialized
	 * @see #setContextClass
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected WebApplicationContext initWebApplicationContext() throws BeansException {
		return null;//we want to start spring all by ourselves
	}

	protected void initFrameworkServlet() throws ServletException, BeansException {
		this.httpInvokerHandler = new KSBHttpInvokerHandler();
		this.setPublishEvents(false);
	}
	
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (handler instanceof HttpRequestHandler) {
			return new HttpRequestHandlerAdapter();	
		} else if (handler instanceof Controller) {
			return new SimpleControllerHandlerAdapter();
		}
		throw new RiceRuntimeException("handler of type " + handler.getClass().getName() + " is not known and can't be used by " + KSBDispatcherServlet.class.getName());
	}
	
	/**
	 * Return the HandlerExecutionChain for this request.
	 * Try all handler mappings in order.
	 * @param request current HTTP request
	 * @param cache whether to cache the HandlerExecutionChain in a request attribute
	 * @return the HandlerExceutionChain, or <code>null</code> if no handler could be found
	 */
	protected HandlerExecutionChain getHandler(HttpServletRequest request, boolean cache) throws Exception {
		return this.httpInvokerHandler.getHandler(request);
	}

	@Override
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		try {
			QName serviceName = this.httpInvokerHandler.getServiceNameFromRequest(request);
			LOG.info("Caught Exception from service " + serviceName, ex);
		} catch (Throwable throwable) {
			LOG.warn("Caught exception attempting to log exception thrown from remotely accessed service", throwable);
		}
		return null;
	}

	/**
	 * Overrides the service method to replace the request and responses with one which will provide input and output streams for
	 * verifying and signing the data.
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (isSecure(request)) {
			super.service(new SignatureVerifyingRequestWrapper(request), new SignatureSigningResponseWrapper(response));
		} else {
			super.service(request, response);
		}
	}
	
	protected boolean isSecure(HttpServletRequest request) {
		QName serviceName = this.httpInvokerHandler.getServiceNameFromRequest(request);
		return KSBServiceLocator.getServiceDeployer().getRemotedServiceHolder(serviceName).getServiceInfo().getServiceDefinition().getBusSecurity();
	}
}