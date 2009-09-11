/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.SecurityAttribute;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentSecurityService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KeyValue;
import org.kuali.rice.kew.web.session.Authentication;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;


public class DocumentSecurityServiceImpl implements DocumentSecurityService {
  public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSecurityServiceImpl.class);

  public boolean docSearchAuthorized(UserSession userSession, DocSearchDTO docCriteriaDTO, SecuritySession session) {
      return checkAuthorization(userSession, session, docCriteriaDTO.getDocTypeName(), docCriteriaDTO.getRouteHeaderId(), docCriteriaDTO.getInitiatorWorkflowId());
  }

  public boolean routeLogAuthorized(UserSession userSession, DocumentRouteHeaderValue routeHeader, SecuritySession session) {
      return checkAuthorization(userSession, session, routeHeader.getDocumentType().getName(), routeHeader.getRouteHeaderId(), routeHeader.getInitiatorWorkflowId());
  }

  protected boolean checkAuthorization(UserSession userSession, SecuritySession session, String documentTypeName, Long routeHeaderId, String initiatorPrincipalId) {
      DocumentTypeSecurity security = getDocumentTypeSecurity(userSession, documentTypeName, session);
      if (security == null || !security.isActive()) {
        // Security is not enabled for this doctype.  Everyone can see this doc.
        return true;
      }
      if (isAdmin(session)) {
          return true;
      }
      for (SecurityAttribute securityAttribute : security.getSecurityAttributes()) {
          Boolean authorized = securityAttribute.docSearchAuthorized(userSession.getPerson(), documentTypeName, routeHeaderId, initiatorPrincipalId);
          if (authorized != null) {
              return authorized.booleanValue();
          }
      }
      return checkStandardAuthorization(security, userSession, documentTypeName, routeHeaderId, initiatorPrincipalId, session);
  }

  protected boolean isAdmin(SecuritySession session) {
	  if (session.getUserSession() == null) {
		  return false;
	  }
	  return KIMServiceLocator.getIdentityManagementService().isAuthorized(session.getUserSession().getPrincipalId(), KEWConstants.KEW_NAMESPACE,	KEWConstants.PermissionNames.UNRESTRICTED_DOCUMENT_SEARCH, new AttributeSet(), new AttributeSet());
  }

  protected boolean checkStandardAuthorization(DocumentTypeSecurity security, UserSession userSession, String docTypeName, Long documentId, String initiatorPrincipalId, SecuritySession session) {
	Person user = userSession.getPerson();

    LOG.debug("auth check user=" + user.getPrincipalId() +" docId=" + documentId);

    // Doc Initiator Authorization
    if (security.getInitiatorOk() != null && security.getInitiatorOk()) {
      boolean isInitiator = StringUtils.equals(initiatorPrincipalId, user.getPrincipalId());
      if (isInitiator) {
        return true;
      }
    }

    // Role authorization
    List<String> allowedRoles = security.getAllowedRoles();
    List<String> disallowedRoles = security.getDisallowedRoles();
    // only execute role security if they have it defined
    if ((allowedRoles != null && !allowedRoles.isEmpty()) ||
    		(disallowedRoles != null && !disallowedRoles.isEmpty())) {
    	Boolean passesRoleSecurity = session.getPassesRoleSecurity().get(docTypeName);
    	if (passesRoleSecurity != null) {
    		if (passesRoleSecurity) {
    			return true;
    		}
    	} else {
    		passesRoleSecurity = isRoleAuthenticated(allowedRoles, disallowedRoles, userSession, session);
    		session.getPassesRoleSecurity().put(docTypeName, passesRoleSecurity);
    		if (passesRoleSecurity) {
    			return true;
    		}
    	}
    }

    //  Workgroup Authorization
    List<Group> securityWorkgroups = security.getWorkgroups();
    if (securityWorkgroups != null) {
      for (Group securityWorkgroup : securityWorkgroups) {
        if (isWorkgroupAuthenticated(securityWorkgroup.getNamespaceCode(), securityWorkgroup.getGroupName(), session)) {
        	return true;
        }
      }
    }

    // Searchable Attribute Authorization
    Collection searchableAttributes = security.getSearchableAttributes();
    if (searchableAttributes != null) {
      for (Iterator iterator = searchableAttributes.iterator(); iterator.hasNext();) {
        KeyValue searchableAttr = (KeyValue) iterator.next();
        String attrName = searchableAttr.getkey();
        String idType = searchableAttr.getvalue();
        String idValue = UserUtils.getIdValue(idType, user);
        if (!StringUtils.isEmpty(idValue)) {
          if (KEWServiceLocator.getRouteHeaderService().hasSearchableAttributeValue(documentId, attrName, idValue)) {
            return true;
          }
        }
      }
    }

    // Route Log Authorization
    if (security.getRouteLogAuthenticatedOk() != null && security.getRouteLogAuthenticatedOk()) {
      boolean isInitiator = StringUtils.equals(initiatorPrincipalId, user.getPrincipalId());
      if (isInitiator) {
        return true;
      }
      boolean hasTakenAction = KEWServiceLocator.getActionTakenService().hasUserTakenAction(user.getPrincipalId(), documentId);
      if (hasTakenAction) {
        return true;
      }
      boolean hasRequest = KEWServiceLocator.getActionRequestService().doesPrincipalHaveRequest(user.getPrincipalId(), documentId);
      if (hasRequest) {
        return true;
      }
    }

    LOG.debug("user not authorized");
    return false;
  }

  protected DocumentTypeSecurity getDocumentTypeSecurity(UserSession userSession, String documentTypeName, SecuritySession session) {
      if (session == null) {
          session = new SecuritySession(userSession);
      }
	  DocumentTypeSecurity security = session.getDocumentTypeSecurity().get(documentTypeName);
	  if (security == null) {
		  DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
		  security = docType.getDocumentTypeSecurity();
		  session.getDocumentTypeSecurity().put(documentTypeName, security);
	  }
	  return security;
  }

  protected boolean isWorkgroupAuthenticated(String namespace, String workgroupName, SecuritySession session) {
	  String key = namespace.trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + workgroupName.trim();
      Boolean existingAuth = session.getAuthenticatedWorkgroups().get(key);
	  if (existingAuth != null) {
		  return existingAuth;
	  }
	  boolean memberOfGroup = session.getUserSession().isMemberOfGroupWithName(namespace, workgroupName);
	  session.getAuthenticatedWorkgroups().put(key, memberOfGroup);
	  return memberOfGroup;
  }

  protected boolean isRoleAuthenticated(List<String> allowedRoles, List<String> disallowedRoles, UserSession userSession, SecuritySession session) {
	  boolean disallowed = false;
	  boolean allowed = false;
	  for (Iterator iterator = userSession.getAuthentications().iterator(); iterator.hasNext();) {
		  Authentication auth = (Authentication) iterator.next();
		  String role = auth.getAuthority();
		  if (disallowedRoles.contains(role)) {
			  disallowed = true;
		  }
		  if (allowedRoles.contains(role)) {
			  allowed = true;
		  }
	  }
	  if (allowed) {
		  // allowed takes precedence over disallowed
		  return true;
	  } else if (disallowed) {
		  // we know that we haven't been allowed at this point, if we're disallowed than we're not authenticated
		  return false;
	  } else if (allowedRoles.isEmpty()) {
		  // if allowedRoles is empty, that means that disallowed roles are not empty and we know because of the previous condition
		  // that the user has not been disallowed, therefore the user should be allowed if they aren't in the disallow set
		  return true;
	  }
	  return false;
  }

}
