/*
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
package edu.iu.uis.eden.clientapp;

import edu.iu.uis.eden.IDocumentEvent;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * Signal to the PostProcessor that the routeHeader is being deleted.
 * 
 */
public class DeleteEvent implements IDocumentEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1991987156524201870L;

	private String appDocId;

	private Long routeHeaderId;

	public DeleteEvent(Long routeHeaderId, String appDocId) {
		this.routeHeaderId = routeHeaderId;
		this.appDocId = appDocId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iu.uis.eden.IDocumentEvent#getDocumentEventCode()
	 */
	public String getDocumentEventCode() {
		return DELETE_CHANGE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iu.uis.eden.IDocumentEvent#getRouteHeaderId()
	 */
	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iu.uis.eden.IDocumentEvent#getAppDocId()
	 */
	public String getAppDocId() {
		return this.appDocId;
	}
}

/*
 * Copyright 2003 The Trustees of Indiana University. All rights reserved.
 * 
 * This file is part of the EDEN software package. For license information, see
 * the LICENSE file in the top level directory of the EDEN source distribution.
 */
