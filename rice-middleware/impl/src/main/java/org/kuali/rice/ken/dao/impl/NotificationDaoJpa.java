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
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.dao.NotificationDao;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.krad.data.DataObjectService;

import java.sql.Timestamp;
import java.util.Collection;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.lessThanOrEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNull;

/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NotificationDaoJpa implements NotificationDao{

	private static final Logger LOG = Logger.getLogger(NotificationDaoJpa.class);

	/**
	 * This overridden method ...
	 * 
	 * @see NotificationDao#findMatchedNotificationsForResolution(java.sql.Timestamp, org.kuali.rice.krad.data.DataObjectService)
	 */
	@Override
	public Collection findMatchedNotificationsForResolution(Timestamp tm, DataObjectService dataObjectService) {

		//LOG.info("************************calling JPANotificationDao.findMatchedNotificationsForResolution(************************ ");

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
            and(
                equal(NotificationConstants.BO_PROPERTY_NAMES.PROCESSING_FLAG, NotificationConstants.PROCESSING_FLAGS.UNRESOLVED),
                lessThanOrEqual(NotificationConstants.BO_PROPERTY_NAMES.SEND_DATE_TIME, new Timestamp(System.currentTimeMillis())),
                isNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE)
            )
        );

        return dataObjectService.findMatching(NotificationBo.class, criteria.build()).getResults();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see NotificationDao#findMatchedNotificationsForUnlock(org.kuali.rice.ken.bo.NotificationBo, org.kuali.rice.krad.data.DataObjectService)
	 */
	@Override
	public Collection findMatchedNotificationsForUnlock(NotificationBo not, DataObjectService dataObjectService) {

		//LOG.info("************************calling JPANotificationDao.findMatchedNotificationsForForUnlock************************ ");

		QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(NotificationConstants.BO_PROPERTY_NAMES.ID, not.getId()));

        Collection<NotificationBo> notifications = dataObjectService.findMatching(NotificationBo.class, criteria.build()).getResults();

		return notifications;
	}
}

