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
package org.kuali.rice.krad.web.bind;

import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValue;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is a top level BeanWrapper for a UIF View (form).  It will call the
 * view service to find formatters and check if fields are encrypted.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifViewBeanWrapper extends BeanWrapperImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifViewBeanWrapper.class);

    // this is a handle to the target object so we don't
    // have to cast so often
    private UifFormBase form;

    // this stores all properties this wrapper has already checked
    // with the view so the service isn't called again
    private Set<String> processedProperties;

    public UifViewBeanWrapper(Object object) {
        super(object);

        form = (UifFormBase) object;
        processedProperties = new HashSet<String>();
    }

    protected void callViewService(String propertyName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting view service call for property '" + propertyName + "'");
        }
        Class<? extends Formatter> formatterClass = null;

        // viewId should be determined in UifAnnotationMethodHandlerAdapter so
        // nothing we can do without one here
        if (form.getView() == null) {
            return;
        }

        // check if we already processed this property for this BeanWrapper instance
        if (processedProperties.contains(propertyName)) {
            return;
        }

        AttributeField af = form.getView().getViewIndex().getAttributeFieldByPath(propertyName);
        boolean requiresEncryption = false;
        if (af != null) {
            if (af.getAttributeSecurity() != null) {
                if (af.getAttributeSecurity().hasRestrictionThatRemovesValueFromUI()) {
                    requiresEncryption = true;
                }
            }

            Formatter formatter = af.getFormatter();
            if (formatter != null) {
                formatterClass = formatter.getClass();
            }
        }

        // really these should be PropertyEditors after we evaluate how many are
        // needed vs how many spring provides
        if (formatterClass != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering custom editor for property path '" + propertyName + "' and formatter class '" +
                        formatterClass.getName() + "'");
            }
            PropertyEditor customEditor = new UifKnsFormatterPropertyEditor(formatterClass);
            if (requiresEncryption) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Enabling encryption for custom editor '" + propertyName + "' and formatter class '" +
                            formatterClass.getName() + "'");
                }
                this.registerCustomEditor(null, propertyName, new UifEncryptionPropertyEditorWrapper(customEditor));
            } else {
                this.registerCustomEditor(null, propertyName, customEditor);
            }
        } else if (requiresEncryption) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No custom formatter for property path '" + propertyName +
                        "' but property does require encryption");
            }
            this.registerCustomEditor(null, propertyName,
                    new UifEncryptionPropertyEditorWrapper(findEditorForPropertyName(propertyName)));
        }

        processedProperties.add(propertyName);
    }

    protected PropertyEditor findEditorForPropertyName(String propertyName) {
        Class<?> clazz = getPropertyType(propertyName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting retrieval of property editor using class '" + clazz + "' and property path '" +
                    propertyName + "'");
        }
        PropertyEditor editor = findCustomEditor(clazz, propertyName);
        if (editor == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No custom property editor found using class '" + clazz + "' and property path '" +
                        propertyName + "'. Attempting to find default property editor class.");
            }
            editor = getDefaultEditor(clazz);
        }
        return editor;
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
            if (pd != null) {
                return pd.getPropertyType();
            }

            // Maybe an indexed/mapped property...
            Object value = super.getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }

            // Check to see if there is a custom editor,
            // which might give an indication on the desired target type.
            Class<?> editorType = guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
        } catch (InvalidPropertyException ex) {
            // Consider as not determinable.
        }

        return null;
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        callViewService(propertyName);
        return super.getPropertyValue(propertyName);
    }

    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        callViewService(pv.getName());
        super.setPropertyValue(pv);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        callViewService(propertyName);
        super.setPropertyValue(propertyName, value);
    }

    @Override
    public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
        //TODO clear cache?
        form = (UifFormBase) object;
        super.setWrappedInstance(object, nestedPath, rootObject);
    }

    @Override
    public void setWrappedInstance(Object object) {
        //TODO clear cache?
        form = (UifFormBase) object;
        super.setWrappedInstance(object);
    }
}
