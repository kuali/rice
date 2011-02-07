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
package org.kuali.rice.kns.web.spring;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;

/**
 * This class is a top level BeanWrapper for a UIF View (form).  It will call the
 * view service to find formatters and check if fields are encrypted. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifViewBeanWrapper extends BeanWrapperImpl {
    
    private String viewId;
    
    // this is a handle to the target object so we don't
    // have to cast so often
    private UifFormBase form;
    
    // this stores all properties this wrapper has already checked
    // with the view so the service isn't called again
    private Set<String> processedProperties;
    
    
    public UifViewBeanWrapper(Object object, String viewId) {
        super(object);
        
        form = (UifFormBase)object;
        
        processedProperties = new HashSet<String>();
        
        this.viewId = viewId;
    }
    
    
    protected void callViewService(String propertyName) {
        Class<? extends Formatter> formatterClass = null;
        
        // viewId should be determined in UifAnnotationMethodHandlerAdapter so
        // nothing we can do without one here
        if(form.getView() == null) {
            return;
        }
        
        // check if we already processed this property for this BeanWrapper instance
        if(processedProperties.contains(propertyName)) {
            return;
        }
        
        AttributeField af = form.getView().getViewIndex().getAttributeFieldByPath(propertyName);
        if(af != null) {
            // TODO check authorization and use EncryptedFormatter if necessary
            
            Formatter formatter = af.getFormatter();
            if(formatter != null) {
                formatterClass = formatter.getClass();
            }
        }
        
        // really these should be PropertyEditors after we evaluate how many are
        // needed vs how many spring provides
        if(formatterClass != null) {
            this.registerCustomEditor(null, propertyName, new KualiFormatterPropertyEditor(formatterClass));
        }
        
        processedProperties.add(propertyName);
        
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
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
        
        for(PropertyValue pv : pvs.getPropertyValues()) {
            callViewService(pv.getName());
        }
        super.setPropertyValues(pvs, ignoreUnknown, ignoreInvalid);
    }

    @Override
    public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
        //TODO clear cache?
        form = (UifFormBase)object;
        super.setWrappedInstance(object, nestedPath, rootObject);
    }

    @Override
    public void setWrappedInstance(Object object) {
        //TODO clear cache?
        form = (UifFormBase)object;
        super.setWrappedInstance(object);
    }

}
