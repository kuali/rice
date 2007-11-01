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
package edu.iu.uis.eden.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;

import edu.iu.uis.eden.messaging.serviceconnectors.ServiceConnectorFactory;

/**
 * Holds the reference to an endpoint of a service as well as the {@link ServiceInfo}
 * that defines the service.  Provides lazy loading of services at call time.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemotedServiceHolder implements ServiceHolder {


    	private static final Logger LOG = Logger.getLogger(RemotedServiceHolder.class);

	private Object service;
	private ServiceInfo serviceInfo;

	public RemotedServiceHolder(ServiceInfo entry) {
		this.setServiceInfo(entry);
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(ServiceInfo entry) {
	    if (Core.getCurrentContextConfig().getDevMode()) {
		this.serviceInfo = cloneServiceInfo(entry);
	    } else {
		this.serviceInfo = entry;
	    }
	}

	public Object getService() throws Exception {
	    synchronized (this) {
		if (this.service == null) {
		    this.setService(ServiceConnectorFactory.getServiceConnector(serviceInfo).getService());
		}
	    }
	    return this.service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	/**
         * this is a hack so we don't mess with the {@link ServiceInfo} on the deployment side of things from the
         * {@link RemotedServiceRegistry}. We need the service in the {@link ServiceInfo} used on the client to be null but
         * it can't be for the server side stuff - solution serialize the object just like it was put in a datastore.
         *
         * @param serviceInfo
         * @return
         * @throws Exception
         */
    private static ServiceInfo cloneServiceInfo(final ServiceInfo serviceInfo) {

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
	} catch (Exception e) {
	    throw new RiceRuntimeException(e);
	} finally {

	    serviceInfo.getServiceDefinition().setService(tempService);
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException ioe) {
		    LOG.info("failed to close InputStream", ioe);
		}
	    }
	    if (out != null) {
		try {
		out.close();
		} catch (IOException ioe) {
		    LOG.info("Failed to close OutputStream", ioe);
		}
	    }

	}
    }
}