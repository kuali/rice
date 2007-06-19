package edu.iu.uis.eden.messaging.objectremoting;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.config.ServiceBasedServiceDefinitionRegisterer;

public class ObjectRemoterServiceImpl extends BaseLifecycle implements ObjectRemoterService {

	private static final Logger LOG = Logger.getLogger(ObjectRemoterServiceImpl.class);

	private ServiceBasedServiceDefinitionRegisterer defRegisterer;
	private static long counter = 0;

	public ServiceInfo getRemotedClassURL(ObjectDefinition objectDefinition) {
		LOG.debug("Looking for object " + objectDefinition.getClassName());
		objectDefinition.setAtRemotingLayer(true);
		Object target = GlobalResourceLoader.getResourceLoader().getObject(objectDefinition);
		//make sure that we have the class
		if (target == null) {
			LOG.debug("Didn't find object " + objectDefinition);
			return null;
		}

		LOG.debug("Found object " + objectDefinition);
		JavaServiceDefinition serviceDefinition = new JavaServiceDefinition();
		serviceDefinition.setLocalServiceName(objectDefinition.getClassName() + counter++);
		serviceDefinition.setService(target);
		serviceDefinition.validate();
		
		KSBServiceLocator.getServiceDeployer().registerTempService(serviceDefinition, target);
        serviceDefinition.setService(null);
		return new ServiceInfo(serviceDefinition);
	}
	
	public void removeService(QName serviceName) {
		LOG.debug("Removing service " + serviceName + " from message entity" + Core.getCurrentContextConfig().getMessageEntity());
		KSBServiceLocator.getServiceDeployer().removeRemoteServiceFromRegistry(serviceName);
	}

	public void start() throws Exception {
	    this.defRegisterer = new ServiceBasedServiceDefinitionRegisterer("ksb.objectRemoterServiceDefinition");
	    this.defRegisterer.registerServiceDefinition(false);
		setStarted(true);
	}

	public void stop() throws Exception {
	    this.defRegisterer.unregisterServiceDefinition();
		setStarted(false);
	}
}