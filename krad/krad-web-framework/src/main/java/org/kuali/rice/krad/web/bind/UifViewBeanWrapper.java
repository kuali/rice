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
package org.kuali.rice.krad.web.bind;

import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValue;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.HashSet;
import java.util.Set;

/**
 * Class is a top level BeanWrapper for a UIF View Model
 *
 * <p>
 * Registers custom property editors configured on the field associated with the property name for which
 * we are getting or setting a value. In addition determines if the field requires encryption and if so applies
 * the {@link UifEncryptionPropertyEditorWrapper}
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifViewBeanWrapper extends BeanWrapperImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifViewBeanWrapper.class);

    // this is a handle to the target object so we don't have to cast so often
    private ViewModel model;

    // this stores all properties this wrapper has already checked
    // with the view so the service isn't called again
    private Set<String> processedProperties;

    public UifViewBeanWrapper(ViewModel model) {
        super(model);

        this.model = model;
        this.processedProperties = new HashSet<String>();
    }

    /**
     * Attempts to find a corresponding data field for the given property name in the current view or previous view,
     * then if the field has a property editor configured it is registered with the property editor registry to use
     * for this property
     *
     * @param propertyName - name of the property to find field and editor for
     */
    protected void registerEditorFromView(String propertyName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to find property editor for property '" + propertyName + "'");
        }

        PropertyEditor propertyEditor = null;

        // viewId should be determined in UifAnnotationMethodHandlerAdapter so
        // nothing we can do without one here
        if (model.getView() == null) {
            return;
        }

        // check if we already processed this property for this BeanWrapper instance
        if (processedProperties.contains(propertyName)) {
            return;
        }

        DataField dataField = null;
        if (model.getView().getViewIndex() != null) {
            dataField = model.getView().getViewIndex().getDataFieldByPath(propertyName);
        }

        if ((dataField == null) && (model.getPreviousView() != null) && (model.getPreviousView().getViewIndex()
                != null)) {
            dataField = model.getPreviousView().getViewIndex().getDataFieldByPath(propertyName);
        }

        // determine if the field value should be secured
        boolean requiresEncryption = false;
        if (dataField != null) {
            if (dataField.hasSecureValue()) {
                requiresEncryption = true;
            }

            propertyEditor = dataField.getPropertyEditor();
        }

        if (propertyEditor != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering custom editor for property path '"
                        + propertyName
                        + "' and property editor class '"
                        + propertyEditor.getClass().getName()
                        + "'");
            }

            if (requiresEncryption) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Enabling encryption for custom editor '"
                            + propertyName
                            + "' and property editor class '"
                            + propertyEditor.getClass().getName()
                            + "'");
                }
                this.registerCustomEditor(null, propertyName, new UifEncryptionPropertyEditorWrapper(propertyEditor));
            } else {
                this.registerCustomEditor(null, propertyName, propertyEditor);
            }
        } else if (requiresEncryption) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No custom formatter for property path '"
                        + propertyName
                        + "' but property does require encryption");
            }

            this.registerCustomEditor(null, propertyName, new UifEncryptionPropertyEditorWrapper(
                    findEditorForPropertyName(propertyName)));
        }

        processedProperties.add(propertyName);
    }

    protected PropertyEditor findEditorForPropertyName(String propertyName) {
        Class<?> clazz = getPropertyType(propertyName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting retrieval of property editor using class '"
                    + clazz
                    + "' and property path '"
                    + propertyName
                    + "'");
        }

        PropertyEditor editor = findCustomEditor(clazz, propertyName);
        if (editor == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No custom property editor found using class '"
                        + clazz
                        + "' and property path '"
                        + propertyName
                        + "'. Attempting to find default property editor class.");
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
        registerEditorFromView(propertyName);
        return super.getPropertyValue(propertyName);
    }

    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        registerEditorFromView(pv.getName());
        super.setPropertyValue(pv);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        registerEditorFromView(propertyName);
        super.setPropertyValue(propertyName, value);
    }

    @Override
    public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
        //TODO clear cache?
        model = (ViewModel) object;
        super.setWrappedInstance(object, nestedPath, rootObject);
    }

    @Override
    public void setWrappedInstance(Object object) {
        //TODO clear cache?
        model = (ViewModel) object;
        super.setWrappedInstance(object);
    }
}
