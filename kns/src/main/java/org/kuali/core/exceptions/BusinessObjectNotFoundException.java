/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.exceptions;

import org.kuali.core.bo.PersistableBusinessObject;

/**
 * This class represents a situation where a business object is specified for lookup or loading, but it does not exist in the
 * database.
 * 
 * This exception can be used for when that is a fatal error.
 * 
 * 
 */
public class BusinessObjectNotFoundException extends RuntimeException {

    /**
     * Constructs a BusinessObjectNotFoundException.java.
     */
    public BusinessObjectNotFoundException() {
        super();
    }

    /**
     * Constructs a BusinessObjectNotFoundException.java.
     * 
     * @param message
     */
    public BusinessObjectNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a BusinessObjectNotFoundException.java.
     * 
     * @param message
     * @param cause
     */
    public BusinessObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a BusinessObjectNotFoundException.java.
     * 
     * @param cause
     */
    public BusinessObjectNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * Constructs a BusinessObjectNotFoundException.java.
     * 
     * @param bo - the business object specified which cannot be found in the database
     * @param message - the message to pass up
     */
    public BusinessObjectNotFoundException(PersistableBusinessObject bo, String message) {
        super("BO Not Found: [" + bo.getClass().getName() + "] " + bo.toString() + ".  " + message);
    }

    /**
     * 
     * Constructs a BusinessObjectNotFoundException.java.
     * 
     * @param bo - the business object specified which cannot be found in the database
     */
    public BusinessObjectNotFoundException(PersistableBusinessObject bo) {
        super("BO Not Found: [" + bo.getClass().getName() + "] " + bo.toString() + ".");
    }

}
