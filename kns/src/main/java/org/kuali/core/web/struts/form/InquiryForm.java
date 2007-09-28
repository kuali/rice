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

import java.lang.reflect.Constructor;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class is the action form for inquiries.
 */
public class InquiryForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryForm.class);

    private static final long serialVersionUID = 1L;
    private String fieldConversions;
    private List sections;
    private String businessObjectClassName;
    private Map editingMode;
    private String backLocation;
    private String formKey;

    /**
     * The following map is used to pass primary key values between invocations of the inquiry screens after the start method has been called.
     */
    private Map<String, String> inquiryPrimaryKeys;

    /**
     * A comma separated list of field names.  Each field name in this list has an encrypted value in the request.
     */
    private String encryptedValues;

    /**
     * A map of collection name -> Boolean mappings.  Used to denote whether a collection name is configured to show inactive records.
     */
    private Map<String, Boolean> inactiveRecordDisplay;

    private Inquirable inquirable;

    public InquiryForm() {
        super();
        this.editingMode = new HashMap();
        this.editingMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, "TRUE");
        this.inactiveRecordDisplay = null;
    }

        @Override
    public void populate(HttpServletRequest request) {
    // set to null for security reasons (so POJO form base can't access it), then we'll make an new instance of it after
    // POJO form base is done
        this.inquirable = null;
        super.populate(request);
        if (request.getParameter("returnLocation") != null) {
            setBackLocation(request.getParameter("returnLocation"));
        }
        if (request.getParameter("docFormKey") != null) {
            setFormKey(request.getParameter("docFormKey"));
        }

        inquirable = getInquirable(getBusinessObjectClassName());

        // the following variable is true if the method to call is not start, meaning that we already called start
        boolean passedFromPreviousInquiry = !RiceConstants.START_METHOD.equals(getMethodToCall());

        // There is no requirement that an inquiry screen must display the primary key values.  However, when clicking on hide/show (without javascript) and
        // hide/show inactive, the PK values are needed to allow the server to properly render results after the user clicks on a hide/show button that results
        // in server processing.  This line will populate the form with the PK values so that they may be used in subsequent requests.  Note that encrypted
        // values will remain encrypted in the form.
        this.inquiryPrimaryKeys = getPKFieldValues(request, getBusinessObjectClassName(), passedFromPreviousInquiry);

        populateInactiveRecordsInIntoInquirable(inquirable, request);
    }

    protected Inquirable getInquirable(String boClassName) {
        try {
            Class customInquirableClass = null;

            try {
                customInquirableClass = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClassName).getInquiryDefinition().getInquirableClass();
            }
            catch (Exception e) {
                LOG.error("Unable to correlate business object class with maintenance document entry");
            }

            Inquirable kualiInquirable = KNSServiceLocator.getKualiInquirable(); // get inquirable impl from Spring

            if (customInquirableClass != null) {
                Class[] defaultConstructor = new Class[] {};
                Constructor cons = customInquirableClass.getConstructor(defaultConstructor);
                kualiInquirable = (Inquirable) cons.newInstance();
            }

            kualiInquirable.setBusinessObjectClass(Class.forName(boClassName));

            return kualiInquirable;
        }
        catch (Exception e) {
            LOG.error("Error attempting to retrieve inquirable.", e);
            throw new RuntimeException("Error attempting to retrieve inquirable.");
        }
    }

    protected Map<String, String> getPKFieldValues(HttpServletRequest request, String boClassName, boolean passedFromPreviousInquiry) {
    try {
            EncryptionService encryptionService = KNSServiceLocator.getEncryptionService();

            // List of encrypted values
            List encryptedList = new ArrayList();
            if (StringUtils.isNotBlank(getEncryptedValues())) {
                encryptedList = Arrays.asList(StringUtils.split(getEncryptedValues(), RiceConstants.FIELD_CONVERSIONS_SEPERATOR));
            }

            Class businessObjectClass = Class.forName(boClassName);

            DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
            // build list of key values from request, if all keys not given throw error
            List boKeys = KNSServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(businessObjectClass);
            Map<String, String> fieldValues = new HashMap<String, String>();
            for (Iterator iter = boKeys.iterator(); iter.hasNext();) {
                String realPkFieldName = (String) iter.next();
                String pkParamName = realPkFieldName;
                if (passedFromPreviousInquiry) {
                    pkParamName = RiceConstants.INQUIRY_PK_VALUE_PASSED_FROM_PREVIOUS_REQUEST_PREFIX + pkParamName;
                }

                if (request.getParameter(pkParamName) != null) {
                    String parameter = (String) request.getParameter(pkParamName);

                    if (StringUtils.isNotBlank(dataDictionaryService.getAttributeDisplayWorkgroup(boClassName, realPkFieldName))) {
                        // This PK field needs to be encrypted coming in from the request, if it was decrypt it, if not, throw exception

                        // this check prevents a brute-force attacker from passing in an unencrypted PK value that's supposed to be encrypted and determining whether
                        // a record with that guessed PK value exists in the DB, effectively bypassing encryption
                        if (encryptedList.contains(realPkFieldName)) {
                            parameter = encryptionService.decrypt(parameter);
                        }
                        else {
                            LOG.error("All PK fields that are specified as encrypted in the DD must be encrypted when passed into the inquiry page.  Field not encrypted is " + realPkFieldName);
                            throw new RuntimeException("All PK fields that are specified as encrypted in the DD must be encrypted when passed into the inquiry page");
                        }
                    }
                    fieldValues.put(realPkFieldName, parameter);
                }
                else {
                    LOG.error("All keys not given to lookup for bo class name " + businessObjectClass.getName());
                    throw new RuntimeException("All keys not given to lookup for bo class name " + businessObjectClass.getName());
                }
            }
            return fieldValues;
        }
        catch (ClassNotFoundException e) {
	     LOG.error("Can't instantiate class: " + boClassName, e);
          throw new RuntimeException("Can't instantiate class: " + boClassName);
        }
        catch (GeneralSecurityException e) {
            LOG.error("Error occured trying to decrypt value", e);
            throw new RuntimeException("Error occured trying to decrypt value");
        }
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
        /**
     * Gets a comma separated list of field names.  Each field name in this list has an encrypted value in the request.
     *
     * @return a comma separated list of field names.  Each field name in this list has an encrypted value in the request.
     */
    public String getEncryptedValues() {
        return this.encryptedValues;
    }

    /**
     * Sets a comma separated list of field names.  Each field name in this list has an encrypted value in the request.
     *
     * @param encryptedValues a comma separated list of field names.  Each field name in this list has an encrypted value in the request.
     */
    public void setEncryptedValues(String encryptedValues) {
        this.encryptedValues = encryptedValues;
    }

    /**
     * Gets the map used to pass primary key values between invocations of the inquiry screens after the start method has been called.
     *
     * @return
     */
    public Map<String, String> getInquiryPrimaryKeys() {
        return this.inquiryPrimaryKeys;
    }

    /**
     * Sets the map used to pass primary key values between invocations of the inquiry screens after the start method has been called.
     *
     * @param inquiryPrimaryKeys
     */
    public void setInquiryPrimaryKeys(Map<String, String> inquiryPrimaryKeys) {
        this.inquiryPrimaryKeys = inquiryPrimaryKeys;
    }

    /**
     * Gets map of collection name -> Boolean mappings.  Used to denote whether a collection name is configured to show inactive records.
     *
     * @return
     */
    public Map<String, Boolean> getInactiveRecordDisplay() {
        return getInquirable().getInactiveRecordDisplay();
    }

    public Inquirable getInquirable() {
        return inquirable;
    }

    protected void populateInactiveRecordsInIntoInquirable(Inquirable inquirable, HttpServletRequest request) {
	for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
	    String paramName = (String) e.nextElement();
	    if (paramName.startsWith(RiceConstants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX)) {
		String collectionName = StringUtils.substringAfter(paramName, RiceConstants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX);
		Boolean showInactive = Boolean.parseBoolean(request.getParameter(paramName));
		inquirable.setShowInactiveRecords(collectionName, showInactive);
	    }
	}
    }

    public String getFormKey() {
        return this.formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getBackLocation() {
        return this.backLocation;
    }

    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }
}