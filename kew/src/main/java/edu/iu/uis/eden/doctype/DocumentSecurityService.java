package edu.iu.uis.eden.doctype;

import edu.iu.uis.eden.docsearch.DocSearchVO;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.web.session.UserSession;

public interface DocumentSecurityService {
  public boolean docSearchAuthorized(UserSession userSession, DocSearchVO docSearchVO, SecuritySession session);
  public boolean routeLogAuthorized(UserSession userSession, DocumentRouteHeaderValue routeHeader, SecuritySession session);
}
