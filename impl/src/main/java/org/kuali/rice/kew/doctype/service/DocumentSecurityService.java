package org.kuali.rice.kew.doctype.service;

import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.web.session.UserSession;


public interface DocumentSecurityService {
  public boolean docSearchAuthorized(UserSession userSession, DocSearchDTO docCriteriaDTO, SecuritySession session);
  public boolean routeLogAuthorized(UserSession userSession, DocumentRouteHeaderValue routeHeader, SecuritySession session);
}
