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
package edu.iu.uis.eden.messaging.threadpool;

import java.util.Comparator;

import edu.emory.mathcs.backport.java.util.concurrent.PriorityBlockingQueue;
import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMessage;

/**
 * A comparator to put into the {@link PriorityBlockingQueue} used in the {@link KSBThreadPoolImpl}.
 * 
 *  Determines execution order by priority and create date.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PriorityBlockingQueuePersistedMessageComparator implements Comparator {

    
    public int compare(Object arg0, Object arg1) {
	if (! (arg0 instanceof MessageServiceInvoker) || ! (arg1 instanceof MessageServiceInvoker) ) {
	    return 0;
	}
	PersistedMessage message0 = ((MessageServiceInvoker)arg0).getMessage();
	PersistedMessage message1 = ((MessageServiceInvoker)arg1).getMessage();
	
	if (message0.getQueuePriority() < message1.getQueuePriority()) {
	    return -1;
	} else if (message0.getQueuePriority() > message1.getQueuePriority()) {
	    return 1;
	}
	
	if (message0.getQueueDate().before(message1.getQueueDate())) {
	    return -1;
	} else if (message0.getQueueDate().after(message1.getQueueDate())) {
	    return 1;
	}
	
	return 0;
    }

}
