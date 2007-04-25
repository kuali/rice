/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.bo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.TypeUtils;

/**
 * Transient Business Object Base Business Object
 */
public abstract class BusinessObjectBase implements BusinessObject {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectBase.class);

    /**
     * Default constructor. Required to do some of the voodoo involved in letting the DataDictionary validate attributeNames for a
     * given BusinessObject subclass.
     */
    public BusinessObjectBase() {
    }

    /**
     * @param fieldValues
     * @return consistently-formatted String containing the given fieldnames and their values
     */
    protected String toStringBuilder(LinkedHashMap fieldValues) {
        String built = null;
        String className = StringUtils.uncapitalize(StringUtils.substringAfterLast(this.getClass().getName(), "."));

        if ((fieldValues == null) || fieldValues.isEmpty()) {
            built = super.toString();
        }
        else {

            StringBuffer prefix = new StringBuffer(className);
            StringBuffer suffix = new StringBuffer("=");

            prefix.append("(");
            suffix.append("(");
            for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                String fieldName = e.getKey().toString();
                Object fieldValue = e.getValue();

                String fieldValueString = String.valueOf(e.getValue()); // prevent NullPointerException;


                if ((fieldValue == null) || TypeUtils.isSimpleType(fieldValue.getClass())) {
                    prefix.append(fieldName);
                    suffix.append(fieldValueString);
                }
                else {
                    prefix.append("{");
                    prefix.append(fieldName);
                    prefix.append("}");

                    suffix.append("{");
                    suffix.append(fieldValueString);
                    suffix.append("}");
                }

                if (i.hasNext()) {
                    prefix.append(",");
                    suffix.append(",");
                }
            }
            prefix.append(")");
            suffix.append(")");

            built = prefix.toString() + suffix.toString();
        }

        return built;
    }

    /**
     * @return Map containing the fieldValues of the key fields for this class, indexed by fieldName
     */
    abstract protected LinkedHashMap toStringMapper();


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (!StringUtils.contains(this.getClass().getName(), "$$")) {
            return toStringBuilder(toStringMapper());
        }
        else {
            return "Proxy: " + this.getClass().getName();
        }
    }
}
