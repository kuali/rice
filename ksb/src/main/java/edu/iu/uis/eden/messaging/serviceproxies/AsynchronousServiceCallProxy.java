package edu.iu.uis.eden.messaging.serviceproxies;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.proxy.BaseInvocationHandler;
import org.kuali.rice.proxy.TargetedInvocationHandler;
import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.AsynchronousCallback;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Standard default proxy used to call services asynchronously. Persists the
 * method call to the db so call is never lost and only sent when transaction is
 * committed.
 * 
 * @author rkirkend
 * 
 */
public class AsynchronousServiceCallProxy extends BaseInvocationHandler implements TargetedInvocationHandler {

	private static final Logger LOG = Logger.getLogger(AsynchronousServiceCallProxy.class);

	private AsynchronousCallback callback;

	private List<RemotedServiceHolder> serviceDefs;

	private Date deliveryDate;

	private Serializable context;

	private Long repeatCallTimeIncrement;

	private boolean topicService;

	protected AsynchronousServiceCallProxy(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback, Serializable context, Date deliveryDate, Long repeatCallTimeIncrement) {
		this.serviceDefs = serviceDefs;
		this.callback = callback;
		this.deliveryDate = deliveryDate;
		this.context = context;
		this.repeatCallTimeIncrement = repeatCallTimeIncrement;
	}

	public static Object createInstance(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback, Serializable context, Date deliveryDate, Long repeatCallTimeIncrement) {
		if (serviceDefs == null || serviceDefs.isEmpty()) {
			throw new RuntimeException("Cannot create service proxy, no service(s) passed in.");
		}
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy.getInterfacesToProxyIncludeSpring(serviceDefs.get(0).getService()), new AsynchronousServiceCallProxy(serviceDefs, callback, context,
				deliveryDate, repeatCallTimeIncrement));
	}

	@Override
	protected Object invokeInternal(Object proxy, Method method, Object[] arguments) throws Throwable {
		// there are multiple service calls to make in the case of topics.
		AsynchronousCall methodCall = null;
		PersistedMessage message = null;
		synchronized (this) {
			// consider moving all this topic invocation stuff to the service
			// invoker for speed reasons
			for (RemotedServiceHolder remotedServiceHolder : this.serviceDefs) {
				ServiceInfo serviceInfo = remotedServiceHolder.getServiceInfo();
				methodCall = new AsynchronousCall(method.getParameterTypes(), arguments, serviceInfo, method.getName(), this.context, this.callback, this.repeatCallTimeIncrement);
				message = KSBServiceLocator.getRouteQueueService().getMessage(serviceInfo, methodCall, this.deliveryDate);
				saveMessage(message);
				// only do one iteration if this is a queue. The load balancing
				// will be handled when the service is
				// fetched by the MessageServiceInvoker through the GRL (and
				// then through the RemoteResourceServiceLocatorImpl)
				if (serviceInfo.getServiceDefinition().getQueue()) {
					break;
				}
				this.topicService = true;
			}
		}

		if (this.repeatCallTimeIncrement != null && this.topicService) {
			throw new UnsupportedOperationException("repeating topic invocation not supported.  get quartz.");
//			QName repeatTopicInvokingQueueName = new QName("KEW", "RepeatTopicInvokerQueue");
//			// the above work will send the first message. Offset the next send
//			// of the topic by the time increment of the repeated call
//			Timestamp deliveryDate = new Timestamp(message.getQueueDate().getTime() + this.repeatCallTimeIncrement);
//			RepeatTopicInvokerQueue repeatTopicInvokingQueue = (RepeatTopicInvokerQueue) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(repeatTopicInvokingQueueName, deliveryDate, null, TimeUnit.MILLISECONDS,
//					this.repeatCallTimeIncrement);
//			repeatTopicInvokingQueue.invokeTopic(methodCall);
		}

		return null;
	}



	protected void saveMessage(PersistedMessage message) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persisting Message " + message);
		}
		KSBServiceLocator.getRouteQueueService().save(message);
	}

	/**
	 * Returns the List<RemotedServiceHolder> of asynchronous services which
	 * will be invoked by calls to this proxy. This is a List because, in the
	 * case of Topics, there can be more than one service invoked.
	 */
	public Object getTarget() {
		return this.serviceDefs;
	}

	public AsynchronousCallback getCallback() {
		return this.callback;
	}

	public void setCallback(AsynchronousCallback callback) {
		this.callback = callback;
	}

	public List<RemotedServiceHolder> getServiceDefs() {
		return this.serviceDefs;
	}

	public void setServiceDefs(List<RemotedServiceHolder> serviceDefs) {
		this.serviceDefs = serviceDefs;
	}

	public Date getDeliveryDate() {
		return this.deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Serializable getContext() {
		return this.context;
	}

	public void setContext(Serializable context) {
		this.context = context;
	}

}
