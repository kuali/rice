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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.messaging.RemotedServiceHolder;
import edu.iu.uis.eden.messaging.RemotedServiceRegistry;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.bam.BAMClientProxy;

/**
 * 
 * @author rkirkend
 */
public class BusLocalConnector extends AbstractServiceConnector {

	public BusLocalConnector(final ServiceInfo serviceInfo) {
		super(serviceInfo);
	}

	public RemotedServiceHolder getServiceHolder() throws Exception {
		final Object service = getServiceProxy(KSBServiceLocator.getServiceDeployer().getLocalService(getServiceInfo().getQname()));
		return new RemotedServiceHolder(service, cloneServiceInfo(getServiceInfo()));
	}

	private Object getServiceProxy(Object service) {
		return BAMClientProxy.wrap(service, getServiceInfo());
	}

	/**
	 * this is a hack so we don't mess with the {@link ServiceInfo} on the deployment 
	 * side of things from the {@link RemotedServiceRegistry}.  We need the service in the {@link ServiceInfo} 
	 * used on the client to be null but it can't be for the server side stuff - solution serialize the 
	 * object just like it was put in a datastore.
	 * 
	 * @param serviceInfo
	 * @return
	 * @throws Exception
	 */
	private ServiceInfo cloneServiceInfo(final ServiceInfo serviceInfo) throws Exception {

		ObjectInputStream in = null;
		ObjectOutput out = null;
		Object tempService = serviceInfo.getServiceDefinition().getService();

		try {
			serviceInfo.getServiceDefinition().setService(null);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(serviceInfo);

			in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			return (ServiceInfo) in.readObject();
		} finally {
			serviceInfo.getServiceDefinition().setService(tempService);
			in.close();
			out.close();
		}
	}
}