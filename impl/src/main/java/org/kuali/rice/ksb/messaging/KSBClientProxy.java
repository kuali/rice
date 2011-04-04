/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

/**
 * This class creates a proxy for services deployed on KSB. A 
 * reference to the service is obtained only upon the first method
 * invocation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KSBClientProxy implements InvocationHandler {

private static final Logger LOG = Logger.getLogger(KSBClientProxy.class);
    
    QName serviceName;
    Object service;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String serviceQName, Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, new KSBClientProxy(serviceQName));
    }

    public KSBClientProxy(String serviceQName){
        this.serviceName = QName.valueOf(serviceQName);
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.service == null){
            LOG.info("Getting service using GRL for: " + serviceName);
            service = GlobalResourceLoader.getService(serviceName);
            LOG.info("Obtained service using GRL for: " + serviceName);
        }
        try {
	        return method.invoke(service, args);
	    } catch (InvocationTargetException e) {
	        throw e.getCause();
	    }
    }

}
