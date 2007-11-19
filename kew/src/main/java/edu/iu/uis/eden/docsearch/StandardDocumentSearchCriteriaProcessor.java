/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.docsearch;

import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * This is the standard document search criteria processor implementation. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StandardDocumentSearchCriteriaProcessor implements DocumentSearchCriteriaProcessor {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardDocumentSearchCriteriaProcessor.class);

	private DocSearchCriteriaVO docSearchCriteriaVO;
    private WorkflowUser searchingUser;
	
	/**
	 * @return the docSearchCriteriaVO
	 */
	public DocSearchCriteriaVO getDocSearchCriteriaVO() {
		return this.docSearchCriteriaVO;
	}

	/**
	 * @param docSearchCriteriaVO the docSearchCriteriaVO to set
	 */
	public void setDocSearchCriteriaVO(DocSearchCriteriaVO docSearchCriteriaVO) {
		this.docSearchCriteriaVO = docSearchCriteriaVO;
	}

	/**
	 * @return the searchingUser
	 */
	public WorkflowUser getSearchingUser() {
		return this.searchingUser;
	}

	/**
	 * @param searchingUser the searchingUser to set
	 */
	public void setSearchingUser(WorkflowUser searchingUser) {
		this.searchingUser = searchingUser;
	}

	/**
	 * Standard implementation of this method is that the header bar is always displayed
	 * so this returns true.
	 * 
	 * @see edu.iu.uis.eden.docsearch.DocumentSearchCriteriaProcessor#isHeaderBarDisplayed()
	 */
	public boolean isHeaderBarDisplayed() {
		return true;
	}

	/**
	 * Standard implementation of this method is that the search criteria is always displayed
	 * so this returns true.
	 * 
	 * @see edu.iu.uis.eden.docsearch.DocumentSearchCriteriaProcessor#isSearchCriteriaDisplayed()
	 */
	public Boolean isSearchCriteriaDisplayed() {
		return Boolean.TRUE;
	}
	
	

}
