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
package org.kuali.rice.krad.web.bind;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyValue;

/**
 * Bean wrapper that will auto grow paths for setting the value but not grow paths for getting
 * a value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifBeanWrapper extends BeanWrapperImpl {

    public UifBeanWrapper() {
    }

    public UifBeanWrapper(boolean registerDefaultEditors) {
        super(registerDefaultEditors);
    }

    public UifBeanWrapper(Object object) {
        super(object);
    }

    public UifBeanWrapper(Class<?> clazz) {
        super(clazz);
    }

    public UifBeanWrapper(Object object, String nestedPath, Object rootObject) {
        super(object, nestedPath, rootObject);
    }

    /**
     * Overridden to set auto grow nested paths to false for getting the value.
     *
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        return getPropertyValue(propertyName, false);
    }

    /**
     * Returns the value for the given property growing nested paths depending on the parameter.
     *
     * @param propertyName name of the property to get value for
     * @param autoGrowNestedPaths whether nested paths should be grown (initialized if null)
     * @return value for property
     */
    protected Object getPropertyValue(String propertyName, boolean autoGrowNestedPaths) {
        setAutoGrowNestedPaths(autoGrowNestedPaths);

        Object value = null;
        try {
            value = super.getPropertyValue(propertyName);
        } catch (NullValueInNestedPathException e) {
            // swallow null values in path and return null as the value
        } catch (InvalidPropertyException e1) {
            if (!(e1.getRootCause() instanceof NullValueInNestedPathException)) {
                throw e1;
            }
        }

        return value;
    }

    /**
     * Override to set auto grow to true for setting property values.
     *
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        setAutoGrowNestedPaths(true);

        super.setPropertyValue(pv);
    }

    /**
     * Override to set auto grow to true for setting property values.
     *
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        setAutoGrowNestedPaths(true);

        super.setPropertyValue(propertyName, value);
    }

    /**
     * Override to instantiate a UIF bean wrapper for nested bean wrappers.
     *
     * {@inheritDoc}
     */
    @Override
    protected BeanWrapperImpl newNestedBeanWrapper(Object object, String nestedPath) {
        return new UifBeanWrapper(object, nestedPath, this);
    }
}
