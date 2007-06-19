package edu.iu.uis.eden.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;
import edu.iu.uis.eden.messaging.serviceproxies.AsynchronousServiceCallProxy;
import edu.iu.uis.eden.messaging.serviceproxies.SynchronousServiceCallProxy;

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
//			throw new RiceRuntimeException(e);
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

	public Object getServiceAsynchronously(QName qname, Date deliveryDate) {
		return getAsynchronousServiceCallProxy(qname, null, null, deliveryDate, null);
	}

	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback) {
		return getAsynchronousServiceCallProxy(qname, callback, null, deliveryDate, null);
	}

	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback, TimeUnit repeatTimeUnit, Long repeatDelay) {
		Long millisRepeatIncrement = repeatTimeUnit.toMillis(repeatDelay);
		return getAsynchronousServiceCallProxy(qname, callback, null, deliveryDate, millisRepeatIncrement);
	}
	
	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback, TimeUnit repeatTimeUnit, Long repeatDelay, Serializable context) {
		Long millisRepeatIncrement = null;
		if (repeatTimeUnit != null) {
			millisRepeatIncrement = repeatTimeUnit.toMillis(repeatDelay);
		}
		return getAsynchronousServiceCallProxy(qname, callback, context, deliveryDate, millisRepeatIncrement);
	}

	public Object getAsynchronousServiceCallProxy(QName qname, AsynchronousCallback callback, Serializable context, Date deliveryDate, Long repeatCallTimeIncrement) {
		List<RemotedServiceHolder> servicesToProxy = KSBResourceLoaderFactory.getRemoteResourceLocator().getAllServices(qname);
		if (RiceConstants.MESSAGING_SYNCHRONOUS.equals(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGE_PERSISTENCE))) {
			return SynchronousServiceCallProxy.createInstance(servicesToProxy, callback, context, deliveryDate, repeatCallTimeIncrement);
		}
		
			return AsynchronousServiceCallProxy.createInstance(servicesToProxy, callback, context, deliveryDate, repeatCallTimeIncrement);
		}
	}
