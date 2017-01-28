/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ForwardAction subclass that interprets path as module-relative 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated KNS Struts deprecated, use KRAD and the Spring MVC framework.
 */
@Deprecated
public class ForwardWithQueryParametersAction extends Action {
	
	public ActionForward execute(
	        ActionMapping mapping,
	        ActionForm form,
	        HttpServletRequest request,
	        HttpServletResponse response)
	        throws Exception {

	        String path = mapping.getParameter();
	        if (request.getQueryString() != null) {
	        	path = path + "?" + request.getQueryString();
	        }
	        ActionForward retVal = new ActionForward(path);
	        retVal.setModule("");

	        return retVal;
	    }    

}
