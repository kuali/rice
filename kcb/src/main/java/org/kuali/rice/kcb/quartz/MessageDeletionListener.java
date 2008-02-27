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
package org.kuali.rice.kcb.quartz;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.quartz.MessageProcessingJob.Mode;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Listener that takes care of deleting removed messages 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MessageDeletionListener implements JobListener {
    public static final String NAME = "MessageDeletionListener";

    private static final Logger LOG = Logger.getLogger(MessageDeletionListener.class);
 

    /**
     * @see org.quartz.JobListener#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    public void jobExecutionVetoed(JobExecutionContext arg0) {
    }

    /**
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext arg0) {
    }

    /**
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        if (exception != null) {
            LOG.error("Exception occurred in DeliveryJob, not acting", exception);
            return;
        }
        
        long messageId = context.getMergedJobDataMap().getLong("messageId");
        LOG.info("Handling message " + messageId);
        Mode mode = Mode.valueOf(context.getMergedJobDataMap().getString("mode"));
        //if (Mode.REMOVE == mode) {
            MessageService ms = GlobalKCBServiceLocator.getInstance().getMessageService();
            Message m = ms.getMessage(messageId);
            
            if (m == null) {
                // this can happen in unit tests, or if for some other reason the message goes away before this listener
                // is called
                LOG.warn("Called for invalid message: " + messageId);
                return;
            }
            MessageDeliveryService mds = GlobalKCBServiceLocator.getInstance().getMessageDeliveryService(); 
            Collection<MessageDelivery> c = mds.getMessageDeliveries(m);
            if (c.size() == 0) {
                LOG.info("Deleting message " + m);
                ms.deleteMessage(m);
            } else {
                LOG.info("Message " + m.getId() + " has " + c.size() + " deliveries");
                for (MessageDelivery md: c) {
                    LOG.info(md);
                }
            }
        //}
    }
}