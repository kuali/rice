/*
 * Copyright 2006-2011 The Kuali Foundation
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.messaging.AsynchronousCallback;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.kuali.rice.ksb.messaging.serviceproxies.AsynchronousServiceCallProxy;
import org.kuali.rice.ksb.messaging.serviceproxies.DelayedAsynchronousServiceCallProxy;
import org.kuali.rice.ksb.messaging.serviceproxies.SynchronousServiceCallProxy;
import org.kuali.rice.ksb.util.KSBConstants;


public class MessageHelperImpl implements MessageHelper {

    private static final Logger LOG = Logger.getLogger(MessageHelperImpl.class);

    public String serializeObject(Serializable object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
        } catch (IOException e) {
            throw new RiceRuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LOG.error("Failed to close ObjectOutputStream", e);
            }
        }
        byte[] buf = bos.toByteArray();
        Base64 b64 = new Base64();
        byte[] encodedObj = b64.encode(buf);
        return new String(encodedObj);
    }

    public Object deserializeObject(String serializedObject) {
        if (serializedObject == null) {
            return serializedObject;
        }
        Base64 b64 = new Base64();
        byte[] result = b64.decode(serializedObject.getBytes());
        Object payload = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(result));
            payload = ois.readObject();
        } catch (Exception e) {
            // may want to move this loggging up
            LOG.error("Caught Error de-serializing message payload", e);
            // throw new RiceRuntimeException(e);
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                LOG.error("Failed to close de-serialization stream", e);
            }
        }
        return payload;
    }

    public Object getServiceAsynchronously(QName qname) {
        return getAsynchronousServiceCallProxy(qname, null, null, null, null);
    }

    public Object getServiceAsynchronously(QName qname, AsynchronousCallback callback) {
        return getAsynchronousServiceCallProxy(qname, callback, null, null, null);
    }

    public Object getServiceAsynchronously(QName qname, AsynchronousCallback callback, Serializable context) {
        return getAsynchronousServiceCallProxy(qname, callback, context, null, null);
    }

    public Object getServiceAsynchronously(QName qname, AsynchronousCallback callback, Serializable context, String value1, String value2) {
        return getAsynchronousServiceCallProxy(qname, callback, context, value1, value2);
    }

    public Object getAsynchronousServiceCallProxy(QName qname, AsynchronousCallback callback, Serializable context, String value1, String value2) {

    	List<Endpoint> endpoints = KsbApiServiceLocator.getServiceBus().getEndpoints(qname);
    	if (endpoints.isEmpty()) {
    		throw new RuntimeException("Cannot create service proxy, failed to locate any endpoints with the given service name: " + qname);
    	}
        if (KSBConstants.MESSAGING_SYNCHRONOUS.equals(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.MESSAGE_DELIVERY))) {
            return SynchronousServiceCallProxy.createInstance(endpoints, callback, context, value1, value2);
        }

        return AsynchronousServiceCallProxy.createInstance(endpoints, callback, context, value1, value2);

    }

    public Object getDelayedAsynchronousServiceCallProxy(QName qname, Serializable context, String value1, String value2, long delayMilliseconds) {
    	List<Endpoint> endpoints = KsbApiServiceLocator.getServiceBus().getEndpoints(qname);
    	if (endpoints.isEmpty()) {
    		throw new RuntimeException("Cannot create service proxy, failed to locate any endpoints with the given service name: " + qname);
    	}
        if (KSBConstants.MESSAGING_SYNCHRONOUS.equals(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.MESSAGE_DELIVERY))) {
            LOG.warn("Executing a delayed service call for " + qname + " with delay of " + delayMilliseconds + " in synchronous mode.  Service will be invoked immediately.");
            return SynchronousServiceCallProxy.createInstance(endpoints, null, context, value1, value2);
        }
        return DelayedAsynchronousServiceCallProxy.createInstance(endpoints, context, value1, value2, delayMilliseconds);
    }

    public Object getServiceAsynchronously(QName qname, Serializable context, String value1, String value2, long delayMilliseconds) {
        return getDelayedAsynchronousServiceCallProxy(qname, context, value1, value2, delayMilliseconds);
    }

}
