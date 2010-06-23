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
package org.kuali.rice.ksb.messaging;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.ksb.util.KSBConstants;

/**
 * Service definition for RESTful services.  A JAX-WS service has a resource class, which is the class or 
 * interface marked by the JAX-WS annotations (e.g. @Path, @GET, etc).  This may or may not be the implementation
 * class.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RESTServiceDefinition extends ServiceDefinition {

    private static final long serialVersionUID = 5892163789061959602L;

	private String resourceClass;

	/**
	 * Default constructor.  Sets bus security to FALSE.
	 */
	public RESTServiceDefinition() {
		super(Boolean.FALSE);
		
		Config config = ConfigContext.getCurrentContextConfig();
		if (config == null) {
		    // Kludge, at the point in Rice initialization for KSB unit tests where RESTServiceDefinitionS are first
		    // constructed, ConfigContext.getCurrentContextConfig() returns null.  The following worked.
		    config = ConfigContext.getConfig(RESTServiceDefinition.class.getClassLoader());
		}
		
		String restfulServicePath = config.getProperty(KSBConstants.Config.RESTFUL_SERVICE_PATH);

		super.setServicePath(restfulServicePath);
	}

	/**
	 * To ensure transparency that RESTful services are not digitally signed, throw an exception
	 * if someone tries to enable bus security.
	 * 
	 * @see org.kuali.rice.ksb.messaging.ServiceDefinition#setBusSecurity(java.lang.Boolean)
	 */
	@Override
	public void setBusSecurity(Boolean busSecurity) {
	    if (busSecurity == true) {
	        throw new RiceRuntimeException("Rice does not support bus security (digital request/response signing) " +
	        		"for RESTful services");
	    }
	    super.setBusSecurity(busSecurity);
	}
	
	/**
	 * overriding to prohibit changing of the service path
	 * 
	 * @see org.kuali.rice.ksb.messaging.ServiceDefinition#setServicePath(java.lang.String)
	 */
	@Override
	public void setServicePath(String servicePath) {
	    throw new UnsupportedOperationException("the "+ KSBConstants.Config.RESTFUL_SERVICE_PATH +
	            " configuration parameter sets the RESTServiceDefinition's service path, and can not be overridden");
	}
	
	/**
	 * Set the resourceClass, the class or interface marked by the JAX-WS annotations
	 * which specify the RESTful URL interface.
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(String resourceClass) {
		this.resourceClass = resourceClass;
	}
	
	/**
	 * @see #setResourceClass(String)
	 * @return the resourceClass
	 */
	public String getResourceClass() {
		return this.resourceClass;
	}
	
	/**
	 * sets the service implementation
	 * 
	 * @see org.kuali.rice.ksb.messaging.ServiceDefinition#setService(java.lang.Object)
	 */
	@Override
	public void setService(Object service) {
	    super.setService(service);
	}
	
	/**
	 * does some simple validation of this RESTServiceDefinition
	 * 
	 * @see org.kuali.rice.ksb.messaging.ServiceDefinition#validate()
	 */
	@Override
	public void validate() {

		super.validate();
		// if interface is null, set it to the service class
		if (getResourceClass() == null) {
		    throw new ConfigurationException(
            "resource class must be set to export a REST service");
		} 

		// Validate that the JAX-WS annotated class / interface is available to the classloader.
		try {
		    Class.forName(getResourceClass()); 
		} catch (ClassNotFoundException e) {
		    throw new ConfigurationException(
		            "resource class '" + getResourceClass() + "' could not be found in the classpath");
		}

		if (getBusSecurity() == null) {
			setBusSecurity(false);
		}
	}

	/**
	 * @return true if the given {@link RESTServiceDefinition} has the same resource class as this one. 
	 * @see org.kuali.rice.ksb.messaging.ServiceDefinition#isSame(org.kuali.rice.ksb.messaging.ServiceDefinition)
	 */
	@Override
	public boolean isSame(final ServiceDefinition serviceDefinition) {
		boolean same = super.isSame(serviceDefinition)
				&& serviceDefinition instanceof RESTServiceDefinition;
		if (!same) {
			return same;
		}
		return ((RESTServiceDefinition) serviceDefinition)
				.getResourceClass().equals(this.getResourceClass());
	}
}
