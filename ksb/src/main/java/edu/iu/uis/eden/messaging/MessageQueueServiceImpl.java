package edu.iu.uis.eden.messaging;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.RiceUtilities;

import edu.iu.uis.eden.messaging.dao.MessageQueueDAO;

public class MessageQueueServiceImpl implements MessageQueueService {

    private MessageQueueDAO messageQueueDAO;

    public void delete(PersistedMessage routeQueue) {
        this.getMessageQueueDAO().remove(routeQueue);
    }

    public void save(PersistedMessage routeQueue) {
        this.getMessageQueueDAO().save(routeQueue);
    }
    
    public List<PersistedMessage> findAll() {
        return this.getMessageQueueDAO().findAll();
    }
    
    public List<PersistedMessage> findAll(int maxRows) {
        return this.getMessageQueueDAO().findAll(maxRows);
    }

    public PersistedMessage findByRouteQueueId(Long routeQueueId) {
        return getMessageQueueDAO().findByRouteQueueId(routeQueueId);
    }

    public List getNextDocuments(int maxDocuments) {
        if (maxDocuments < 0)
            throw new IllegalArgumentException("maxDocuments must be >= 0, value was: " + maxDocuments);
        if (maxDocuments == 0)
            return new ArrayList();
        return this.getMessageQueueDAO().getNextDocuments(new Integer(maxDocuments));
    }

    public List getNextDocuments() {
        return this.getMessageQueueDAO().getNextDocument();
    }

    public MessageQueueDAO getMessageQueueDAO() {
        return this.messageQueueDAO;
    }

    public void setMessageQueueDAO(MessageQueueDAO queueDAO) {
        this.messageQueueDAO = queueDAO;
    }

//    public void validateRouteQueueValues(PersistedMessage routeQueue) {
//        List<WorkflowServiceErrorImpl> errors = new ArrayList<WorkflowServiceErrorImpl>();
//
//        errors.addAll(checkMaxRetriesExceeded(routeQueue));
//        
//        if (!errors.isEmpty()) {
//            throw new WorkflowServiceErrorException("Route Queue Validation Errors", errors);
//        }
//    }
//    
//    /**
//     * Uses the MessageExceptionHandler appropriate to this message type to test for the 
//     * max allowed retries.
//     * 
//     * @param message A populated message to test.
//     * @return A list of errors, that may have zero to many entries.  Will never return a null object.
//     */
//    protected List<WorkflowServiceErrorImpl> checkMaxRetriesExceeded(PersistedMessage message) {
//        List<WorkflowServiceErrorImpl> errors = new ArrayList<WorkflowServiceErrorImpl>();
//        
//        //  delegate the validation to the MessageExceptionHandler appropriate for this service
//        AsynchronousCall methodCall = null;
//        if (message.getPayload() != null) {
//            methodCall = (AsynchronousCall) KEWServiceLocator.getMessageHelper().deserializeObject(message.getPayload());
//        }
//        String messageExceptionHandlerName = methodCall.getServiceInfo().getServiceDefinition().getMessageExceptionHandler();
//        if (messageExceptionHandlerName == null) {
//            messageExceptionHandlerName = DefaultMessageExceptionHandler.class.getName();
//        }
//        MessageExceptionHandler exceptionHandler =  (MessageExceptionHandler) GlobalResourceLoader.getObject(new ObjectDefinition(messageExceptionHandlerName));
//        
//        if (exceptionHandler.isInException(message)) {
//          errors.add(new WorkflowServiceErrorImpl("Retry Count beyond maximum allowed", "routequeue.RouteQueueService.retryCount.overMax"));
//        }
//        return errors;
//    }
    
	public List<PersistedMessage> findByServiceName(QName serviceName, String methodName) {
		return getMessageQueueDAO().findByServiceName(serviceName, methodName);
	}

    public List<PersistedMessage> findByValues(Map<String,String> criteriaValues) {
        return getMessageQueueDAO().findByValues(criteriaValues);
    }
  
    public Integer getMaxRetryAttempts() {
        return new Integer(Core.getCurrentContextConfig().getProperty(RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY));
    }
    
	public PersistedMessage getMessage(ServiceInfo serviceInfo, AsynchronousCall methodCall, Date deliveryDate) {
		PersistedMessage message = new PersistedMessage();
		message.setIpNumber(RiceUtilities.getIpNumber());
		message.setServiceName(serviceInfo.getQname().toString());
		String encodedMethodCall;
		encodedMethodCall = KSBServiceLocator.getMessageHelper().serializeObject(methodCall);
		message.setPayload(encodedMethodCall);
		if (deliveryDate == null) {
			message.setQueueDate(new Timestamp(System.currentTimeMillis()));
		} else {
			message.setQueueDate(new Timestamp(deliveryDate.getTime()));
		}
		// we need this set to persist the message but it's likely many services
		// won't set this
		// for now just use a reasonable default value
		if (serviceInfo.getServiceDefinition().getPriority() == null) {
			message.setQueuePriority(RiceConstants.ROUTE_QUEUE_DEFAULT_PRIORITY);
		} else {
			message.setQueuePriority(serviceInfo.getServiceDefinition().getPriority());
		}
		message.setQueueStatus(RiceConstants.ROUTE_QUEUE_QUEUED);
		message.setRetryCount(0);
		if (serviceInfo.getServiceDefinition().getMillisToLive() > 0) {
			message.setExpirationDate(new Timestamp(System.currentTimeMillis() + serviceInfo.getServiceDefinition().getMillisToLive()));
		}
		message.setMessageEntity(Core.getCurrentContextConfig().getMessageEntity());
		message.setMethodName(methodCall.getMethodName());
		return message;
	}
}