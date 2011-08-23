/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.service.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.util.KEWWebServiceConstants;

import javax.jws.WebService;

@SuppressWarnings({"unchecked"})
@WebService(endpointInterface = KEWWebServiceConstants.WorkflowUtility.INTERFACE_CLASS,
        serviceName = KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_NAME,
        portName = KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_PORT,
        targetNamespace = KEWWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class WorkflowUtilityWebServiceImpl implements WorkflowUtility {

    private static final Logger LOG = Logger.getLogger(WorkflowUtilityWebServiceImpl.class);

    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        return performDocumentSearchWithPrincipal(null, criteriaVO);
    }

    public DocumentSearchResultDTO performDocumentSearchWithPrincipal(String principalId, DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        DocSearchCriteriaDTO criteria = DTOConverter.convertDocumentSearchCriteriaDTO(criteriaVO);
        criteria.setOverridingUserSession(true);
        if (principalId != null) {
        	KEWServiceLocator.getIdentityHelperService().validatePrincipalId(principalId);
        } else {
        	// if the principal is null then we need to use the system "kr" user for execution of the search
        	principalId = KEWServiceLocator.getIdentityHelperService().getSystemPrincipal().getPrincipalId();
        }
        DocumentSearchResultComponents components = KEWServiceLocator.getDocumentSearchService().getListRestrictedByCriteria(principalId, criteria);
        DocumentSearchResultDTO resultVO = DTOConverter.convertDocumentSearchResultComponents(components);
        resultVO.setOverThreshold(criteria.isOverThreshold());
        resultVO.setSecurityFilteredRows(Integer.valueOf(criteria.getSecurityFilteredRows()));
        return resultVO;
    }

}
