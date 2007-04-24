/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;

/**
 * This class is the action form for inquiries.
 */
public class InquiryForm extends KualiForm {
    private static final long serialVersionUID = 1L;
    private String fieldConversions;
    private List sections;
    private String businessObjectClassName;
    private Map editingMode;

    public InquiryForm() {
        super();
        this.editingMode = new HashMap();
        this.editingMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, "TRUE");
    }
    
    /**
     * @return Returns the fieldConversions.
     */
    public String getFieldConversions() {
        return fieldConversions;
    }


    /**
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(String fieldConversions) {
        this.fieldConversions = fieldConversions;
    }


    /**
     * @return Returns the inquiry sections.
     */
    public List getSections() {
        return sections;
    }


    /**
     * @param sections The sections to set.
     */
    public void setSections(List sections) {
        this.sections = sections;
    }

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }

    public Map getEditingMode() {
        return editingMode;
    }
}