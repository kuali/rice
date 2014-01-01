/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.util.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * A type adapter that is used to help allow primitive boolean values to be optional in the wsdl.
 * <p>This adapter will translate any missing values into Boolean.FALSE which will be unboxed to false when applied
 * to the field.</p>
 *
 * <p>To accomplish this, mark the field in question with
 * <pre>
 * &#64;XmlElement(name=FIELD_NAME_CONSTANT, required=false, type=Boolean.class)
 * &#64;XmlJavaTypeAdapter(PrimitiveBooleanDefaultToFalseAdapter.class)
 * </pre>
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PrimitiveBooleanDefaultToFalseAdapter extends XmlAdapter {

    /**
     * Converts null to Boolean.FALSE, and otherwise it just returns the argument value.
     *
     * @param v the value to convert
     * @return the value of v, or Boolean.FALSE if v is null
     * @see XmlAdapter#unmarshal(Object)
     */
    @Override
    public Object unmarshal(Object v) {
        if (v == null) {
            v = Boolean.FALSE;
        }

        return v;
    }

    /**
     * Converts null to Boolean.FALSE, and otherwise it just returns the argument value.
     *
     * @param v the value to convert
     * @return the value of v, or Boolean.FALSE if v is null
     * @see XmlAdapter#marshal(Object)
     */
    @Override
    public Object marshal(Object v) {
        if (v == null) {
            v = Boolean.FALSE;
        }

        return v;
    }
}
