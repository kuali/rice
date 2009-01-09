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
package org.kuali.rice.kew.docsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.SecurityAttribute;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.session.Authentication;
import org.kuali.rice.kim.bo.Person;


/**
 * This is a description of what this class does - delyea don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
    public Boolean docSearchAuthorized(DocumentTypeSecurity security, Person currentUser, List<Authentication> authentications, String docTypeName, Long documentId, String initiatorWorkflowId, SecuritySession session) {
        return checkAuthorizations(security, currentUser, authentications, docTypeName, documentId, initiatorWorkflowId, session);
    }

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#routeLogAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kew.user.WorkflowUser, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public Boolean routeLogAuthorized(DocumentTypeSecurity security, Person currentUser, List<Authentication> authentications, String docTypeName, Long documentId, String initiatorWorkflowId, SecuritySession session) {
        return checkAuthorizations(security, currentUser, authentications, docTypeName, documentId, initiatorWorkflowId, session);
    }

    private Boolean checkAuthorizations(DocumentTypeSecurity security, Person currentUser, List<Authentication> authentications, String docTypeName, Long documentId, String initiatorWorkflowId, SecuritySession session) {
        try {
            WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO(currentUser.getPrincipalName()),documentId);
            String networkId = VIEWERS_BY_STATUS.get(doc.getRouteHeader().getDocRouteStatus());
            return ( (StringUtils.isNotBlank(networkId)) && (networkId.equals(currentUser.getPrincipalName())) );
        } catch (Exception e) {
            throw new RuntimeException("Unable to process custom security filter attribute", e);
        }
    }
}
