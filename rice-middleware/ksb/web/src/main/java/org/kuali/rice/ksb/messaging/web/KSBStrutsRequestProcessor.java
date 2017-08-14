/**
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.web;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.action.RequestProcessor;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.CsrfValidator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A RequestProcessor implementation for Struts which handles determining whether or not access
 * should be allowed to the requested KSB page.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KSBStrutsRequestProcessor extends RequestProcessor {

	private static Logger LOG = Logger.getLogger(KSBStrutsRequestProcessor.class);

	private static final String CSRF_PARAMETER = "csrfToken";
	private static final String CSRF_SESSION_TOKEN = "csrfSessionToken";

	@Override
	protected boolean processPreprocess(HttpServletRequest request,
										HttpServletResponse response) {
		final UserSession session = KRADUtils.getUserSessionFromRequest(request);

		if (session == null) {
			throw new IllegalStateException("the user session has not been established");
		}

		GlobalVariables.setUserSession(session);
		GlobalVariables.clear();
		return super.processPreprocess(request, response);
	}

	@Override
	protected boolean processValidate(HttpServletRequest request, HttpServletResponse response, ActionForm form, ActionMapping mapping) throws IOException, ServletException, InvalidCancelException {
		// need to make sure that we don't check CSRF until after the form is populated so that Struts will parse the
		// multipart parameters into the request if it's a multipart request
		if (!CsrfValidator.validateCsrf(request, response)) {
			try {
				return false;
			} finally {
				// Special handling for multipart request
				if (form.getMultipartRequestHandler() != null) {
					if (log.isTraceEnabled()) {
						log.trace("  Rolling back multipart request");
					}

					form.getMultipartRequestHandler().rollback();
				}
			}
		}

		return super.processValidate(request, response, form, mapping);
	}

}
