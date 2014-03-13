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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.krad.data.provider.annotation.SerializationContext;
import org.kuali.rice.krad.data.provider.annotation.Serialized;
import org.kuali.rice.krad.service.BusinessObjectSerializerService;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluatorBase;
import org.kuali.rice.krad.util.documentserializer.SerializationState;

import javax.persistence.Transient;
import java.lang.reflect.Field;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectSerializerServiceImpl extends SerializerServiceBase implements BusinessObjectSerializerService {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertySerializabilityEvaluator getPropertySerizabilityEvaluator(Object businessObject) {
        // Avoiding AlwaysTruePropertySerializibilityEvaluator otherwise SerializerServiceBase uses the
        // getXmlObjectSerializerService instead of it's own XStream instance, and our ignoreField method isn't used.
        PropertySerializabilityEvaluator evaluator = new PropertySerializabilityEvaluatorBase() {
            @Override
            public boolean isPropertySerializable(SerializationState state, Object containingObject,
                    String childPropertyName, Object childPropertyValue) {
                return true;
            }
        };

        return evaluator;
    }

    /**
     * Examines {@link Serialized} and {@link Transient} annotations to determine if the field should not be serialized.
     *
     * <p>{@inheritDoc}</p>
     */
    @Override
    protected boolean ignoreField(Field field) {
        Serialized serialized = field.getAnnotation(Serialized.class);

        // if we have a @Serialized annotation that is relevant to serializationContext, let it determine serializability
        if (serialized != null && SerializationContext.MAINTENANCE.matches(serialized.forContexts())) {
            return !serialized.enabled(); // note the ! operator, since ignore=true is equiv to serialized=false
        }

        // otherwise, if the field is marked as javax.persistence.Transient, ignore it
        if (field.getAnnotation(Transient.class) != null) {
            return true;
        }

        // by default we don't want to ignore it
        return false;
    }
}
