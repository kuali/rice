package org.kuali.bus.services;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import edu.iu.uis.eden.messaging.MessageHelper;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.RemotedServiceRegistry;
import edu.iu.uis.eden.messaging.ServiceRegistry;
import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.exceptionhandling.ExceptionRoutingService;
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
	
	public static Object getService(String name) {
		return GlobalResourceLoader.getService(name);
	}
	
	public static UserTransaction getUserTransaction() {
		return (UserTransaction) getService("userTransaction");
	}
	
	public static JtaTransactionManager getTransactionManager() {
		return (JtaTransactionManager) getService("transactionManager");
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
	
	public static DataSource getDataSource() {
		return (DataSource) getService("dataSource");
	}
}