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
package edu.iu.uis.eden.notes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * A servlet which can be used to retrieve attachments from Notes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AttachmentServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1918858512573502697L;
	public static final String ATTACHMENT_ID_KEY = "attachmentId";


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long attachmentId = new Long(request.getParameter(ATTACHMENT_ID_KEY));
		if (attachmentId == null) {
			throw new ServletException("No 'attachmentId' was specified.");
		}
		NoteService noteService = KEWServiceLocator.getNoteService(); 
		Attachment attachment = noteService.findAttachment(attachmentId);
		File file = noteService.findAttachmentFile(attachment);
		response.setContentLength((int)file.length());
		response.setContentType(attachment.getMimeType());
		response.setHeader("Content-disposition", "attachment; filename="+attachment.getFileName());
		FileInputStream attachmentFile = new FileInputStream(file);
		BufferedInputStream inputStream = new BufferedInputStream(attachmentFile);
		OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		
		int bytesWritten = 0;
		try {
			int c;
			while ((c = inputStream.read()) != -1) {
				outputStream.write(c);
				bytesWritten++;
			}
		} finally {
			inputStream.close();
		}
		outputStream.close();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}	
	
}
