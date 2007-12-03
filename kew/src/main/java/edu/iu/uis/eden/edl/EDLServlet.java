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
package edu.iu.uis.eden.edl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Takes edl web requests.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EDLServlet extends HttpServlet {

	private static final long serialVersionUID = -6344765194278430690L;

	private static final Logger LOG = Logger.getLogger(EDLServlet.class);

	public void init() throws ServletException {
		try {
			KEWServiceLocator.getEDocLiteService().initEDLGlobalConfig();
		} catch (Exception e) {
			LOG.error("Error initializing EDL", e);
		}

	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    RequestParser requestParser = new RequestParser(request);
		    String edlName = requestParser.getParameterValue("edlName");
		    if (edlName == null) {
		        edlName = requestParser.getParameterValue("docTypeName");//this is for 'WorkflowQuicklinks'
		    }
		    EDLController edlController = null;
		    if (edlName == null) {
		        String documentId = requestParser.getParameterValue("docId");
		        if (documentId == null) {
		            throw new WorkflowRuntimeException("No edl name or document id detected");
		        }
		        edlController = KEWServiceLocator.getEDocLiteService().getEDLController(new Long(documentId));
		    } else {
		        edlController = KEWServiceLocator.getEDocLiteService().getEDLController(edlName);
		    }

		    EDLControllerChain controllerChain = new EDLControllerChain();
		    controllerChain.addEdlController(edlController);
		    response.setContentType("text/html; charset=UTF-8");
		    controllerChain.renderEDL(requestParser, response);

		} catch (Exception e) {
			LOG.error("Error processing EDL", e);
			outputError(request, response, e);
		}
	}

	private void outputError(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
	        request.setAttribute("WORKFLOW_ERROR", e);
	        RequestDispatcher rd = getServletContext().getRequestDispatcher(request.getServletPath() + "/../Error.do");
	        rd.forward(request, response);
	}

}