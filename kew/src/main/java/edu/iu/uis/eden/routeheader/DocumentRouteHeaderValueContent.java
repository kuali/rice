/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.routeheader;

import java.io.Serializable;

public class DocumentRouteHeaderValueContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long routeHeaderId;
	private String documentContent;
		
	public DocumentRouteHeaderValueContent() {}
	
	public DocumentRouteHeaderValueContent(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}
	
	public String getDocumentContent() {
		return documentContent;
	}
	public void setDocumentContent(String documentContent) {
		this.documentContent = documentContent;
	}
	public Long getRouteHeaderId() {
		return routeHeaderId;
	}
	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

}
