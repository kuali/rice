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

import java.util.Collection;
import java.util.List;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * MessageService implementation 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MessageServiceImpl implements MessageService {

    private DataObjectService dataObjectService;
    /**
     * @see org.kuali.rice.kcb.service.MessageService#deleteMessage(org.kuali.rice.kcb.bo.Message)
     */
    public void deleteMessage(Message message) {
        dataObjectService.delete(message);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageService#getMessage(java.lang.Long)
     */
    public Message getMessage(Long id) {
        return dataObjectService.find(Message.class, id);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageService#getAllMessages()
     */
    public Collection<Message> getAllMessages() {
        return dataObjectService.findMatching(Message.class, QueryByCriteria.Builder.create().build()).getResults();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageService#saveMessage(org.kuali.rice.kcb.bo.Message)
     */
    public Message saveMessage(Message message) {
        return dataObjectService.save(message);
    }

    /**
     * @see org.kuali.rice.kcb.service.MessageService#getMessageByOriginId(java.lang.String)
     */
    public Message getMessageByOriginId(String originId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(Message.ORIGINID_FIELD, originId));
        List<Message> messages = dataObjectService.findMatching(Message.class, criteria.build()).getResults();

        if (messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    /**
     * Sets the data object service.
     * @param dataObjectService persits data to the datasource.
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
