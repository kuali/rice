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

import org.codehaus.xfire.spring.remoting.XFireExporter;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JInHandler;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JOutHandler;

import edu.iu.uis.eden.messaging.SOAPServiceDefinition;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.bam.BAMServerProxy;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SOAPServiceExporter implements ServiceExporter {

	private ServiceInfo serviceInfo;

	public SOAPServiceExporter(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
		try {
			XFireExporter serviceExporter = new XFireExporter();
			serviceExporter.setServiceFactory(KSBServiceLocator.getXFireServiceFactory());
			serviceExporter.setXfire(KSBServiceLocator.getXFire());
			serviceExporter.setName(getServiceInfo().getQname().toString());
			serviceExporter.setServiceBean(serviceImpl);
			//when xfire supports service by service level securing
			serviceExporter.setInHandlers(new ArrayList());
			serviceExporter.getInHandlers().add(new DOMInHandler());
			serviceExporter.getInHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
			serviceExporter.getInHandlers().add(new WorkflowXFireWSS4JInHandler(serviceInfo));
			serviceExporter.setOutHandlers(new ArrayList());
			serviceExporter.getOutHandlers().add(new DOMOutHandler());
			serviceExporter.getOutHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
			serviceExporter.getOutHandlers().add(new WorkflowXFireWSS4JOutHandler(serviceInfo));
			serviceExporter.setFaultHandlers(new ArrayList());
			serviceExporter.getFaultHandlers().add(new DOMOutHandler());
			serviceExporter.getFaultHandlers().add(new org.codehaus.xfire.util.LoggingHandler());
			serviceExporter.getFaultHandlers().add(new WorkflowXFireWSS4JOutHandler(serviceInfo));
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

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

}