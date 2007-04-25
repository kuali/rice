/*
 * Copyright 2005-2006 The Kuali Foundation.
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

/**
 * Use this when the data dictionary cannot find a matching business object entry for a populated business object that runs through
 * the data dictionary validation service and its methods.
 * 
 * 
 */
public class UnknownBusinessClassException extends RuntimeException {
    private static final long serialVersionUID = -6207371740492175878L;

    /**
     * Create an UnknownBusinessClassException with the given message
     * 
     * @param message
     */
    public UnknownBusinessClassException(String message) {
        super(message);
    }

    /**
     * Create an UnknownBusinessClassException with the given message and cause
     * 
     * @param message
     * @param cause
     */
    public UnknownBusinessClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
