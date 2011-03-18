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
import org.apache.cxf.message.Message;
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
    
    public static List<Interceptor<? extends Message>> getInInterceptors() {
    	try {
    		return (List<Interceptor<? extends Message>>) getService(KSBConstants.ServiceNames.BUS_IN_INTERCEPTORS);
    	}
    	catch(RiceRemoteServiceConnectionException ex) {
    		// swallow this exception, means no bus wide interceptors defined
    		return null;
    	}
    }
    
    public static List<Interceptor<? extends Message>> getOutInterceptors() {
    	try {
    		return (List<Interceptor<? extends Message>>) getService(KSBConstants.ServiceNames.BUS_OUT_INTERCEPTORS);
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
