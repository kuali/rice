package edu.iu.uis.eden.messaging.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.resourceloader.SimpleServiceLocator;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

public class ServicesOverrideLifecycle extends BaseLifecycle {
	
	private static final Logger LOG = Logger.getLogger(ServicesOverrideLifecycle.class);
	
	private List<ServiceHolder> overrideServices;
	
	public ServicesOverrideLifecycle(List<ServiceHolder> overrideServices) {
		this.setOverrideServices(overrideServices);
	}
	
	public void start() throws Exception {
		if (this.getOverrideServices() == null) {
			return;
		}
		SimpleServiceLocator locator = (SimpleServiceLocator)KSBResourceLoaderFactory.getRootResourceLoader().getServiceLocator();
		for (ServiceHolder serviceHolder : this.getOverrideServices()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading override service " + serviceHolder.getServiceName() + " " + serviceHolder.getService());
			}
			locator.addService(serviceHolder.getServiceName(), serviceHolder.getService());
		}
		setStarted(true);
	}

	public List<ServiceHolder> getOverrideServices() {
		return this.overrideServices;
	}

	public void setOverrideServices(List<ServiceHolder> overrideServices) {
		this.overrideServices = overrideServices;
	}

	
}
