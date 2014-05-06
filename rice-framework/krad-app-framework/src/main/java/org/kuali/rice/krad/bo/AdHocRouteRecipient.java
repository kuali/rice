/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.util.CodeTranslator;


/**
 * Ad Hoc Route Recipient Business Object
 *
 * TODO we should not be referencing kew constants from this class and wedding ourselves to that workflow application
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("deprecation")
@MappedSuperclass
public class AdHocRouteRecipient implements Serializable {
    private static final long serialVersionUID = -6499610180752232494L;

    private static Map<String, String> actionRequestCds = null;
    public static final Integer PERSON_TYPE = new Integer(0);
    public static final Integer WORKGROUP_TYPE = new Integer(1);

    @Id
    @Column(name="DOC_HDR_ID",length=14)
    protected String documentNumber;

    @Id
	@Column(name="RECIP_TYP_CD",length=1)
	protected Integer type;

    @Id
	@Column(name="ACTN_RQST_CD",length=30)
	protected String actionRequested = KewApiConstants.ACTION_REQUEST_APPROVE_REQ;

    @Id
	@Column(name="ACTN_RQST_RECIP_ID",length=70)
	protected String id; // can be networkId or group id

    // This is just here so we don't need to change the data model
    @Column(name="OBJ_ID", length=36, nullable = false)
    @Deprecated
    private String objectId;

    @Transient
    @Deprecated
    private Integer versionNumber;

    @Transient
    protected String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            if ( actionRequestCds == null ) {
                Map<String,String> temp = new HashMap<String, String>();
                temp.putAll(CodeTranslator.arLabels);
                actionRequestCds = temp;
            }
            actionRequestedValue = (String) actionRequestCds.get(getActionRequested());
        }

        return actionRequestedValue;
    }

    @PrePersist
    @Deprecated
    public void prePersist() {
        objectId = UUID.randomUUID().toString();
    }

    @Deprecated
    public void refresh() {
        // Do nothing - just here since we needed to implement BusinessObject
    }

    @Deprecated
    public Integer getVersionNumber() {
        return this.versionNumber;
    }

    @Deprecated
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }
}
