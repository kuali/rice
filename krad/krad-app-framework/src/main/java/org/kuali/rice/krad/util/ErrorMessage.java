/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.util;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Contains the error message key and parameters for a specific instantiation of an error message.
 * 
 * 
 */
public class ErrorMessage implements Serializable {
    private String errorKey;
    private String[] messageParameters;

    /**
     * Default constructor, required by AutoPopulatingList
     */
    public ErrorMessage() {
    }

    /**
     * Convenience constructor which sets both fields
     * 
     * @param errorKey
     * @param messageParameters
     */
    public ErrorMessage(String errorKey, String... messageParameters) {
        if (StringUtils.isBlank(errorKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        setErrorKey(errorKey);
        setMessageParameters((String[]) ArrayUtils.clone(messageParameters));
    }


    public void setErrorKey(String errorKey) {
        if (StringUtils.isBlank(errorKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }


    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(getErrorKey());

        String[] params = getMessageParameters();
        if (params != null) {
            s.append("(");
            for (int i = 0; i < params.length; ++i) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(params[i]);
            }
            s.append(")");
        }
        return s.toString();
    }


    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (this == obj) {
            equals = true;
        }
        else if (obj instanceof ErrorMessage) {
            ErrorMessage other = (ErrorMessage) obj;

            if (StringUtils.equals(getErrorKey(), other.getErrorKey())) {
                equals = Arrays.equals(getMessageParameters(), other.getMessageParameters());
            }
        }

        return equals;
    }

    /**
     * Defined because when you redefine equals, you must redefine hashcode.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 5011966;

        if (getErrorKey() != null) {
            hashCode = getErrorKey().hashCode();
        }

        return hashCode;
    }
}
