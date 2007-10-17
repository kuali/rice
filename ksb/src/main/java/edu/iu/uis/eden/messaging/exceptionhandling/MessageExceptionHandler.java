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
package edu.iu.uis.eden.messaging.exceptionhandling;

import edu.iu.uis.eden.messaging.PersistedMessage;

/**
 * A MessageExceptionHandler handles exception which arrise during processing of the
 * message.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface MessageExceptionHandler {

	public void handleException(Throwable throwable, PersistedMessage message, Object service);
	
    /**
     * Determines whether the message would go into Exception if submitted.
     * @param message The PersistedMessage instance to be tested.
     * @return Returns true if the message would go into exception, otherwise returns false.
     */
    public boolean isInException(PersistedMessage message);

}
