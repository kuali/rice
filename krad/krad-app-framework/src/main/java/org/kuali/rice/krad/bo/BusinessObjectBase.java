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
package org.kuali.rice.krad.bo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.LinkedHashMap;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class BusinessObjectBase implements BusinessObject {

    /**
     * Default constructor. Required to do some of the voodoo involved in letting the DataDictionary validate attributeNames for a
     * given BusinessObject subclass.
     */
    public BusinessObjectBase() {
    }

    /** @deprecated will be removed in rice 1.1 */
    @Deprecated
    protected final String toStringBuilder(LinkedHashMap<String, Object> fieldValues) {
        throw new UnsupportedOperationException("do not call. this method will be removed from rice 1.1");
    }

    /** @deprecated will be removed in rice 1.1 */
    @Deprecated
     protected final LinkedHashMap<String, Object> toStringMapper() {
         throw new UnsupportedOperationException("do not call.  this method will be removed from rice 1.1");
     }

    @Override
	public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /** @deprecated will be removed in rice 1.1 */
    @Deprecated
    public final void prepareForWorkflow() {
        throw new UnsupportedOperationException("do not call.  this method will be removed from rice 1.1");
    }
}
