/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ksb.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusImpl;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.transport.servlet.ServletTransportFactory;
import org.kuali.rice.core.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.MessageHelper;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;
import org.kuali.rice.ksb.messaging.exceptionhandling.ExceptionRoutingService;
import org.kuali.rice.ksb.messaging.service.BusAdminService;
import org.kuali.rice.ksb.messaging.service.MessageQueueService;
import org.kuali.rice.ksb.messaging.service.ServiceRegistry;
import org.kuali.rice.ksb.messaging.threadpool.KSBScheduledPool;
import org.kuali.rice.ksb.messaging.threadpool.KSBThreadPool;
import org.kuali.rice.ksb.security.admin.service.JavaSecurityManagementService;
import org.kuali.rice.ksb.security.service.DigitalSignatureService;
import org.kuali.rice.ksb.util.KSBConstants;
import org.quartz.Scheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


public class KSBServiceLocator {

	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.OBJECT_REMOTER
	 */
	@Deprecated
    public static final String OBJECT_REMOTER = KSBConstants.ServiceNames.OBJECT_REMOTER;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.SERVICE_REMOVER_SERVICE
	 */
	@Deprecated
    public static final String SERVICE_REMOVER_SERVICE = KSBConstants.ServiceNames.SERVICE_REMOVER_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.THREAD_POOL_SERVICE
	 */
	@Deprecated
    public static final String THREAD_POOL_SERVICE = KSBConstants.ServiceNames.THREAD_POOL_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.REMOTED_SERVICE_REGISTRY
	 */
	@Deprecated
    public static final String REMOTED_SERVICE_REGISTRY = KSBConstants.ServiceNames.REMOTED_SERVICE_REGISTRY;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.REPEAT_TOPIC_INVOKING_QUEUE
	 */
	@Deprecated
    public static final String REPEAT_TOPIC_INVOKING_QUEUE = KSBConstants.ServiceNames.REPEAT_TOPIC_INVOKING_QUEUE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.ENCRYPTION_SERVICE
	 */
	@Deprecated
    public static final String ENCRYPTION_SERVICE = KSBConstants.ServiceNames.ENCRYPTION_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.DIGITAL_SIGNATURE_SERVICE
	 */
	@Deprecated
    public static final String DIGITAL_SIGNATURE_SERVICE = KSBConstants.ServiceNames.DIGITAL_SIGNATURE_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.JAVA_SECURITY_MANAGEMENT_SERVICE
	 */
	@Deprecated
    public static final String JAVA_SECURITY_MANAGEMENT_SERVICE = KSBConstants.ServiceNames.JAVA_SECURITY_MANAGEMENT_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.CACHE_ADMINISTRATOR_FACTORY
	 */
	@Deprecated
    public static final String CACHE_ADMINISTRATOR_FACTORY = KSBConstants.ServiceNames.CACHE_ADMINISTRATOR_FACTORY;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.JTA_TRANSACTION_MANAGER
	 */
	@Deprecated
    public static final String JTA_TRANSACTION_MANAGER = KSBConstants.ServiceNames.JTA_TRANSACTION_MANAGER;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.SCHEDULED_THREAD_POOL_SERVICE
	 */
	@Deprecated
    public static final String SCHEDULED_THREAD_POOL_SERVICE = KSBConstants.ServiceNames.SCHEDULED_THREAD_POOL_SERVICE;
	
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.BUS_ADMIN_SERVICE
	 */
	@Deprecated
    public static final String BUS_ADMIN_SERVICE = KSBConstants.ServiceNames.BUS_ADMIN_SERVICE;
    
	/**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.MESSAGE_ENTITY_MANAGER_FACTORY
	 */
	@Deprecated
    public static final String MESSAGE_ENTITY_MANAGER_FACTORY = KSBConstants.ServiceNames.MESSAGE_ENTITY_MANAGER_FACTORY;
    
    /**
	 * @deprecated Use org.kuali.rice.ksb.util.KSBConstants.ServiceNames.REGISTRY_ENTITY_MANAGER_FACTORY
	 */
	@Deprecated
    public static final String REGISTRY_ENTITY_MANAGER_FACTORY = KSBConstants.ServiceNames.REGISTRY_ENTITY_MANAGER_FACTORY;

	
    public static Object getService(String name) {
        return GlobalResourceLoader.getService(name);
    }

    public static EntityManagerFactory getMessageEntityManagerFactory() {
        return (EntityManagerFactory) getService(KSBConstants.ServiceNames.MESSAGE_ENTITY_MANAGER_FACTORY);
    }
    
