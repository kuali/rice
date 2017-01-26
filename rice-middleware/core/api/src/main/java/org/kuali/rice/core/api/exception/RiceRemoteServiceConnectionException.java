/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.api.exception;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceRemoteServiceConnectionException extends RiceRuntimeException {

    /**
     * This constructs an instance of java.lang.RuntimeException
     * 
     * @param message
     */
    public RiceRemoteServiceConnectionException(String message) {
        super(message);
    }


    /**
     * This constructs an instance of java.lang.RuntimeException
     * 
     * @param message
     * @param t
     */
    public RiceRemoteServiceConnectionException(String message, Throwable t) {
        super(message, t);
    }
    
    /**
     * This constructs an instance of java.lang.RuntimeException
     * 
     * @param t
     */
    public RiceRemoteServiceConnectionException(Throwable t) {
        super(t);
    }

}
