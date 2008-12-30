/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.authorization.FieldAuthorization;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;
import org.kuali.rice.kns.web.ui.Field;

/**
 * 
 * This class holds all the information needed to describe the authorization related restrictions for a MaintenanceDocument.
 * 
 * IMPORTANT NOTE: This class defaults to fully editable, if not otherwise specified. So if this class is queried for the status of
 * a field, and the field has not been specified in this class, it will return a FieldAuthorization class populated with the
 * fieldName and EDITABLE.
 * 
 * 
 */
public class MaintenanceDocumentAuthorizations implements Serializable {

    private static final Logger LOG = Logger.getLogger(MaintenanceDocumentAuthorizations.class);

    private Map authFields;
    private Map fieldFormatters;
    private Set<String> hiddenSectionIds; // not implemented, does nothing yet
    private Set<String> readOnlySectionIds; // not implemented, does nothing yet

    public MaintenanceDocumentAuthorizations() {
        authFields = new HashMap();
        hiddenSectionIds = new HashSet<String>();
        readOnlySectionIds = new HashSet<String>();
        fieldFormatters = new HashMap();
    }

    /**
     * 
     * Returns a collection of all the fields that have non-default authorization restrictions.
     * 
     * @return Collection of field names that are restricted
     * 
     */
    public Collection getAuthFieldNames() {
        return authFields.keySet();
    }

    /**
     * 
     * This method is a convenience method to determine whether there are any restricted fields at all in this
     * MaintDocAuthorization.
     * 
     * If any fields are listed as restricted, this will return true, otherwise it will return false.
     * 
     * @return true if any fields restricted, false otherwise
     * 
     */
    public boolean hasAnyFieldRestrictions() {

        boolean anyRestricted = false;

        // walk through each field listed here
        for (Iterator iter = authFields.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();
            FieldAuthorization fieldAuth = getAuthFieldAuthorization(fieldName);

            // if a field is restricted, then we know there are some restricted, so we're done
            if (fieldAuth.isRestricted()) {
                return true;
            }
        }
        return anyRestricted;
    }

    /**
     * 
     * This method is a quick way to lookup whether the field has a definition in this authorization. If it isnt defined with some
     * authorization flag, this returns false.
     * 
     * @param fieldName
     * @return boolean
     */
    public boolean hasAuthFieldRestricted(String fieldName) {
        if (authFields.containsKey(fieldName)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 
     * This method adds a new authorization field instance.
     * 
     * @param fieldName
     * @param authorizationFlag - Field.HIDDEN, Field.READONLY, or Field.EDITABLE;
     * 
     */
    public void addAuthField(String fieldName, String authorizationFlag) {
        authFields.put(fieldName, authorizationFlag);
    }
   

    /**
     * This method adds the fieldName specified as Editable.
     * 
     * @param fieldName
     */
    public void addEditableAuthField(String fieldName) {
        addAuthField(fieldName, Field.EDITABLE);
    }

    /**
     * This method adds the fieldName specified as Read-Only.
     * 
     * @param fieldName
     */
    public void addReadonlyAuthField(String fieldName) {
        addAuthField(fieldName, Field.READONLY);
    }

    /**
     * This method adds the fieldName specified as Hidden.
     * 
     * @param fieldName
     */
    public void addHiddenAuthField(String fieldName) {
        addAuthField(fieldName, Field.HIDDEN);
    }
    

    /**
     * This method adds the fieldName specified as masked.
     * 
     * @param fieldName
     */
    public void addMaskedAuthField(String fieldName, MaskFormatter maskFormatter) {
        addAuthField(fieldName, Field.MASKED);
        addFieldFormatter(fieldName, maskFormatter);
        
    }

    /**
     * This method adds the fieldName specified as partially masked.
     * 
     * @param fieldName
     */
    public void addPartiallyMaskedAuthField(String fieldName, MaskFormatter partialMaskFormatter) {
        addAuthField(fieldName, Field.PARTIALLY_MASKED);
        addFieldFormatter(fieldName, partialMaskFormatter);
    }
    
 
    public void addHiddenSectionId(String sectionId) {
    	hiddenSectionIds.add(sectionId);
    }

    public void addReadOnlySectionId(String sectionId) {
    	readOnlySectionIds.add(sectionId);
    }

    /**
     * 
     * Returns a collection of all the sections that have non-default authorization restrictions.
     * 
     * @return Collection of section names that are restricted
     * 
     */
    public Set<String> getHiddenSectionIds() {
        return hiddenSectionIds;
    }

    public Set<String> getReadOnlySectionIds() {
        return readOnlySectionIds;
    }

    /**
     * 
     * This method returns the authorization setting for the given field name. If the field name is not restricted in any way, a
     * default full-editable value is returned.
     * 
     * @param fieldName - name of field to get authorization restrictions for.
     * @return a populated FieldAuthorization class for this field
     * 
     */
    public FieldAuthorization getAuthFieldAuthorization(String fieldName) {
    	//fieldName.replaceAll("(\\[[0-9]*\\])", "");
    	if (authFields.containsKey(fieldName)) {
    		FieldAuthorization fieldAuth = new FieldAuthorization(fieldName, (String) authFields.get(fieldName));
    		if(fieldFormatters.containsKey(fieldName)){
    			fieldAuth.setMaskFormatter((MaskFormatter) fieldFormatters.get(fieldName));
    		}
    		return fieldAuth;
        }
        else {
            return new FieldAuthorization(fieldName, Field.EDITABLE);
        }
    }
    
    public void clearAllRestrictions() {
    	authFields.clear();
    	fieldFormatters.clear();
    	hiddenSectionIds.clear();
    	readOnlySectionIds.clear();
    }

    private void addFieldFormatter(String fieldName, MaskFormatter maskFormatter) {
    	fieldFormatters.put(fieldName, maskFormatter);
    }
    
}
