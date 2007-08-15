/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging.serviceproxies;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMessage;

/**
 * Puts new message invoker in thread pool when JTA transaction completes. This is what allows us to keep our messaging
 * sync'd up with our current tranaction.
 * 
 * @author rkirkend
 * 
 */
public class AsynchronousMessageCaller implements Synchronization {

    private static final Logger LOG = Logger.getLogger(AsynchronousMessageCaller.class);

    //this is to verify this was called without implementing some sort of plugability in this 
    //layer of the code for the tests.
    public static boolean CALLED_TRANS_COMMITTED = false;
    public static boolean CALLED_TRANS_ROLLEDBACKED = false;
    
    private PersistedMessage message;

    public AsynchronousMessageCaller(PersistedMessage message) {
	this.message = message;
    }

    public void afterCompletion(int status) {
	if (status == Status.STATUS_COMMITTED) {
	    KSBServiceLocator.getThreadPool().execute(new MessageServiceInvoker(message));
	    CALLED_TRANS_COMMITTED = true;
	} else {
	    LOG.info("Message " + message + " not sent because transaction not committed.");
	    CALLED_TRANS_ROLLEDBACKED = true;
	}
    }

    public void beforeCompletion() {
    }
}