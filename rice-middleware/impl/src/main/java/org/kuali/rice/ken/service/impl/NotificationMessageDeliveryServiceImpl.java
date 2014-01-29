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
package org.kuali.rice.ken.service.impl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.dao.NotificationMessegeDeliveryDao;
import org.kuali.rice.ken.service.NotificationMessageDeliveryService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.krad.data.DataObjectService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * NotificationService implementation - this is the default out-of-the-box implementation of the service that uses the 
 * businessObjectDao to get at the data via our OOTB DBMS.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationMessageDeliveryServiceImpl implements NotificationMessageDeliveryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationMessageDeliveryServiceImpl.class);

    private DataObjectService dataObjectService;
    private NotificationMessegeDeliveryDao ntdDao;
    
    /**
     * Constructs a NotificationServiceImpl class instance.
     * @param dataObjectService
     * @param ntdDao
     */
    public NotificationMessageDeliveryServiceImpl(DataObjectService dataObjectService, NotificationMessegeDeliveryDao ntdDao) {
        this.dataObjectService = dataObjectService;
        this.ntdDao = ntdDao;
    }

    /**
     * This is the default implementation that uses the businessObjectDao.
     * @param id
     * @return NotificationMessageDelivery
     */
    public NotificationMessageDelivery getNotificationMessageDelivery(Long id) {

        return dataObjectService.find(NotificationMessageDelivery.class, id);
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationMessageDeliveryService#getNotificationMessageDeliveryByDelivererId(java.lang.String)
     */
    //switch to JPA criteria
    @Override
    public NotificationMessageDelivery getNotificationMessageDeliveryByDelivererId(String id) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(NotificationConstants.BO_PROPERTY_NAMES.DELIVERY_SYSTEM_ID, id));
        Collection<NotificationMessageDelivery> results = dataObjectService.findMatching(NotificationMessageDelivery.class, criteria.build()).getResults();

        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new RuntimeException("More than one message delivery found with the following delivery system id: " + id);
        }

        return results.iterator().next();
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationMessageDeliveryService#getNotificationMessageDeliveries()
     */
    public Collection<NotificationMessageDelivery> getNotificationMessageDeliveries() {
        return dataObjectService.findMatching(NotificationMessageDelivery.class, QueryByCriteria.Builder.create().build()).getResults();
    }
    
    /**
     * @see org.kuali.rice.ken.service.NotificationMessageDeliveryService#getNotificationMessageDeliveries(java.lang.Long, java.lang.String)
     */
    //switch to JPA criteria
    @Override
    public Collection<NotificationMessageDelivery> getNotificationMessageDeliveries(NotificationBo notification, String userRecipientId) {

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(NotificationConstants.BO_PROPERTY_NAMES.NOTIFICATION + ".id", notification.getId()),
                equal(NotificationConstants.BO_PROPERTY_NAMES.USER_RECIPIENT_ID, userRecipientId));

        return dataObjectService.findMatching(NotificationMessageDelivery.class, criteria.build()).getResults();
    }

    /**
     * This method is responsible for atomically finding all untaken, undelivered messagedeliveries, marking them as taken
     * and returning them to the caller for processing.
     * NOTE: it is important that this method execute in a SEPARATE dedicated transaction; either the caller should
     * NOT be wrapped by Spring declarative transaction and this service should be wrapped (which is the case), or
     * the caller should arrange to invoke this from within a newly created transaction).

     * @return a list of available message deliveries that have been marked as taken by the caller
     */
    //switch to JPA criteria
    @Override
    public Collection<NotificationMessageDelivery> takeMessageDeliveriesForDispatch() {
        // DO WITHIN TRANSACTION: get all untaken messagedeliveries, and mark as "taken" so no other thread/job takes them
        // need to think about durability of work list

        // get all undelivered message deliveries
        Collection<NotificationMessageDelivery> messageDeliveries =  ntdDao.getUndeliveredMessageDelivers(dataObjectService);
        List<NotificationMessageDelivery> savedMsgDel = new ArrayList<NotificationMessageDelivery>();
        
        LOG.debug("Retrieved " + messageDeliveries.size() + " available message deliveries: " + System.currentTimeMillis());

        // mark messageDeliveries as taken
        for (NotificationMessageDelivery delivery: messageDeliveries) {
            delivery.setLockedDateValue(new Timestamp(System.currentTimeMillis()));
            savedMsgDel.add(dataObjectService.save(delivery));
        }
        return savedMsgDel;
    }
    
    /**
     * This method is responsible for atomically finding all untaken message deliveries that are ready to be autoremoved,
     * marking them as taken and returning them to the caller for processing.
     * NOTE: it is important that this method execute in a SEPARATE dedicated transaction; either the caller should
     * NOT be wrapped by Spring declarative transaction and this service should be wrapped (which is the case), or
     * the caller should arrange to invoke this from within a newly created transaction).
     * @return a list of notifications to be autoremoved that have been marked as taken by the caller
     */
    @Override
    public Collection<NotificationMessageDelivery> takeMessageDeliveriesForAutoRemoval() {
        // get all UNDELIVERED/DELIVERED notification notification message delivery records with associated notifications that have and autoRemovalDateTime <= current
    	Collection<NotificationMessageDelivery> messageDeliveries = ntdDao.getMessageDeliveriesForAutoRemoval(new Timestamp(System.currentTimeMillis()), dataObjectService);
    	List<NotificationMessageDelivery> savedMsgDel = new ArrayList<NotificationMessageDelivery>();
        for (NotificationMessageDelivery d: messageDeliveries) {
            d.setLockedDateValue(new Timestamp(System.currentTimeMillis()));
            savedMsgDel.add(dataObjectService.save(d));
        }
        
        return savedMsgDel;
    
    }

    /**
     * Unlocks the specified messageDelivery object
     * @param messageDelivery the message delivery to unlock
     */
    @Override
    public void unlockMessageDelivery(NotificationMessageDelivery messageDelivery) {

        NotificationMessageDelivery d = dataObjectService.find(NotificationMessageDelivery.class, messageDelivery.getId());

        if (d == null) {
            throw new RuntimeException("NotificationMessageDelivery #" + messageDelivery.getId() + " not found to unlock");
        }

        d.setLockedDateValue(null);
        dataObjectService.save(d);
    }
}
