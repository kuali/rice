/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.serviceproxies;

import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.MessageServiceInvoker;
import org.kuali.rice.ksb.messaging.PersistedMessageBO;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.transaction.support.TransactionSynchronization;


/**
 * Sends message when current transaction commits.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MessageSendingTransactionSynchronization implements TransactionSynchronization {
    
    private static final Logger LOG = Logger.getLogger(MessageSendingTransactionSynchronization.class);

    //this is to verify this was called without implementing some sort of plugability in this 
    //layer of the code for the tests.
    public static boolean CALLED_TRANS_COMMITTED = false;
    public static boolean CALLED_TRANS_ROLLEDBACKED = false;
    
    private PersistedMessageBO message;
    
    public MessageSendingTransactionSynchronization(PersistedMessageBO message) {
	this.message = message;
    }

    public void afterCommit() {

    }

    public void afterCompletion(int status) {
	if (status == STATUS_COMMITTED) {
	    KSBServiceLocator.getThreadPool().execute(new MessageServiceInvoker(message));
	    CALLED_TRANS_COMMITTED = true;
	} else {
	    LOG.info("Message " + message + " not sent because transaction not committed.");
	    CALLED_TRANS_ROLLEDBACKED = true;
	}
    }

    public void beforeCommit(boolean readOnly) {
    }

    public void beforeCompletion() {
    }

    public void resume() {
    }

    public void suspend() {
    }
    
    public void flush() {
    }

}
