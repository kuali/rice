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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.spring.form.InquiryForm;
import org.kuali.rice.kns.web.spring.form.UITestForm;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;

import edu.sampleu.travel.bo.TravelAccount;

/**
 * This class is a top level BeanWrapper for a UIF View (form).  It will call the
 * view service to find formatters and check if fields are encrypted. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifViewBeanWrapper extends BeanWrapperImpl {
    
    private String viewId;
    
    // conatains mapping of property prefix to BO Class for
    // all BO's in the view (on the form), might not need the
    // class when actual calls are made to the view service
    private Map<String, Class<?>> boPrefixes;
    
    // this stores all properties this wrapper has already checked
    // with the view so the service isn't called again
    private Set<String> processedProperties;
    
    
    public UifViewBeanWrapper(Object object, String viewId) {
        super(object);
        
        processedProperties = new HashSet<String>();
        boPrefixes = new HashMap<String, Class<?>>();
        
        // this is for testing, view id should come in as parameter
        if(object instanceof InquiryForm) {
            Object bo = ((InquiryForm)object).getBo();
            // default to TravelAccount since that is what we are testing with
            if(bo == null) {
                boPrefixes.put("bo.", TravelAccount.class);
            }
            else {
                boPrefixes.put("bo.", bo.getClass());
            }
        }
        else if(object instanceof UITestForm) {
            boPrefixes.put("travelAccount1.", TravelAccount.class);
        }
        // end testing
        
        this.viewId = viewId;
    }
    
    
    private void callViewService(String propertyName) {
        Class<? extends Formatter> formatterClass = null;
        
        // check if we already processed this property for this BeanWrapper instance
        if(processedProperties.contains(propertyName)) {
            return;
        }
        
        for(String s : boPrefixes.keySet()) {
            if(propertyName.startsWith(s)) {
                // TODO
                // check authorization service if encryption is needed here
                // set formatter to EncryptedFormatter and skip dictionary lookup
                
                formatterClass = KNSServiceLocator.getDataDictionaryService()
                        .getAttributeFormatter(boPrefixes.get(s), StringUtils.substringAfter(propertyName, s));
                break;
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

}
