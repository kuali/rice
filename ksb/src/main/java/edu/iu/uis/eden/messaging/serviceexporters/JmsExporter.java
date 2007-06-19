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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;

import org.kuali.rice.exceptions.RiceRuntimeException;
import org.logicblaze.lingo.jms.JmsServiceExporter;

import edu.iu.uis.eden.messaging.JmsServiceDefinition;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.bam.BAMServerProxy;

/**
 * 
 * @author rkirkend
 */
public class JmsExporter implements ServiceExporter {

	private ServiceInfo serviceInfo;

	public JmsExporter(ServiceInfo serviceInfo) {
		this.setServiceInfo(serviceInfo);
	}

	public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
		try {
			ConnectionFactory connectionFactory = ((JmsServiceDefinition) this.serviceInfo.getServiceDefinition()).getConnectionFactory();
			String interfaceName = ((JmsServiceDefinition) this.serviceInfo.getServiceDefinition()).getServiceInterface();
			JmsServiceExporter jmsExporter = new JmsServiceExporter();
			jmsExporter.setService(wrapServiceInBamProxy(serviceImpl));
			jmsExporter.setServiceInterface(Class.forName(interfaceName));
			jmsExporter.setConnectionFactory(connectionFactory);
			jmsExporter.setDestination(getDestination(connectionFactory, interfaceName));
			jmsExporter.afterPropertiesSet();
			return new ServerSideRemotedServiceHolder(null, serviceImpl, this.serviceInfo);
		} catch (Exception e) {
			throw new RiceRuntimeException("Unable to configure service " + this.getServiceInfo(), e);
		}
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	protected Destination getDestination(ConnectionFactory connectionFactory, String queueName) throws Exception {
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue(queueName);
		return queue;
	}
	
	protected Object wrapServiceInBamProxy(Object service) {
		return BAMServerProxy.wrap(service, this.getServiceInfo());
	}
}