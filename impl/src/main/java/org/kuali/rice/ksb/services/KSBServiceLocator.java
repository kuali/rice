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
package org.kuali.rice.ksb.services;

import javax.sql.DataSource;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.security.DigitalSignatureService;
import org.kuali.rice.kew.security.admin.JavaSecurityManagementService;
import org.kuali.rice.ksb.messaging.BusAdminService;
import org.kuali.rice.ksb.messaging.MessageHelper;
import org.kuali.rice.ksb.messaging.MessageQueueService;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;
import org.kuali.rice.ksb.messaging.ServiceRegistry;
import org.kuali.rice.ksb.messaging.bam.BAMService;
import org.kuali.rice.ksb.messaging.exceptionhandling.ExceptionRoutingService;
import org.kuali.rice.ksb.messaging.threadpool.KSBScheduledPool;
import org.kuali.rice.ksb.messaging.threadpool.KSBThreadPool;
import org.quartz.Scheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


public class KSBServiceLocator {

    public static final String OBJECT_REMOTER = "ObjectRemoterService";
    public static final String SERVICE_REMOVER_SERVICE = "RemoteClassRemoverService";
    public static final String THREAD_POOL_SERVICE = "enThreadPool";
    public static final String REMOTED_SERVICE_REGISTRY = "enServiceInvoker";
    public static final String REPEAT_TOPIC_INVOKING_QUEUE = "enRepeatTopicInvokerQueue";
    public static final String ENCRYPTION_SERVICE = "enEncryptionService";
    public static final String DIGITAL_SIGNATURE_SERVICE = "digitalSignatureService";
    public static final String JAVA_SECURITY_MANAGEMENT_SERVICE = "ksbJavaSecurityManagementService";
    public static final String CACHE_ADMINISTRATOR_FACTORY = "enKEWCacheAdministratorFactoryService";
    public static final String JTA_TRANSACTION_MANAGER = "jtaTransactionManager";
    public static final String SCHEDULED_THREAD_POOL_SERVICE = "enScheduledThreadPool";
    public static final String BUS_ADMIN_SERVICE = "busAdminService";

    public static Object getService(String name) {
        return GlobalResourceLoader.getService(name);
    }

    public static TransactionTemplate getTransactionTemplate() {
        return (TransactionTemplate) getService("transactionTemplate");
    }

    public static PlatformTransactionManager getPlatformTransactionManager() {
        return (PlatformTransactionManager) getService("transactionManager");
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

    public static DigitalSignatureService getDigitalSignatureService() {
        return (DigitalSignatureService) getService(DIGITAL_SIGNATURE_SERVICE);
    }

    public static JavaSecurityManagementService getJavaSecurityManagementService() {
        return (JavaSecurityManagementService) getService(JAVA_SECURITY_MANAGEMENT_SERVICE);
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

    public static ServiceFactory getXFireServiceFactory() {
        return (ServiceFactory) getService("xfire.serviceFactory");
    }

    public static XFire getXFire() {
        return (XFire) getService("xfire");
    }

    public static DataSource getMessageDataSource() {
        return (DataSource) getService("ksbMessageDataSource");
    }

    public static DataSource getMessageNonTransactionalDataSource() {
        return (DataSource) getService("ksbMessageNonTransactionalDataSource");
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