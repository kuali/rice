package edu.iu.uis.eden.messaging;

import java.lang.reflect.Method;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.lifecycle.BaseLifecycle;

import edu.iu.uis.eden.messaging.config.ServiceBasedServiceDefinitionRegisterer;

public class RepeatTopicInvokerQueueImpl extends BaseLifecycle implements RepeatTopicInvokerQueue {

	private ServiceBasedServiceDefinitionRegisterer defRegisterer;
	
	public Object invokeTopic(AsynchronousCall methodCall) {
		ServiceInfo serviceInfo = methodCall.getServiceInfo();
		Object service = KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceInfo.getQname());
		try {
			Method method = service.getClass().getMethod(methodCall.getMethodName(), methodCall.getParamTypes());
			return method.invoke(service, methodCall.getArguments());
		} catch (Throwable t) {
			throw new RiceRuntimeException("Caught Exception invoking repeatable topic", t);
		}
	}
	
	public void start() throws Exception {
	    this.defRegisterer = new ServiceBasedServiceDefinitionRegisterer("repeatTopicInvokerQueueDefinition");
	    this.defRegisterer.registerServiceDefinition(false);
		setStarted(true);
	}

	public void stop() throws Exception {
	    this.defRegisterer.unregisterServiceDefinition();
		setStarted(false);
	}
}
