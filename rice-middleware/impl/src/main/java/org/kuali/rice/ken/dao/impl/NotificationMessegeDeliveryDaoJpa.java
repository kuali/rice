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
package org.kuali.rice.ken.dao.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.dao.NotificationMessegeDeliveryDao;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.krad.data.DataObjectService;

import java.sql.Timestamp;
import java.util.Collection;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * This is a description of what this class does - g1zhang don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NotificationMessegeDeliveryDaoJpa implements NotificationMessegeDeliveryDao{

    private static final Logger LOG = Logger.getLogger(NotificationMessegeDeliveryDaoJpa.class);

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.ken.dao.NotificationMessegeDeliveryDao#getUndeliveredMessageDelivers()
     */
    @Override
    public Collection getUndeliveredMessageDelivers(DataObjectService dataObjectService) {

        //LOG.info("************************calling OJBNotificationMessegeDeliveryDao.getUndeliveredMessageDelivers************************ ");

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
                equal(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED),
                isNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE)
        );

        return dataObjectService.findMatching(NotificationMessageDelivery.class, criteria.build()).getResults();
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.ken.dao.NotificationMessegeDeliveryDao#getMessageDeliveriesForAutoRemoval(org.kuali.rice.core.framework.persistence.dao.GenericDao)
     */
    @Override
    public Collection<NotificationMessageDelivery> getMessageDeliveriesForAutoRemoval(Timestamp tm, DataObjectService dataObjectService) {

        //LOG.info("************************calling OJBNotificationMessegeDeliveryDao.getMessageDeliveriesForAutoRemoval************************ ");

        // get all UNDELIVERED/DELIVERED notification notification message delivery records with associated notifications that have and autoRemovalDateTime <= current
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
            and(
                or(
                    equal(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED),
                    equal(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED)
                ),
                isNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE),
                lessThanOrEqual(NotificationConstants.BO_PROPERTY_NAMES.NOTIFICATION_AUTO_REMOVE_DATE_TIME, new Timestamp(System.currentTimeMillis()))
            )
        );

        Collection<NotificationMessageDelivery> messageDeliveries = dataObjectService.findMatching(NotificationMessageDelivery.class, criteria.build()).getResults();

        return messageDeliveries;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.ken.dao.NotificationMessegeDeliveryDao#getLockedDeliveries(java.lang.Class, org.kuali.rice.core.framework.persistence.dao.GenericDao)
     */
    @Override
    public Collection<NotificationMessageDelivery> getLockedDeliveries(Class clazz, DataObjectService dataObjectService) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(isNotNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE));
        Collection<NotificationMessageDelivery> lockedDeliveries = dataObjectService.findMatching(clazz, criteria.build()).getResults();

        return null;
    }


}
