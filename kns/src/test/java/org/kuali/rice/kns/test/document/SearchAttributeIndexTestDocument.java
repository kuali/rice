/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.kns.test.document;

import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.workflow.SearchAttributeIndexRequestTest;

/**
 * This is a description of what this class does - jksmith don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SearchAttributeIndexTestDocument extends TransactionalDocumentBase {
	static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SearchAttributeIndexRequestTest.class);
	private static final long serialVersionUID = -2290510385815271758L;
	private int routeLevelCount = 0;
	private String constantString;
	private String routedString;
	private String heldRoutedString;
	private int readAccessCount = 0;
	
	/**
	 * Constructor for the document which sets the constant string and keeps a hole of the routedString
	 * @param constantString the constant String to set
	 * @param routedString the routed String to hold on to, but not set until routing has occurred
	 */
	public void initialize(String constantString, String routedString) {
		this.constantString = constantString;
		this.heldRoutedString = routedString;
	}
	
	/**
	 * @return the count of how many route levels have been passed
	 */
	public int getRouteLevelCount() {
		readAccessCount += 1;
		return routeLevelCount;
	}
	
	/**
	 * @return a constant String
	 */
	public String getConstantString() {
		return constantString;
	}
	
	/**
	 * @return a routed String
	 */
	public String getRoutedString() {
		return routedString;
	}
	
	/**
	 * @return the readAccessCount
	 */
	public int getReadAccessCount() {
		return this.readAccessCount;
	}

	/**
	 * Overridden to make the document state change as route levels occur
	 * 
	 * @see org.kuali.rice.kns.document.DocumentBase#doRouteLevelChange(org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO)
	 */
	@Override
	public void doRouteLevelChange(DocumentRouteLevelChangeDTO levelChangeEvent) {
		super.doRouteLevelChange(levelChangeEvent);
		routeLevelCount += 1;
		if (routedString == null) {
			routedString = heldRoutedString;
		}
		LOG.info("Performing route level change on SearchAttributeIndexTestDocument; routeLevelCount is "+routeLevelCount);
	}
	
}
