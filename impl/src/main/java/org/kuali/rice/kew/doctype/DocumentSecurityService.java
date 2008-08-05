package org.kuali.rice.kew.doctype;

import org.kuali.rice.kew.docsearch.DocSearchVO;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.web.session.UserSession;


public interface DocumentSecurityService {
  public boolean docSearchAuthorized(UserSession userSession, DocSearchVO docSearchVO, SecuritySession session);
  public boolean routeLogAuthorized(UserSession userSession, DocumentRouteHeaderValue routeHeader, SecuritySession session);
}
