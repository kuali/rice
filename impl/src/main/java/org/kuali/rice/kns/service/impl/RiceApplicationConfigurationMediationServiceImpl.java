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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.NamespaceService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationMediationService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationService;
import org.kuali.rice.ksb.messaging.BusClientFailureProxy;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.kns.bo.Namespace;

//@Transactional
public class RiceApplicationConfigurationMediationServiceImpl implements RiceApplicationConfigurationMediationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationMediationServiceImpl.class);
     
 // Max age defined in seconds
    protected int configurationParameterCacheMaxSize = 200;
    protected int configurationParameterCacheMaxAgeSeconds = 3600;
    protected int nonDatabaseComponentsCacheMaxSize = 50;
    protected int nonDatabaseComponentsCacheMaxAgeSeconds = 3600;

    protected HashMap<String,MaxAgeSoftReference<String>> configurationParameterCache = new HashMap<String,MaxAgeSoftReference<String>>( configurationParameterCacheMaxSize );
    protected HashMap<String,MaxAgeSoftReference<List<ParameterDetailType>>> nonDatabaseComponentsCache = new HashMap<String,MaxAgeSoftReference<List<ParameterDetailType>>>( nonDatabaseComponentsCacheMaxSize );
    
    public String getConfigurationParameter( String namespaceCode, String parameterName ){
    	
    	String parameterValue = null;
    	if ( namespaceCode != null ) {
    	    String parameterKey = (new StringBuffer(namespaceCode).append("|").append(parameterName)).toString();
    	    parameterValue = getParameterValueFromConfigurationParameterCache( parameterKey );
    	    if ( parameterValue != null ) {
                return parameterValue;
            }
    	    NamespaceService nsService = KNSServiceLocator.getNamespaceService();
    	    String applicationNamespaceCode = null;
    	    Namespace namespace = nsService.getNamespace(namespaceCode);
    	    if (namespace != null) {
    	        applicationNamespaceCode = namespace.getApplicationNamespaceCode();
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
        if (configurationParameterCache == null) {
            configurationParameterCache = new HashMap<String,MaxAgeSoftReference<String>>( configurationParameterCacheMaxSize );
        }
        MaxAgeSoftReference<String> parameterValue = configurationParameterCache.get( parameterKey );
        if ( parameterValue != null ) {
            return parameterValue.get();
        }
        return null;
    }
    
    protected List<ParameterDetailType> getComponentListFromNonDatabaseComponentsCache(String nonDatabaseServiceNameKey) {
        if (nonDatabaseComponentsCache == null) {
            nonDatabaseComponentsCache =  new HashMap<String,MaxAgeSoftReference<List<ParameterDetailType>>>( nonDatabaseComponentsCacheMaxSize );
        }
        MaxAgeSoftReference<List<ParameterDetailType>> nonDatabaseComponent = nonDatabaseComponentsCache.get( nonDatabaseServiceNameKey );
        if ( nonDatabaseComponent != null ) {
            return nonDatabaseComponent.get();
        }
        return null;
    }

    public List<ParameterDetailType> getNonDatabaseComponents() {
    	
		// TODO I think the code that's below here will actually pull in more than
		// one reference to a particular application's config service if it is deployed
		// in a cluster, it needs to only pull a single RiceApplicationConfigurationService
		// implementation per service namespace.  Also, may want to consider load balancing
		// and failover in those cases?  It might be best to try and utilize the client-side
		// KSB proxies that handle a lot of this stuff for us
    	

    	RemoteResourceServiceLocator remoteResourceServiceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
    	List<QName> serviceNames = remoteResourceServiceLocator.getServiceNamesForUnqualifiedName(KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE);
		
		List<ParameterDetailType> nonDatabaseComponents = new ArrayList<ParameterDetailType>();
		//add cache per serviceName
		for ( QName serviceName : serviceNames ) {
		    List<ParameterDetailType> nonDatabaseComponentFromCache = this.getComponentListFromNonDatabaseComponentsCache(serviceName.toString());
	        if (nonDatabaseComponentFromCache != null) {
	            nonDatabaseComponents.addAll(nonDatabaseComponentFromCache);
	        } else {
    			RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(serviceName);
    			try {
        			if (rac != null) {
        				nonDatabaseComponents.addAll(rac.getNonDatabaseComponents());
        				synchronized (nonDatabaseComponentsCache) {
            	            nonDatabaseComponentsCache.put(serviceName.toString(), new MaxAgeSoftReference<List<ParameterDetailType>>( nonDatabaseComponentsCacheMaxAgeSeconds, rac.getNonDatabaseComponents() ));
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
    
    private RiceApplicationConfigurationService findRiceApplicationConfigurationService(QName serviceName) {
    	try {
    		return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(serviceName);
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with name: " + serviceName);
    	}
    	return null;
    }
    
    private RiceApplicationConfigurationService findRiceApplicationConfigurationService(String namespace) {
    	try {
    		return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(new QName(namespace, KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE));
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with namespace: " + namespace);
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
    

}
