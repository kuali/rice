/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging.serviceconnectors;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.kuali.rice.ksb.messaging.RESTServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.security.soap.CredentialsOutHandler;
import org.kuali.rice.ksb.service.KSBServiceLocator;


/**
 * Connector (provider of client proxys) for RESTful services.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 0.9
 */
public class RESTConnector extends AbstractServiceConnector {
    
    public RESTConnector(final ServiceInfo serviceInfo) {
        super(serviceInfo);
    }


    /**
     * @return a CXF client proxy for web service corresponding to the ServiceInfo passed in on construction.
     * @see org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnector#getService()
     */
    public Object getService() throws Exception {
        Class<?> resourceClass = Class.forName(((RESTServiceDefinition) getServiceInfo().getServiceDefinition()).getResourceClass());

        JAXRSClientFactoryBean clientFactory;

        clientFactory = new JAXRSClientFactoryBean();
        clientFactory.setBus(KSBServiceLocator.getCXFBus());

        clientFactory.setResourceClass(resourceClass);
        clientFactory.setAddress(getServiceInfo().getActualEndpointUrl());
        BindingFactoryManager bindingFactoryManager = KSBServiceLocator.getCXFBus().getExtension(BindingFactoryManager.class);
        JAXRSBindingFactory bindingFactory = new JAXRSBindingFactory();
        bindingFactory.setBus(KSBServiceLocator.getCXFBus());

        bindingFactoryManager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, bindingFactory);

        //Set logging and security interceptors
        clientFactory.getOutInterceptors().add(new LoggingOutInterceptor());
//        clientFactory.getOutInterceptors().add(new SigningOutInterceptor());
        if (getCredentialsSource() != null) {
            clientFactory.getOutInterceptors().add(new CredentialsOutHandler(getCredentialsSource(), getServiceInfo()));
        }
        clientFactory.getInInterceptors().add(new LoggingInInterceptor());
//        clientFactory.getInInterceptors().add(new VerifyingInInterceptor());

        Object service = clientFactory.create();
        return getServiceProxyWithFailureMode(service, this.getServiceInfo());
    }	

}
