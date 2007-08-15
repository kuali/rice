/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.bus.services;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.quartz.Scheduler;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import edu.iu.uis.eden.messaging.BusAdminService;
import edu.iu.uis.eden.messaging.MessageHelper;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.RemotedServiceRegistry;
import edu.iu.uis.eden.messaging.ServiceRegistry;
import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.exceptionhandling.ExceptionRoutingService;
import edu.iu.uis.eden.messaging.threadpool.KSBScheduledPool;
import edu.iu.uis.eden.messaging.threadpool.KSBThreadPool;
import edu.iu.uis.eden.security.DigitalSignatureService;
import edu.iu.uis.eden.security.EncryptionService;
import edu.iu.uis.eden.util.OptimisticLockFailureService;

public class KSBServiceLocator {

    public static final String OBJECT_REMOTER = "ObjectRemoterService";
    public static final String SERVICE_REMOVER_SERVICE = "RemoteClassRemoverService";
    public static final String THREAD_POOL_SERVICE = "enThreadPool";
    public static final String REMOTED_SERVICE_REGISTRY = "enServiceInvoker";
    public static final String REPEAT_TOPIC_INVOKING_QUEUE = "enRepeatTopicInvokerQueue";
    public static final String ENCRYPTION_SERVICE = "enEncryptionService";
    public static final String DIGITAL_SIGNATURE_SERVICE = "digitalSignatureService";
    public static final String CACHE_ADMINISTRATOR_FACTORY = "enKEWCacheAdministratorFactoryService";
    public static final String JTA_TRANSACTION_MANAGER = "jtaTransactionManager";
    public static final String SCHEDULED_THREAD_POOL_SERVICE = "enScheduledThreadPool";
    public static final String BUS_ADMIN_SERVICE = "busAdminService";

    public static Object getService(String name) {
	return GlobalResourceLoader.getService(name);
    }

    public static UserTransaction getUserTransaction() {
	return (UserTransaction) getService("userTransaction");
    }

    public static JtaTransactionManager getTransactionManager() {
	return (JtaTransactionManager) getService("transactionManager");
    }
    
    public static TransactionManager getJtaTransactionManager() {
	return (TransactionManager) getService(JTA_TRANSACTION_MANAGER);
    }

    public static TransactionTemplate getTransactionTemplate() {
	return (TransactionTemplate) getService("transactionTemplate");
    }

    public static BAMService getBAMService() {
	return (BAMService) getService("bamService");
    }

    public static MessageHelper getMessageHelper() {
	return (MessageHelper) getService("enMessageHelper");
    }

    public static MessageQueueService getRouteQueueService() {
	return (MessageQueueService) getService("enRouteQueueService");
    }

    public static ExceptionRoutingService getExceptionRoutingService() {
	return (ExceptionRoutingService) getService("exceptionMessagingService");
    }

    public static RemotedServiceRegistry getServiceDeployer() {
	return (RemotedServiceRegistry) getService(REMOTED_SERVICE_REGISTRY);
    }

    public static EncryptionService getEncryptionService() {
	return (EncryptionService) getService(ENCRYPTION_SERVICE);
    }

    public static DigitalSignatureService getDigitalSignatureService() {
	return (DigitalSignatureService) getService(DIGITAL_SIGNATURE_SERVICE);
    }

    public static KSBThreadPool getThreadPool() {
	return (KSBThreadPool) getService(THREAD_POOL_SERVICE);
    }

    public static KSBScheduledPool getScheduledPool() {
    	return (KSBScheduledPool) getService(SCHEDULED_THREAD_POOL_SERVICE);
    }

    public static ServiceRegistry getIPTableService() {
	return (ServiceRegistry) getService("enRoutingTableService");
    }

    public static OptimisticLockFailureService getOptimisticLockFailureService() {
	return (OptimisticLockFailureService) getService("enOptimisticLockFailureService");
    }

    public static ServiceFactory getXFireServiceFactory() {
	return (ServiceFactory) getService("xfire.serviceFactory");
    }

    public static XFire getXFire() {
	return (XFire) getService("xfire");
    }

    public static DataSource getMessageDataSource() {
	return (DataSource) getService("ksbMessageDataSource");
    }

    public static DataSource getRegistryDataSource() {
	return (DataSource) getService("ksbRegistryDataSource");
    }
    
    public static Scheduler getScheduler() {
    	return (Scheduler) getService("ksbScheduler");
    }
    
    public static BusAdminService getService() {
	return (BusAdminService) getService(BUS_ADMIN_SERVICE);
    }
    
}