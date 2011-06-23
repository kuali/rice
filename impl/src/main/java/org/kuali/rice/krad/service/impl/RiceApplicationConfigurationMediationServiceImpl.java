/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.namespace.Namespace;
import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService;
import org.kuali.rice.krad.service.RiceApplicationConfigurationService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Transactional
public class RiceApplicationConfigurationMediationServiceImpl implements RiceApplicationConfigurationMediationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationMediationServiceImpl.class);
     
 // Max age defined in seconds
    protected int configurationParameterCacheMaxSize = 200;
    protected int configurationParameterCacheMaxAgeSeconds = 3600;
    protected int nonDatabaseComponentsCacheMaxSize = 50;
    protected int nonDatabaseComponentsCacheMaxAgeSeconds = 3600;

    protected final HashMap<String,MaxAgeSoftReference<String>> configurationParameterCache = new HashMap<String,MaxAgeSoftReference<String>>( configurationParameterCacheMaxSize );
    protected final HashMap<String,MaxAgeSoftReference<List<Component>>> nonDatabaseComponentsCache = new HashMap<String,MaxAgeSoftReference<List<Component>>>( nonDatabaseComponentsCacheMaxSize );
    protected final HashMap<String,MaxAgeSoftReference<RiceApplicationConfigurationService>> responsibleServiceByPackageClass = new HashMap<String,MaxAgeSoftReference<RiceApplicationConfigurationService>>( configurationParameterCacheMaxSize );
    
    public String getConfigurationParameter( String namespaceCode, String parameterName ){
    	
    	String parameterValue = null;
    	if ( namespaceCode != null ) {
    	    String parameterKey = (new StringBuffer(namespaceCode).append(SearchOperator.OR.op()).append(parameterName)).toString();
    	    parameterValue = getParameterValueFromConfigurationParameterCache( parameterKey );
    	    if ( parameterValue != null ) {
                return parameterValue;
            }
    	    NamespaceService nsService = KRADServiceLocatorInternal.getNamespaceService();
    	    final String applicationNamespaceCode;
    	    Namespace namespace = nsService.getNamespace(namespaceCode);
    	    if (namespace != null) {
    	        applicationNamespaceCode = namespace.getApplicationId();
    	    } else {
    	        applicationNamespaceCode = namespaceCode;
    	    }
			if (applicationNamespaceCode != null) {
				RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(applicationNamespaceCode);
				if (rac != null) {
					parameterValue = rac.getConfigurationParameter(parameterName);
				}
			}
			if (parameterValue != null){
				synchronized (configurationParameterCache) {
				    configurationParameterCache.put( parameterKey, new MaxAgeSoftReference<String>( configurationParameterCacheMaxAgeSeconds, parameterValue ) );
				}
			}
		}
    	return parameterValue;
    }
    

    protected String getParameterValueFromConfigurationParameterCache(String parameterKey) {
        MaxAgeSoftReference<String> parameterValue = configurationParameterCache.get( parameterKey );
        if ( parameterValue != null ) {
            return parameterValue.get();
        }
        return null;
    }
    
    protected List<Component> getComponentListFromNonDatabaseComponentsCache(String nonDatabaseServiceNameKey) {
        MaxAgeSoftReference<List<Component>> nonDatabaseComponent = nonDatabaseComponentsCache.get( nonDatabaseServiceNameKey );
        if ( nonDatabaseComponent != null ) {
            return nonDatabaseComponent.get();
        }
        return null;
    }

    public List<Component> getNonDatabaseComponents() {
    	
		// TODO I think the code that's below here will actually pull in more than
		// one reference to a particular application's config service if it is deployed
		// in a cluster, it needs to only pull a single RiceApplicationConfigurationService
		// implementation per application id.  Also, may want to consider load balancing
		// and failover in those cases?  It might be best to try and utilize the client-side
		// KSB proxies that handle a lot of this stuff for us
    	

    	Set<QName> serviceNames = findApplicationConfigurationServices();
		
		List<Component> nonDatabaseComponents = new ArrayList<Component>();
		//add cache per serviceName
		for ( QName serviceName : serviceNames ) {
		    List<Component> nonDatabaseComponentFromCache = this.getComponentListFromNonDatabaseComponentsCache(serviceName.toString());
	        if (nonDatabaseComponentFromCache != null) {
	            nonDatabaseComponents.addAll(nonDatabaseComponentFromCache);
	        } else {
    			RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(serviceName);
    			try {
        			if (rac != null) {
        				nonDatabaseComponents.addAll(rac.getNonDatabaseComponents());
        				synchronized (nonDatabaseComponentsCache) {
            	            nonDatabaseComponentsCache.put(serviceName.toString(), new MaxAgeSoftReference<List<Component>>( nonDatabaseComponentsCacheMaxAgeSeconds, rac.getNonDatabaseComponents() ));
    					}
        			}
    			} catch (Exception e) {
    			    //TODO : Need a better way to catch if service is not active (404 error)
    			    LOG.warn("Invalid RiceApplicationConfigurationService with name: " + serviceName + ".  ");
    			}
	        }
			
		}
		
		return nonDatabaseComponents;
    }
    
    protected Set<QName> findApplicationConfigurationServices() {
    	Set<QName> names = new HashSet<QName>();
    	List<Endpoint> allEndpoints = KsbApiServiceLocator.getServiceBus().getAllEndpoints();
    	for (Endpoint endpoint : allEndpoints) {
    		QName serviceName = endpoint.getServiceConfiguration().getServiceName();
    		if (serviceName.getLocalPart().equals(KRADServiceLocatorInternal.RICE_APPLICATION_CONFIGURATION_SERVICE)) {
    			names.add(serviceName);
    		}
    	}
    	return names;
    }
    
    protected RiceApplicationConfigurationService findRiceApplicationConfigurationService(QName serviceName) {
    	try {
    		return (RiceApplicationConfigurationService) GlobalResourceLoader.getService(serviceName);
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with name: " + serviceName,e);
    	}
    	return null;
    }
    
    protected RiceApplicationConfigurationService findRiceApplicationConfigurationService(String namespace) {
    	try {
    		return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(new QName(namespace, KRADServiceLocatorInternal.RICE_APPLICATION_CONFIGURATION_SERVICE));
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with namespace: " + namespace,e);
    	}
    	return null;
    }


    public void setConfigurationParameterCacheMaxSize(
            int configurationParameterCacheMaxSize) {
        this.configurationParameterCacheMaxSize = configurationParameterCacheMaxSize;
    }


    public void setConfigurationParameterCacheMaxAgeSeconds(
            int configurationParameterCacheMaxAgeSeconds) {
        this.configurationParameterCacheMaxAgeSeconds = configurationParameterCacheMaxAgeSeconds;
    }


    public void setNonDatabaseComponentsCacheMaxSize(
            int nonDatabaseComponentsCacheMaxSize) {
        this.nonDatabaseComponentsCacheMaxSize = nonDatabaseComponentsCacheMaxSize;
    }


    public void setNonDatabaseComponentsCacheMaxAgeSeconds(
            int nonDatabaseComponentsCacheMaxAgeSeconds) {
        this.nonDatabaseComponentsCacheMaxAgeSeconds = nonDatabaseComponentsCacheMaxAgeSeconds;
    }
    
    /**
     * Call each available service to see if it's responsible for the given package.  When found, cache the result
     * to prevent need for future service lookups for the same package.
     */
    protected RiceApplicationConfigurationService findServiceResponsibleForPackageOrClass( String packageOrClassName ) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "Checking for app config service responsible for: " + packageOrClassName );
    	}
    	RiceApplicationConfigurationService racService = null;
    	MaxAgeSoftReference<RiceApplicationConfigurationService> ref = responsibleServiceByPackageClass.get(packageOrClassName);
    	if ( ref != null ) {
    		racService = ref.get();
    		if ( racService != null ) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug( "Service found in cache: " + racService.getClass().getName() );
            	}    		    			
    		}
    	}
    	if ( racService == null ) {
        	Set<QName> serviceNames = findApplicationConfigurationServices();
			for ( QName serviceName : serviceNames ) {
				racService = findRiceApplicationConfigurationService(serviceName);
				if ( racService != null ) {
				
					try {
						if ( racService.isResponsibleForPackage(packageOrClassName) ) {
				        	if ( LOG.isDebugEnabled() ) {
				        		LOG.debug( "Found responsible class on bus with name: " + serviceName );
				        	}    		
							responsibleServiceByPackageClass.put(packageOrClassName, new MaxAgeSoftReference<RiceApplicationConfigurationService>( configurationParameterCacheMaxAgeSeconds, racService) );
							break;
						} else {
							racService = null; // null it out in case this is the last iteration
						}
					} catch (Exception e) {
						LOG.warn( "Assuming this racService is not responsible for the package or class.  racService: "  +
								racService.toString() + " ;  packageOrClassName: " + packageOrClassName);
					}
				}
			}
    	}
    	if ( racService == null ) {
    		LOG.warn( "Unable to find service which handles package/class: " + packageOrClassName + " -- returning null." );
    	}
		return racService;
    }
    
    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService#getBaseInquiryUrl(java.lang.String)
     */
    public String getBaseInquiryUrl(String businessObjectClassName) {
    	RiceApplicationConfigurationService racService = findServiceResponsibleForPackageOrClass(businessObjectClassName);
    	if ( racService != null ) {
    		return racService.getBaseInquiryUrl(businessObjectClassName);
    	}
    	return null;
    }

    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService#getBaseLookupUrl(java.lang.String)
     */
    public String getBaseLookupUrl(String businessObjectClassName) {
    	RiceApplicationConfigurationService racService = findServiceResponsibleForPackageOrClass(businessObjectClassName);
    	if ( racService != null ) {
    		return racService.getBaseLookupUrl(businessObjectClassName);
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService#getBaseHelpUrl(java.lang.String)
     */
    public String getBaseHelpUrl(String businessObjectClassName) {
    	RiceApplicationConfigurationService racService = findServiceResponsibleForPackageOrClass(businessObjectClassName);
    	if ( racService != null ) {
    		return racService.getBaseHelpUrl(businessObjectClassName);
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService#getBusinessObjectAttributeDefinition(java.lang.String, java.lang.String)
     */
    public AttributeDefinition getBusinessObjectAttributeDefinition(String businessObjectClassName, String attributeName) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "Querying for an AttributeDefinition for: " + businessObjectClassName + " / " + attributeName );
    	}
    	RiceApplicationConfigurationService racService = findServiceResponsibleForPackageOrClass(businessObjectClassName);
    	if ( racService != null ) {
    		return racService.getBusinessObjectAttributeDefinition(businessObjectClassName, attributeName);
    	}
    	return null;
    }
}
