/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated use new KRAD Data framework {@link org.kuali.rice.krad.data.DataObjectService}
 */
@Deprecated
public abstract class BusinessObjectBase implements BusinessObject {

    /**
     * Default constructor. Required to do some of the voodoo involved in letting the DataDictionary validate attributeNames for a
     * given BusinessObject subclass.
     */
    public BusinessObjectBase() {
    }

    @Override
	public String toString() {
        class BusinessObjectToStringBuilder extends ReflectionToStringBuilder {

            private BusinessObjectToStringBuilder(Object object) {
                super(object);
            }

            @Override
            public boolean accept(Field field) {
                return String.class.isAssignableFrom(field.getType())
                        || ClassUtils.isPrimitiveOrWrapper(field.getType());
            }

        }

        return new BusinessObjectToStringBuilder(this).toString();
    }

}