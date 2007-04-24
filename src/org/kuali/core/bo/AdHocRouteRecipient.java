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
package org.kuali.core.bo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.util.CodeTranslator;

/**
 * TODO we should not be referencing eden constants from this class and wedding ourselves to that workflow application Ad Hoc Route
 * Recipient Business Object
 */
public abstract class AdHocRouteRecipient extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -6499610180752232494L;
    private static Map actionRequestCds = CodeTranslator.arLabels;
    public static final Integer PERSON_TYPE = new Integer(0);
    public static final Integer WORKGROUP_TYPE = new Integer(1);

    protected Integer type;
    protected String actionRequested;
    protected String id; // can be networkId or workgroupname
    protected String name;
    protected String documentNumber;

    public AdHocRouteRecipient() {
        // set some defaults that can be overridden
        this.actionRequested = EdenConstants.ACTION_REQUEST_APPROVE_REQ;
        this.versionNumber = new Long(1);
    }

    public String getActionRequested() {
        return actionRequested;
    }

    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
        
    public abstract String getName();

    public void setName( String name ) {
        // do nothing, assume names come from subclasses
    }
    
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    public void setdocumentNumber (String documentNumber){
        this.documentNumber = documentNumber;
    }
    
    public String getdocumentNumber (){
        return documentNumber;
    }

    public String getActionRequestedValue() {
        String actionRequestedValue = null;
        if (StringUtils.isNotBlank(getActionRequested())) {
            actionRequestedValue = (String) actionRequestCds.get(getActionRequested());
        }
        return actionRequestedValue;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("type", getType());
        m.put("actionRequested", getActionRequested());
        m.put("id", getId());

        return m;
    }
}