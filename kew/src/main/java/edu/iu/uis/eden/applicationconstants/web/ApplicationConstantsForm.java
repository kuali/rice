/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.applicationconstants.web;

import java.util.Collection;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.applicationconstants.ApplicationConstant;

/**
 * Struts form for the {@link ApplicationConstantsAction}
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsForm extends ActionForm {

    private static final long serialVersionUID = -8441254491845120955L;

    private Collection<ApplicationConstant> applicationConstants;
    private ApplicationConstant constant = new ApplicationConstant();
    private String methodToCall = "";
    
    public Collection<ApplicationConstant> getApplicationConstants() {
        return applicationConstants;
    }

    public void setApplicationConstants(Collection<ApplicationConstant> applicationConstants) {
        this.applicationConstants = applicationConstants;
    }

    public ApplicationConstant getConstant() {
        return constant;
    }

    public void setConstant(ApplicationConstant constant) {
        this.constant = constant;
    }

    public void setApplicationConstantName(String applicationConstantName) {
        constant.setApplicationConstantName(applicationConstantName);
    }

    public void setApplicationConstantValue(String applicationConstantValue) {
        constant.setApplicationConstantValue(applicationConstantValue);
    }

    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

}