    public static EntityManagerFactory getRegistryEntityManagerFactory() {
        return (EntityManagerFactory) getService(KSBConstants.ServiceNames.REGISTRY_ENTITY_MANAGER_FACTORY);
    }
    
    public static TransactionTemplate getTransactionTemplate() {
        return (TransactionTemplate) getService(KSBConstants.ServiceNames.TRANSACTION_TEMPLATE);
    }

    public static PlatformTransactionManager getPlatformTransactionManager() {
        return (PlatformTransactionManager) getService(KSBConstants.ServiceNames.TRANSACTION_MANAGER);
    }

    public static BAMService getBAMService() {
        return (BAMService) getService(KSBConstants.ServiceNames.BAM_SERVICE);
    }

    public static MessageHelper getMessageHelper() {
        return (MessageHelper) getService(KSBConstants.ServiceNames.MESSAGE_HELPER);
    }

    public static MessageQueueService getRouteQueueService() {
        return (MessageQueueService) getService(KSBConstants.ServiceNames.ROUTE_QUEUE_SERVICE);
    }

    public static ExceptionRoutingService getExceptionRoutingService() {
        return (ExceptionRoutingService) getService(KSBConstants.ServiceNames.EXCEPTION_MESSAGING_SERVICE);
    }

    public static RemotedServiceRegistry getServiceDeployer() {
        return (RemotedServiceRegistry) getService(KSBConstants.ServiceNames.REMOTED_SERVICE_REGISTRY);
    }

    public static DigitalSignatureService getDigitalSignatureService() {
        return (DigitalSignatureService) getService(KSBConstants.ServiceNames.DIGITAL_SIGNATURE_SERVICE);
    }

    public static JavaSecurityManagementService getJavaSecurityManagementService() {
        return (JavaSecurityManagementService) getService(KSBConstants.ServiceNames.JAVA_SECURITY_MANAGEMENT_SERVICE);
    }

    public static KSBThreadPool getThreadPool() {
        return (KSBThreadPool) getService(KSBConstants.ServiceNames.THREAD_POOL_SERVICE);
    }

    public static KSBScheduledPool getScheduledPool() {
        return (KSBScheduledPool) getService(KSBConstants.ServiceNames.SCHEDULED_THREAD_POOL_SERVICE);
    }

    public static ServiceRegistry getServiceRegistry() {
        return (ServiceRegistry) getService(KSBConstants.ServiceNames.SERVICE_REGISTRY);
    }

    public static Bus getCXFBus(){
    	return (CXFBusImpl) getService(KSBConstants.ServiceNames.CXF_BUS);
    }

    public static ServletTransportFactory getCXFServletTransportFactory(){
    	return (ServletTransportFactory)getService(KSBConstants.ServiceNames.CXF_SERVLET_TRANSPORT_FACTORY);
    }
    
    public static ServerRegistry getCXFServerRegistry(){
    	return (ServerRegistry)getService(KSBConstants.ServiceNames.CXF_SERVER_REGISTRY);
    }
    
    public static List<Interceptor> getInInterceptors() {
    	try {
    		return (List<Interceptor>) getService(KSBConstants.ServiceNames.BUS_IN_INTERCEPTORS);
    	}
    	catch(RiceRemoteServiceConnectionException ex) {
    		// swallow this exception, means no bus wide interceptors defined
    		return null;
    	}
    }
    
    public static List<Interceptor> getOutInterceptors() {
    	try {
    		return (List<Interceptor>) getService(KSBConstants.ServiceNames.BUS_OUT_INTERCEPTORS);
    	}
    	catch(RiceRemoteServiceConnectionException ex) {
    		// swallow this exception, means no bus wide interceptors defined
    		return null;
    	}
    }

    public static DataSource getMessageDataSource() {
        return (DataSource) getService(KSBConstants.ServiceNames.MESSAGE_DATASOURCE);
    }

    public static DataSource getMessageNonTransactionalDataSource() {
        return (DataSource) getService(KSBConstants.ServiceNames.MESSAGE_NON_TRANSACTIONAL_DATASOURCE);
    }

    public static DataSource getRegistryDataSource() {
        return (DataSource) getService(KSBConstants.ServiceNames.REGISTRY_DATASOURCE);
    }

    public static Scheduler getScheduler() {
        return (Scheduler) getService(KSBConstants.ServiceNames.SCHEDULER);
    }

    public static BusAdminService getService() {
        return (BusAdminService) getService(KSBConstants.ServiceNames.BUS_ADMIN_SERVICE);
    }

}
