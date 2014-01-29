/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kcb.service.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.bo.MessageDeliveryStatus;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.krad.data.DataObjectService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * MessageDeliveryService implementation 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageDeliveryServiceImpl implements MessageDeliveryService {
    private static final Logger LOG = Logger.getLogger(MessageDeliveryServiceImpl.class);

    private DataObjectService dataObjectService;

    /**
     * Number of processing attempts to make.  {@link MessageDelivery}s with this number or more of attempts
     * will not be selected for further processing.
     */
    private int maxProcessAttempts;

    /**
     * Sets the max processing attempts
     * @param maxProcessAttempts the max delivery attempts
     */
    public void setMaxProcessAttempts(int maxProcessAttempts) {
        this.maxProcessAttempts = maxProcessAttempts;
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#saveMessageDelivery(org.kuali.rice.kcb.bo.MessageDelivery)
     */
    public MessageDelivery saveMessageDelivery(MessageDelivery delivery) {
        return dataObjectService.save(delivery);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#deleteMessageDelivery(MessageDelivery)
     */
    public void deleteMessageDelivery(MessageDelivery messageDelivery) {
        dataObjectService.delete(messageDelivery);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getAllMessageDeliveries()
     */
    public Collection<MessageDelivery> getAllMessageDeliveries() {
        return dataObjectService.findMatching(MessageDelivery.class, QueryByCriteria.Builder.create().build()).getResults();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDelivery(java.lang.Long)
     */
    public MessageDelivery getMessageDelivery(Long id) {
        return dataObjectService.find(MessageDelivery.class, id);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDeliveryByDelivererSystemId(java.lang.Long)
     */
    public MessageDelivery getMessageDeliveryByDelivererSystemId(Long id) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();

        criteria.setPredicates(equal(MessageDelivery.SYSTEMID_FIELD, id));
        List<MessageDelivery> results = dataObjectService.findMatching(MessageDelivery.class, criteria.build()).getResults();

        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new RuntimeException("More than one message delivery found with the following delivery system id: " + id);
        }
        return results.get(0);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDeliveries(org.kuali.rice.kcb.bo.Message)
     */
    public Collection<MessageDelivery> getMessageDeliveries(Message message) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(MessageDelivery.MESSAGEID_FIELD, message.getId()));

        return dataObjectService.findMatching(MessageDelivery.class, criteria.build()).getResults();
    }

    /* This method is responsible for atomically finding messagedeliveries, marking them as taken
     * and returning them to the caller for processing.
     * NOTE: it is important that this method execute in a SEPARATE dedicated transaction; either the caller should
     * NOT be wrapped by Spring declarative transaction and this service should be wrapped (which is the case), or
     * the caller should arrange to invoke this from within a newly created transaction).
     */
    public Collection<MessageDelivery> lockAndTakeMessageDeliveries(MessageDeliveryStatus[] statuses) {
        return lockAndTakeMessageDeliveries(null, statuses);
    }
    public Collection<MessageDelivery> lockAndTakeMessageDeliveries(Long messageId, MessageDeliveryStatus[] statuses) {
        LOG.debug("========>> ENTERING LockAndTakeMessageDeliveries: " + Thread.currentThread());
        // DO WITHIN TRANSACTION: get all untaken messagedeliveries, and mark as "taken" so no other thread/job takes them
        // need to think about durability of work list

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(isNull(MessageDelivery.LOCKED_DATE));
        if (messageId != null) {
            predicates.add(equal(MessageDelivery.MESSAGEID_FIELD + ".id", messageId));
        }
        predicates.add(lessThan(MessageDelivery.PROCESS_COUNT, maxProcessAttempts));

        Collection<String> statusCollection = new ArrayList<String>(statuses.length);
        for (MessageDeliveryStatus status: statuses) {
            statusCollection.add(status.name());
        }
        predicates.add(in(MessageDelivery.DELIVERY_STATUS, statusCollection));
        criteria.setPredicates(predicates.toArray(new Predicate[predicates.size()]));
        List<MessageDelivery> messageDeliveries = dataObjectService.findMatching(MessageDelivery.class, criteria.build()).getResults();
        List<MessageDelivery> lockedMsgDels = new ArrayList<MessageDelivery>();

        // mark messageDeliveries as taken
        for (MessageDelivery delivery: messageDeliveries) {
            LOG.debug("Took: " + delivery);
            delivery.setLockedDate(new Timestamp(System.currentTimeMillis()));
            delivery = dataObjectService.save(delivery);
            lockedMsgDels.add(delivery);
        }

        LOG.debug("<<=======  LEAVING LockAndTakeMessageDeliveries: " + Thread.currentThread());
        return lockedMsgDels;
    }

    /**
     * Sets the data object service.
     * @param dataObjectService service to persist data to the datasource
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
