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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.ArrayUtils;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * An abstraction that allows for switching between multipart form requests and normal requests when getting
 * request params
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RequestParser {

	private static final String PARSED_MULTI_REQUEST_KEY = "__parsedRequestStruct";
	private static final String UPLOADED_FILES_KEY = "__uploadedFiles";
	
	public static final String WORKFLOW_DOCUMENT_SESSION_KEY = "workflowDocument";
	public static final String GLOBAL_ERRORS_KEY = "edu.iu.uis.eden.edl.GlobalErrors";
	public static final String GLOBAL_MESSAGES_KEY = "edu.iu.uis.eden.edl.GlobalMessages";
	public static final String GLOBAL_FIELD_ERRORS_KEY = "edu.iu.uis.eden.edl.GlobalFieldErrors";
	
	private HttpServletRequest request;
	
	public RequestParser(HttpServletRequest request) {
		this.request = request;
		// setup empty List of errors and messages
		request.setAttribute(GLOBAL_ERRORS_KEY, new ArrayList());
		request.setAttribute(GLOBAL_MESSAGES_KEY, new ArrayList());
		request.setAttribute(GLOBAL_FIELD_ERRORS_KEY, new HashMap<String, String>());
	}

	private static void parseRequest(HttpServletRequest request) {
		if (request.getAttribute(PARSED_MULTI_REQUEST_KEY) != null) {
			return;
		}
		
		Map requestParams = new HashMap();
		request.setAttribute(PARSED_MULTI_REQUEST_KEY, requestParams);
		
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(100);
		ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
		
		List items = null;
		try {
			items = upload.parseRequest(request);	
		} catch (FileUploadException fue) {
			throw new WorkflowRuntimeException(fue);
		}
		
		Iterator iter = items.iterator();
		while (iter.hasNext()) {

			FileItem item = (FileItem) iter.next();
			if (item.isFormField()) {
        		String fieldName = item.getFieldName();
        		String fieldValue = item.getString();
        		String[] paramVals = null;
				if (requestParams.containsKey(fieldName)) {
					paramVals = (String[])requestParams.get(fieldName);
					paramVals = (String[]) ArrayUtils.add(paramVals, fieldValue);
				} else {
					paramVals = new String[1];
					paramVals[0] = fieldValue;
				}
				requestParams.put(fieldName, paramVals);
			} else {
				List uploadedFiles = (List)request.getAttribute(UPLOADED_FILES_KEY);
				if (uploadedFiles == null) {
					uploadedFiles = new ArrayList();
					request.setAttribute(UPLOADED_FILES_KEY, uploadedFiles);
				}
				uploadedFiles.add(item);
			}
		}
	}
	
	public String[] getParameterValues(String paramName) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(this.getRequest());
		if (isMultipart) {
			parseRequest(this.getRequest());
			String[] params = (String[]) ((Map)request.getAttribute(PARSED_MULTI_REQUEST_KEY)).get(paramName);
			if (params == null) {
				return null;
			}
			return params;
		} else {
			return this.getRequest().getParameterValues(paramName);
		}
	}
	
	public void setAttribute(String name, Object value) {
		this.getRequest().setAttribute(name, value);
	}
	
	public Object getAttribute(String name) {
		return this.getRequest().getAttribute(name); 
	}
	
	public String getParameterValue(String paramName) {
		String[] paramVals = getParameterValues(paramName);
		if (paramVals != null) {
			return paramVals[0];
		}
		return null;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public List getUploadList() {
		return (List) request.getAttribute(UPLOADED_FILES_KEY);
	}
}