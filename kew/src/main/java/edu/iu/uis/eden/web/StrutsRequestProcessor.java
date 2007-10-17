/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A RequestProcessor implementation for Struts which handles saving and retrieving
 * {@link ActionForm}s when leaving one context in the web GUI for another and then
 * returning.  This uses the {@link #DOC_FORM_KEY_ATTRIBUTE} to store the saved forms.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StrutsRequestProcessor extends RequestProcessor {

    private static final String REFRESH_MAPPING_PREFIX = "/Refresh";
    private static final String METHOD_PARAM = "methodToCall";
    private static final String DOC_FORM_KEY_ATTRIBUTE = "docFormKey";

    @Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	ModuleContext.setKew(true);
    	try {
    		if (isConvertNullWorkaroundNeeded()) {
    			ConverterUtils.registerContextSensitiveConverters();
    		}
    		super.process(request, response);
    	} finally {
    		ModuleContext.setKew(false);
    	}
	}

    /**
	 * In KEW we set the convertNull property to "true" on the Struts ActionServlet.  However, if an application which is running
	 * KEW as an embedded Struts Module has it set to false or is using some customizations to BeanUtils this will
	 * cause problems.  We need to detect when this case occurs, then register context sensitive converters which will convert
	 * properly inside of the KEW web application.
	 */
    protected boolean isConvertNullWorkaroundNeeded() {
    	return ConvertUtils.convert("", Long.class) != null;
    }

	protected ActionForm processActionForm(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) {
    	UserSession userSession = (UserSession) request.getSession().getAttribute(EdenConstants.USER_SESSION_KEY);
        if ((request.getParameter(DOC_FORM_KEY_ATTRIBUTE) != null && request.getParameter(DOC_FORM_KEY_ATTRIBUTE).length() > 0) && (mapping.getPath().startsWith(REFRESH_MAPPING_PREFIX) || "refresh".equalsIgnoreCase(request.getParameter(METHOD_PARAM)))) {
            if (userSession.retrieveObject(request.getParameter(DOC_FORM_KEY_ATTRIBUTE)) != null) {
                ActionForm form = (ActionForm) userSession.retrieveObject(request.getParameter(DOC_FORM_KEY_ATTRIBUTE));
                request.setAttribute(mapping.getName(), form);
                return form;
            } else {
                return null;
            }
        } else {
            return super.processActionForm(request, response, mapping);
        }
    }

}
