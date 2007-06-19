/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging;

/**
 * All topics that are called with a repeat are called through this queue.  The 
 * queue is used so normal calling topic calling mechanics can be used when doing 
 * the delay.  This is so we pick up any additional topics that come online 
 * since the time the call was first made.  If we didn't use the queue and just 
 * repeated every original topic call when the call was first made we would miss all the 
 * original topic calls.
 * 
 * @author rkirkend
 */
public interface RepeatTopicInvokerQueue {

	Object invokeTopic(AsynchronousCall methodCall);
	
}
