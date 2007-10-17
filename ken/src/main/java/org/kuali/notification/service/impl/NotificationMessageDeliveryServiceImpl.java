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
package org.kuali.notification.service.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.util.NotificationConstants;

/**
 * NotificationService implementation - this is the default out-of-the-box implementation of the service that uses the 
 * businessObjectDao to get at the data via our OOTB DBMS.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryServiceImpl implements NotificationMessageDeliveryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationMessageDeliveryServiceImpl.class);
    
    private BusinessObjectDao businessObjectDao;
    
    /**
     * Constructs a NotificationServiceImpl class instance.
     * @param businessObjectDao
     */
    public NotificationMessageDeliveryServiceImpl(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * This is the default implementation that uses the businessObjectDao.
     * @param id
     * @return NotificationMessageDelivery
     */
    public NotificationMessageDelivery getNotificationMessageDelivery(Long id) {
	HashMap<String, Long> primaryKeys = new HashMap<String, Long>();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, id);
	
        return (NotificationMessageDelivery) businessObjectDao.findByPrimaryKey(NotificationMessageDelivery.class, primaryKeys);
    }

    /**
     * @see org.kuali.notification.service.NotificationMessageDeliveryService#getNotificationMessageDeliveryByDelivererId(java.lang.Long)
     */
    public NotificationMessageDelivery getNotificationMessageDeliveryByDelivererId(Long id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.DELIVERY_SYSTEM_ID, id);
        Collection<NotificationMessageDelivery> results = businessObjectDao.findMatching(NotificationMessageDelivery.class, criteria);
        if (results == null || results.size() == 0) return null;
        if (results.size() > 1) {
            throw new RuntimeException("More than one message delivery found with the following delivery system id: " + id);
        }
        return results.iterator().next();
    }

    /**
     * @see org.kuali.notification.service.NotificationMessageDeliveryService#getNotificationMessageDeliveries()
     */
    public Collection<NotificationMessageDelivery> getNotificationMessageDeliveries() {
        return businessObjectDao.findAll(NotificationMessageDelivery.class);
    }
    
    /**
     * @see org.kuali.notification.service.NotificationMessageDeliveryService#getNotificationMessageDeliveries(java.lang.Long, java.lang.String)
     */
    public Collection<NotificationMessageDelivery> getNotificationMessageDeliveries(Notification notification, String userRecipientId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.NOTIFICATION, notification.getId());
        criteria.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.USER_RECIPIENT_ID, userRecipientId);
        return businessObjectDao.findMatching(NotificationMessageDelivery.class, criteria);
    }

    /**
     * This method is responsible for atomically finding all untaken, undelivered messagedeliveries, marking them as taken
     * and returning them to the caller for processing.
     * NOTE: it is important that this method execute in a SEPARATE dedicated transaction; either the caller should
     * NOT be wrapped by Spring declarative transaction and this service should be wrapped (which is the case), or
     * the caller should arrange to invoke this from within a newly created transaction).

     * @return a list of available message deliveries that have been marked as taken by the caller
     */
    public Collection<NotificationMessageDelivery> takeMessageDeliveriesForDispatch() {
        // DO WITHIN TRANSACTION: get all untaken messagedeliveries, and mark as "taken" so no other thread/job takes them
        // need to think about durability of work list

        // get all undelivered message deliveries
        Criteria criteria = new Criteria();
        criteria.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);
        criteria.addIsNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);
        // implement our select for update hack
        //criteria = Util.makeSelectForUpdate(criteria);
        Collection<NotificationMessageDelivery> messageDeliveries = businessObjectDao.findMatching(NotificationMessageDelivery.class, criteria, true);

        LOG.debug("Retrieved " + messageDeliveries.size() + " available message deliveries: " + System.currentTimeMillis());

        // mark messageDeliveries as taken
        for (NotificationMessageDelivery delivery: messageDeliveries) {
            delivery.setLockedDate(new Timestamp(System.currentTimeMillis()));
            businessObjectDao.save(delivery);
        }

        return messageDeliveries;
    }
    
    /**
     * This method is responsible for atomically finding all untaken message deliveries that are ready to be autoremoved,
     * marking them as taken and returning them to the caller for processing.
     * NOTE: it is important that this method execute in a SEPARATE dedicated transaction; either the caller should
     * NOT be wrapped by Spring declarative transaction and this service should be wrapped (which is the case), or
     * the caller should arrange to invoke this from within a newly created transaction).
     * @return a list of notifications to be autoremoved that have been marked as taken by the caller
     */
    public Collection<NotificationMessageDelivery> takeMessageDeliveriesForAutoRemoval() {
        // get all UNDELIVERED/DELIVERED notification notification message delivery records with associated notifications that have and autoRemovalDateTime <= current
        Criteria criteria_STATUS = new Criteria();
        criteria_STATUS.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED);

        Criteria criteria_UNDELIVERED = new Criteria();
        criteria_UNDELIVERED.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);

        // now OR the above two together
        criteria_STATUS.addOrCriteria(criteria_UNDELIVERED);

        //Criteria criteria_NOTLOCKED = new Criteria();
        //criteria_NOTLOCKED.addIsNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);

        Criteria fullQueryCriteria = new Criteria();
        fullQueryCriteria.addIsNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);
        //fullQueryCriteria.addAndCriteria(criteria_NOTLOCKED);
        fullQueryCriteria.addLessOrEqualThan(NotificationConstants.BO_PROPERTY_NAMES.NOTIFICATION_AUTO_REMOVE_DATE_TIME, new Timestamp(System.currentTimeMillis()));
        // now add in the STATUS check
        fullQueryCriteria.addAndCriteria(criteria_STATUS);
        
        //fullQueryCriteria = Util.makeSelectForUpdate(fullQueryCriteria);
        
        Collection<NotificationMessageDelivery> messageDeliveries = businessObjectDao.findMatching(NotificationMessageDelivery.class, fullQueryCriteria, true);
        
        for (NotificationMessageDelivery d: messageDeliveries) {
            d.setLockedDate(new Timestamp(System.currentTimeMillis()));
            businessObjectDao.save(d);
        }
        
        return messageDeliveries;
    }

    /**
     * Unlocks the specified messageDelivery object
     * @param messageDelivery the message delivery to unlock
     */
    public void unlockMessageDelivery(NotificationMessageDelivery messageDelivery) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.ID, messageDelivery.getId());
        //criteria = Util.makeSelectForUpdate(criteria);

        Collection<NotificationMessageDelivery> deliveries = businessObjectDao.findMatching(NotificationMessageDelivery.class, criteria, true);
        if (deliveries == null || deliveries.size() == 0) {
            throw new RuntimeException("NotificationMessageDelivery #" + messageDelivery.getId() + " not found to unlock");
        }

        NotificationMessageDelivery d = deliveries.iterator().next();
        d.setLockedDate(null);

        businessObjectDao.save(d);
    }
}