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
package org.kuali.rice.kew.notes.web;

import org.apache.log4j.Logger;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.util.KNSConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;




/**
 * A servlet which can be used to retrieve attachments from Notes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttachmentServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1918858512573502697L;
	public static final String ATTACHMENT_ID_KEY = "attachmentId";

	// TODO This should probably be put into KEWConstants when contributed back
	// to Rice 1.0.3
	private static final Logger LOG = Logger.getLogger(AttachmentServlet.class);
			
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long attachmentId = new Long(request.getParameter(ATTACHMENT_ID_KEY));
		if (attachmentId == null) {
			throw new ServletException("No 'attachmentId' was specified.");
		}
		
		boolean secureChecks = true;
		String secureAttachmentsParam = null;
		try {
			secureAttachmentsParam = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, "All", KEWConstants.SECURE_ATTACHMENTS_PARAM);
		} catch (Exception e) {
			LOG.info("Attempted to retrieve parameter value, but could not. Defaulting to unsecured attachment retrieval. " + e.getMessage());
		}
		if (secureAttachmentsParam != null && secureAttachmentsParam.equals("N")) {
			secureChecks = false;
		}
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY);
			if (userSession != null) {// If we can get a valid userSession object off the Http request...
				
				NoteService noteService = KEWServiceLocator.getNoteService(); 
				Attachment attachment = noteService.findAttachment(attachmentId);
				File file = noteService.findAttachmentFile(attachment);
				
				DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(noteService.getNoteByNoteId(attachment.getNoteId()).getRouteHeaderId());
				
				if(!secureChecks || routeHeader != null){// If we can get a valid routeHeader based on the requested attachment ID
					boolean authorized = KEWServiceLocator.getDocumentSecurityService().routeLogAuthorized(userSession, routeHeader, new SecuritySession(userSession));
					
					if(!secureChecks || authorized){// If this user can see this document, they can get the attachment(s)
						response.setContentLength((int)file.length());
						response.setContentType(attachment.getMimeType());
						response.setHeader("Content-disposition", "attachment; filename="+attachment.getFileName());
						FileInputStream attachmentFile = new FileInputStream(file);
						BufferedInputStream inputStream = new BufferedInputStream(attachmentFile);
						OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

						try {
							int c;
							while ((c = inputStream.read()) != -1) {
								outputStream.write(c);
							}
						} finally {
							inputStream.close();
						}
						outputStream.close();
					} else {// Throw a forbidden page back, they were not approved by DocumentSecurityService
						LOG.error("Attempt to access attachmentId:"+ attachmentId + " from routeHeaderId:" + routeHeader.getRouteHeaderId() + " from unauthorized user: " + userSession.getPrincipalId());
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
						return;
					}
				} else {// Throw a not found, couldn't get a valid routeHeader
					LOG.error("Caught Null Pointer trying to determine routeHeader for requested attachmentId:" + attachmentId);
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} else {// Throw a bad request, we couldn't find a valid user session
				LOG.error("Attempt to access attachmentId:" + attachmentId + " with invalid UserSession");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} catch (Exception e) {// Catch any error, log it. Send a not found, and throw up the exception.
			LOG.error("Problem retrieving requested attachmentId:" + attachmentId, e);
			throw new WorkflowRuntimeException(e);
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}	
	
}
