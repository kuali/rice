/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.core.api.mo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.util.collect.CollectionUtils;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * All model object's that are Jaxb annotated should extend this class.
 *
 * This class does several important things:
 * <ol>
 *     <li>Defines jaxb callback method to ensure that Collection and Map types are unmarshalled into immutable empty forms rather than null values</li>
 *     <li>Defines equals/hashcode/toString</li>
 *
 *     Note: the equals/hashCode implementation excludes {@value CoreConstants.CommonElements#FUTURE_ELEMENTS} field.
 *     This element should be present on all jaxb annotated classes.
 * </ol>
 *
 * <b>Important: all classes extending this class must be immutable</b>
 */
@XmlTransient // marked as @XmlTransient so that an AbstractDataTransferObjectType is not included in all WSDL schemas
public abstract class AbstractDataTransferObject implements ModelObjectComplete {

    private transient volatile Integer _hashCode;
    private transient volatile String _toString;

    protected AbstractDataTransferObject() {
        super();
    }

    @Override
    public int hashCode() {
        //using DCL idiom to cache hashCodes.  Hashcodes on immutable objects never change.  They can be safely cached.
        //see effective java 2nd ed. pg. 71
        Integer h = _hashCode;
        if (h == null) {
            synchronized (this) {
                h = _hashCode;
                if (h == null) {
                    _hashCode = h = Integer.valueOf(HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE));
                }
            }
        }

        return h.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        //using DCL idiom to cache toString.  toStrings on immutable objects never change.  They can be safely cached.
        //see effective java 2nd ed. pg. 71
        String t = _toString;
        if (t == null) {
            synchronized (this) {
                t = _toString;
                if (t == null) {
                    _toString = t = ToStringBuilder.reflectionToString(this);
                }
            }
        }

        return t;
    }

    @SuppressWarnings("unused")
    protected void beforeUnmarshal(Unmarshaller u, Object parent) throws Exception {
    }

    @SuppressWarnings("unused")
    protected void afterUnmarshal(Unmarshaller u, Object parent) throws Exception {
        CollectionUtils.makeUnmodifiableAndNullSafe(this);
    }

    private transient Object serializationMutex = new Object();

    private void writeObject(ObjectOutputStream out) throws IOException {
        synchronized (serializationMutex) {
            clearFutureElements();
            out.defaultWriteObject();
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        ois.defaultReadObject();
        serializationMutex = new Object();
    }

    /**
     * Looks for a field named "_futureElements" on the class and clears it's value if it exists.  This allows us to
     * prevent from storing these values during serialization.
     */
    private void clearFutureElements() {
        try {
            Field futureElementsField = getClass().getDeclaredField(CoreConstants.CommonElements.FUTURE_ELEMENTS);
            boolean originalAccessible = futureElementsField.isAccessible();
            futureElementsField.setAccessible(true);
            try {
                futureElementsField.set(this, null);
            } finally {
                futureElementsField.setAccessible(originalAccessible);
            }
        } catch (NoSuchFieldException e) {
            // if the field does not exist, don't do anything
        } catch (IllegalAccessException e) {
            // can't modify the field, ignore
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    protected static class Constants {
        final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS, "_hashCode", "_toString" };
    }
}
