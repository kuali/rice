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
package edu.iu.uis.eden.messaging.serviceexporters;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.spring.remoting.XFireExporter;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JInHandler;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JOutHandler;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.SOAPServiceDefinition;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.bam.BAMServerProxy;

/**
 * 
 * @author rkirkend
 * @author natjohns
 */
public class SOAPServiceExporter implements ServiceExporter {

	private ServiceInfo serviceInfo;

	public SOAPServiceExporter(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
		try {
			makeXFireGood();
			XFireExporter serviceExporter = new XFireExporter();
//			ServiceFactory servicefactory = (ServiceFactory) ((SpringResourceLoader)GlobalResourceLoader.getResourceLoader(new QName(Core.getCurrentContextConfig().getMessageEntity(), KSBConstants.KSB_RESOURCE_LOADER_NAME))).getContext().getBean("xfire.serviceFactory");
//			serviceExporter.setServiceFactory(servicefactory);
			serviceExporter.setServiceFactory(KSBServiceLocator.getXFireServiceFactory());
			serviceExporter.setXfire(KSBServiceLocator.getXFire());
			serviceExporter.setName(getServiceInfo().getQname().toString());
			serviceExporter.setServiceBean(serviceImpl);
			//when xfire supports service by service level securing
//			serviceExporter.getInHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
//			serviceExporter.getInHandlers().add(new WorkflowXFireWSS4JInHandler());
//			serviceExporter.getOutHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
//			serviceExporter.getOutHandlers().add(new WorkflowXFireWSS4JOutHandler());
//			serviceExporter.getFaultHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
//			serviceExporter.getFaultHandlers().add(new WorkflowXFireWSS4JOutHandler());			
			SOAPServiceDefinition serviceDef = (SOAPServiceDefinition) getServiceInfo().getServiceDefinition();
			serviceExporter.setServiceClass(Class.forName(serviceDef.getServiceInterface()));
			serviceExporter.afterPropertiesSet();
			return new ServerSideRemotedServiceHolder(wrapExporterInBamService(serviceExporter), this.serviceInfo.getServiceDefinition().getService(), getServiceInfo());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Object wrapExporterInBamService(XFireExporter serviceExporter) {
		return BAMServerProxy.wrap(serviceExporter, this.getServiceInfo());
	}

	@SuppressWarnings("unchecked")
	private void makeXFireGood() throws Exception {
		if (!isXFireSecure()) {
			XFire xfire = KSBServiceLocator.getXFire();

			List inHandlers = new ArrayList();
			inHandlers.add(new org.codehaus.xfire.util.dom.DOMInHandler());
			inHandlers.add(new org.codehaus.xfire.util.LoggingHandler());
			inHandlers.add(new WorkflowXFireWSS4JInHandler());			
			xfire.getInHandlers().addAll(inHandlers);
			
			List outHandlers = new ArrayList();
			outHandlers.add(new org.codehaus.xfire.util.dom.DOMOutHandler());
			outHandlers.add(new org.codehaus.xfire.util.LoggingHandler());
			outHandlers.add(new WorkflowXFireWSS4JOutHandler());
			xfire.getOutHandlers().addAll(outHandlers);

			List faultHandlers = new ArrayList();
			faultHandlers.add(new org.codehaus.xfire.util.dom.DOMOutHandler());
			faultHandlers.add(new org.codehaus.xfire.util.LoggingHandler());
			faultHandlers.add(new WorkflowXFireWSS4JOutHandler());
			xfire.getFaultHandlers().addAll(faultHandlers);
			
			setXFireSecure(true);
		}
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	//setting this using the config stuff so we're good with ccl's and the testing env (or whever)
	//this will go away when we can do service by service securing with xfire (should be next upgrade)
	private static final String XFIRE_SECURE_STRING = ".XFIRE_SECURE";
	public static boolean isXFireSecure() {
		return new Boolean(Core.getCurrentContextConfig().getProperty(XFIRE_SECURE_STRING));
	}
	
	public static void setXFireSecure(Boolean secure) {
		Core.getCurrentContextConfig().overrideProperty(XFIRE_SECURE_STRING, secure.toString());
	}
}