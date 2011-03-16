/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.SecurityAttribute;

import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;


/**
 * This is a test class to verify the operation of the custom security attributes
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CustomSecurityFilterAttribute implements SecurityAttribute {

    private static final long serialVersionUID = -8487944372203594080L;

    public static final Map<String,String> VIEWERS_BY_STATUS = new HashMap<String,String>();
    static {
        VIEWERS_BY_STATUS.put(KEWConstants.ROUTE_HEADER_SAVED_CD, "user3");
        VIEWERS_BY_STATUS.put(KEWConstants.ROUTE_HEADER_ENROUTE_CD, "dewey");
        VIEWERS_BY_STATUS.put(KEWConstants.ROUTE_HEADER_PROCESSED_CD, "jitrue");
        VIEWERS_BY_STATUS.put(KEWConstants.ROUTE_HEADER_FINAL_CD, "user2");
    }

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#docSearchAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kew.user.WorkflowUser, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public Boolean docSearchAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {
        return checkAuthorizations(currentUser, docTypeName, documentId, initiatorPrincipalId);
    }

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#routeLogAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kew.user.WorkflowUser, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public Boolean routeLogAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {
        return checkAuthorizations(currentUser, docTypeName, documentId, initiatorPrincipalId);
    }

    private Boolean checkAuthorizations(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {
        try {
            WorkflowDocument doc = new WorkflowDocument(currentUser.getPrincipalId(),documentId);
            String networkId = VIEWERS_BY_STATUS.get(doc.getRouteHeader().getDocRouteStatus());
            return ( (StringUtils.isNotBlank(networkId)) && (networkId.equals(currentUser.getPrincipalName())) );
        } catch (Exception e) {
            throw new RuntimeException("Unable to process custom security filter attribute", e);
        }
    }
}
