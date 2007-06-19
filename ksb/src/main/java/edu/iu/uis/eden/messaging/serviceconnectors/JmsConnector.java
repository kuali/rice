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
package edu.iu.uis.eden.messaging.serviceconnectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;

import org.kuali.bus.security.jms.AuthenticationJmsProxyFactoryBean;
import org.logicblaze.lingo.SimpleMetadataStrategy;
import org.logicblaze.lingo.jms.JmsProxyFactoryBean;

import edu.iu.uis.eden.messaging.JmsServiceDefinition;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * 
 * @author rkirkend
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 */
public class JmsConnector extends AbstractServiceConnector {

	public JmsConnector(final ServiceInfo serviceInfo) {
		super(serviceInfo);
	}

	public RemotedServiceHolder getServiceHolder() throws Exception {
		final JmsServiceDefinition serviceDef = (JmsServiceDefinition) getServiceInfo().getServiceDefinition();
		final JmsProxyFactoryBean factoryBean = getCredentialsSource() != null ? new AuthenticationJmsProxyFactoryBean(getCredentialsSource(), getServiceInfo()) : new JmsProxyFactoryBean();
		
		factoryBean.setServiceInterface(Class.forName(serviceDef.getServiceInterface()));
		factoryBean.setConnectionFactory(serviceDef.getConnectionFactory());
		factoryBean.setDestination(getDestination(serviceDef.getConnectionFactory(), serviceDef.getServiceInterface()));
		factoryBean.setMetadataStrategy(new SimpleMetadataStrategy(true));
		factoryBean.afterPropertiesSet();
		return new RemotedServiceHolder(getServiceProxyWithFailureMode(factoryBean.getObject(), getServiceInfo()), getServiceInfo());
	}

	protected Destination getDestination(final ConnectionFactory connectionFactory, final String queueName) throws Exception {
		final Connection connection = connectionFactory.createConnection();
		final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(queueName);
		return queue;
	}
}
