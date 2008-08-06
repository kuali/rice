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
package org.kuali.rice.kns.util.documentserializer;

import java.util.Collection;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;

/**
 * This abstract implementation provides a default implementation of {@link #determinePropertyType(Object)}, which should suffice for most
 * use cases.
 *
 */
public abstract class PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {
    /**
     * @see org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluator#determinePropertyType(java.lang.Object)
     */
    public PropertyType determinePropertyType(Object propertyValue) {
        if (propertyValue == null) {
            return PropertyType.PRIMITIVE;
        }
        if (propertyValue instanceof BusinessObject) {
            return PropertyType.BUSINESS_OBJECT;
        }
        if (propertyValue instanceof Collection) {
            return PropertyType.COLLECTION;
        }
        return PropertyType.PRIMITIVE;
    }
}
