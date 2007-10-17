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
package edu.iu.uis.eden.lookupable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

/**
 * Struts ActionForm for the {@link LookupAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LookupForm extends ActionForm {
    
	private static final long serialVersionUID = -4397265394670795140L;
	private String formKey;
    private String backLocation;
    private Map fields;
    private String methodToCall = "";
    private String lookupableImplServiceName;
    private String conversionFields;
    private Map fieldConversions;
    private String noReturnParams;
    private List supportedExportFormats = new ArrayList();
    
    /**
     * @return Returns the lookupableImplServiceName.
     */
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    /**
     * @param lookupableImplServiceName
     *            The lookupableImplServiceName to set.
     */
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    /**
     * @return Returns the methodToCall.
     */
    public String getMethodToCall() {
        return methodToCall;
    }

    /**
     * @param methodToCall
     *            The methodToCall to set.
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation
     *            The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @return Returns the formKey.
     */
    public String getFormKey() {
        return formKey;
    }

    /**
     * @param formKey
     *            The formKey to set.
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /**
     * @return Returns the fields.
     */
    public Map getFields() {
        return fields;
    }

    /**
     * @param fields
     *            The fields to set.
     */
    public void setFields(Map fields) {
        this.fields = fields;
    }
    public String getConversionFields() {
        return conversionFields;
    }
    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }
    public String getNoReturnParams() {
        return noReturnParams;
    }
    public void setNoReturnParams(String noReturnParams) {
        this.noReturnParams = noReturnParams;
    }
    public Map getFieldConversions() {
        return fieldConversions;
    }
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    public List getSupportedExportFormats() {
        return supportedExportFormats;
    }

    public void setSupportedExportFormats(List supportedExportFormats) {
        this.supportedExportFormats = supportedExportFormats;
    }


}