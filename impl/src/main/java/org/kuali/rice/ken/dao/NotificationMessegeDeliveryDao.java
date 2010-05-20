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
package org.kuali.rice.ken.dao;

import java.sql.Timestamp;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.core.dao.GenericDao;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.util.NotificationConstants;

/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface NotificationMessegeDeliveryDao {
	
	public Collection getUndeliveredMessageDelivers(GenericDao businessObjectDao);
	
    public Collection<NotificationMessageDelivery> getMessageDeliveriesForAutoRemoval(Timestamp tm, GenericDao businessObjectDao);

    public Collection <NotificationMessageDelivery> getLockedDeliveries(Class clazz, GenericDao dao);
}
