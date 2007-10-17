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
package edu.iu.uis.eden.engine.node;

import java.io.Serializable;

import edu.iu.uis.eden.doctype.DocumentType;

/**
 * Represents a route path defined on a {@link DocumentType}.  A Process is a named entity which
 * simply points to an initial {@link RouteNode} which represents the beginning of the Process.
 * The path of the process can then be followed using the next nodes defined on the route nodes. 
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Process implements Serializable {

	private static final long serialVersionUID = -6338857095673479752L;
    
    private Long processId;
	private String name;
	private DocumentType documentType;
	private RouteNode initialRouteNode;
	private boolean initial = false;
	private Integer lockVerNbr;
	
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public RouteNode getInitialRouteNode() {
		return initialRouteNode;
	}
	public void setInitialRouteNode(RouteNode initialRouteNode) {
		this.initialRouteNode = initialRouteNode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isInitial() {
		return initial;
	}
	public void setInitial(boolean initial) {
		this.initial = initial;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
}