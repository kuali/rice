package edu.iu.uis.eden.messaging.serviceproxies;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;

import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.AsynchronousCallback;
import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;

/**
 * Used to Call a service synchronously but through the messaging code within workflow.  Used to when switching 
 * generally asynchronously called services to synchronously called services.  Generally for testing purposes.
 * 
 * @author rkirkend
 *
 */
public class SynchronousServiceCallProxy extends AsynchronousServiceCallProxy {

	private SynchronousServiceCallProxy(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback, Serializable context, Date deliveryDate, Long repeatCallTimeIncrement) {
		super(serviceDefs, callback, context, deliveryDate, repeatCallTimeIncrement);
	}
	
	public static Object createInstance(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback, Serializable context, Date deliveryDate, Long repeatCallTimeIncrement) {
		if (serviceDefs == null || serviceDefs.isEmpty()) {
			throw new RuntimeException("Cannot create service proxy, no service(s) passed in.");
		}
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy.getInterfacesToProxyIncludeSpring(serviceDefs.get(0).getService()), new SynchronousServiceCallProxy(serviceDefs, callback, context, deliveryDate, repeatCallTimeIncrement));
	}

	@Override
	protected void saveMessage(PersistedMessage message) {
		new MessageServiceInvoker(message).run();
	}
}