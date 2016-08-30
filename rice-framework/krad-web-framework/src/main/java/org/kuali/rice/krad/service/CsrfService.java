package org.kuali.rice.krad.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ewestfal on 8/29/16.
 */
public interface CsrfService {

    boolean validateCsrfIfNecessary(HttpServletRequest request, HttpServletResponse response);

    String getSessionToken(HttpServletRequest request);

}
