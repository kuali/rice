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
package org.kuali.rice.ksb.messaging;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.messaging.dao.MessageQueueDAO;
import org.kuali.rice.ksb.util.KSBConstants;


public class MessageQueueServiceImpl implements MessageQueueService {

    
    	private static final Logger LOG = Logger.getLogger(MessageQueueServiceImpl.class);
    private MessageQueueDAO messageQueueDAO;

    public void delete(PersistedMessage routeQueue) {
	    if (new Boolean(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.MESSAGE_PERSISTENCE))) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("Message Persistence is on.  Deleting stored message" + routeQueue);
		}
	this.getMessageQueueDAO().remove(routeQueue);
    }
	}

    public void save(PersistedMessage routeQueue) {
	    if (new Boolean(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.MESSAGE_PERSISTENCE))) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persisting Message " + routeQueue);
		}
	this.getMessageQueueDAO().save(routeQueue);
    }
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
    
    public PersistedMassagePayload findByPersistedMessageByRouteQueueId(Long routeQueueId) {
	return messageQueueDAO.findByPersistedMessageByRouteQueueId(routeQueueId);
    }

    public List<PersistedMessage> getNextDocuments(Integer maxDocuments) {
	return this.getMessageQueueDAO().getNextDocuments(maxDocuments);
    }

    public MessageQueueDAO getMessageQueueDAO() {
	return this.messageQueueDAO;
    }

    public void setMessageQueueDAO(MessageQueueDAO queueDAO) {
	this.messageQueueDAO = queueDAO;
    }

    public List<PersistedMessage> findByServiceName(QName serviceName, String methodName) {
	return getMessageQueueDAO().findByServiceName(serviceName, methodName);
    }

    public List<PersistedMessage> findByValues(Map<String, String> criteriaValues, int maxRows) {
	return getMessageQueueDAO().findByValues(criteriaValues, maxRows);
    }

    public Integer getMaxRetryAttempts() {
	return new Integer(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY));
    }

    public PersistedMessage getMessage(ServiceInfo serviceInfo, AsynchronousCall methodCall) {
	PersistedMessage message = new PersistedMessage();
	message.setPayload(new PersistedMassagePayload(methodCall, message));
	message.setIpNumber(RiceUtilities.getIpNumber());
	message.setServiceName(serviceInfo.getQname().toString());
	    message.setQueueDate(new Timestamp(System.currentTimeMillis()));
	if (serviceInfo.getServiceDefinition().getPriority() == null) {
	    message.setQueuePriority(KSBConstants.ROUTE_QUEUE_DEFAULT_PRIORITY);
	} else {
	    message.setQueuePriority(serviceInfo.getServiceDefinition().getPriority());
	}
	message.setQueueStatus(KSBConstants.ROUTE_QUEUE_QUEUED);
	message.setRetryCount(0);
	if (serviceInfo.getServiceDefinition().getMillisToLive() > 0) {
			message.setExpirationDate(new Timestamp(System.currentTimeMillis() + serviceInfo.getServiceDefinition().getMillisToLive()));
	}
	message.setMessageEntity(ConfigContext.getCurrentContextConfig().getMessageEntity());
	message.setMethodName(methodCall.getMethodName());
	return message;
    }
}