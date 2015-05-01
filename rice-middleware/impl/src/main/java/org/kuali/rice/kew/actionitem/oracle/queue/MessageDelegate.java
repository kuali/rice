/*
 * Copyright 2006-2015 The Kuali Foundation
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

package org.kuali.rice.kew.actionitem.oracle.queue;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.springframework.jms.support.converter.MessageConversionException;

import javax.jms.JMSException;

/**
 * This class handles messages on the actn_item_changed_mq message queue
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageDelegate {

    /**
     * Handles a message on the actn_item_changed_mq message queue
     *
     * @param actnItemChanged information about the action item that has changed)
     *
     **/
    public void handleMessage(ActionItemChangedPayload actnItemChanged) throws MessageConversionException,
            JMSException {
        Character actionType = actnItemChanged.getActnType();
        String actionItemId = actnItemChanged.getActnItemId();
        System.out.println("actionType: " + actionType + "   actionItemId: " + actionItemId);

        // Get the action item unless it was deleted
        if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_INSERTED) ||
           (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_UPDATED))) {
            ActionItem actionItem = KEWServiceLocator.getActionListService().findByActionItemId(actionItemId);
        }

        if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_INSERTED)) {
            System.out.println("Code to INSERT into external action list goes here");
        } else if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_UPDATED)) {
            System.out.println("Code to UPDATE into external action list goes here");
        } else if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_DELETED)) {
            System.out.println("Code to DELETE into external action list goes here");
        }
    }
}