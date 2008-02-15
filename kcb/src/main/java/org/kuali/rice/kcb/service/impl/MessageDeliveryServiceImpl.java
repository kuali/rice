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
package org.kuali.rice.kcb.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.dao.BusinessObjectDao;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.springframework.beans.factory.annotation.Required;

/**
 * MessageDeliveryService implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessageDeliveryServiceImpl extends BusinessObjectServiceImpl implements MessageDeliveryService {
    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#saveMessageDelivery(org.kuali.rice.kcb.bo.MessageDelivery)
     */
    public void saveMessageDelivery(MessageDelivery delivery) {
        dao.save(delivery);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#deleteMessageDelivery(java.lang.Long)
     */
    public void deleteMessageDelivery(MessageDelivery messageDelivery) {
        dao.delete(messageDelivery);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDeliveries()
     */
    public Collection<MessageDelivery> getAllMessageDeliveries() {
        return dao.findAll(MessageDelivery.class);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDelivery(java.lang.Long)
     */
    public MessageDelivery getMessageDelivery(Long id) {
        Map<String, Object> fields = new HashMap<String, Object>(1);
        fields.put(MessageDelivery.ID_FIELD, id);
        return (MessageDelivery) dao.findByPrimaryKey(MessageDelivery.class, fields);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDeliveryByDelivererSystemId(java.lang.Long)
     */
    public MessageDelivery getMessageDeliveryByDelivererSystemId(Long id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(MessageDelivery.SYSTEMID_FIELD, id);
        Collection<MessageDelivery> results = dao.findMatching(MessageDelivery.class, criteria);
        if (results == null || results.size() == 0) return null;
        if (results.size() > 1) {
            throw new RuntimeException("More than one message delivery found with the following delivery system id: " + id);
        }
        return results.iterator().next();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageDeliveryService#getMessageDeliveries(org.kuali.rice.kcb.bo.Message)
     */
    public Collection<MessageDelivery> getMessageDeliveries(Message message) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(MessageDelivery.MESSAGEID_FIELD, message.getId());
        return dao.findMatching(MessageDelivery.class, criteria);
    }
}