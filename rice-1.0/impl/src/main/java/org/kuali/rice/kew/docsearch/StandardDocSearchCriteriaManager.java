/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class to hold management capabilities for standard document search. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StandardDocSearchCriteriaManager implements java.io.Serializable {
	
	private static final long serialVersionUID = -1898076444679158722L;
	
    private List<List<StandardDocSearchCriteriaFieldContainer>> columnsPreSearchAttributes = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
	private List<List<StandardDocSearchCriteriaFieldContainer>> columnsPostSearchAttributes = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
	
	public StandardDocSearchCriteriaManager(int preSearchAttributeColumns, int postSearchAttributeColumns, boolean searchCriteriaDisplayed, boolean headerBarDisplayed) {
		setupColumns(preSearchAttributeColumns,postSearchAttributeColumns);
	}
	
	private void setupColumns(int preSearchAttributeColumns, int postSearchAttributeColumns) {
		for (int i = 0; i < preSearchAttributeColumns; i++) {
			columnsPreSearchAttributes.add(new ArrayList<StandardDocSearchCriteriaFieldContainer>());
		}
		for (int i = 0; i < postSearchAttributeColumns; i++) {
			columnsPostSearchAttributes.add(new ArrayList<StandardDocSearchCriteriaFieldContainer>());
		}
	}
	
	public List<StandardDocSearchCriteriaFieldContainer> getPreSearchAttributeMaxRows() {
		return getMaxRowsForList(columnsPreSearchAttributes);
	}
	
	public List<StandardDocSearchCriteriaFieldContainer> getPostSearchAttributeMaxRows() {
		return getMaxRowsForList(columnsPostSearchAttributes);
	}
	
	@SuppressWarnings("unchecked")
	private List<StandardDocSearchCriteriaFieldContainer> getMaxRowsForList(List<List<StandardDocSearchCriteriaFieldContainer>> columns) {
		List<StandardDocSearchCriteriaFieldContainer> biggestFieldContainerList = null;
		for (Iterator iter = columns.iterator(); iter.hasNext();) {
			List<StandardDocSearchCriteriaFieldContainer> fieldContainerList = (List<StandardDocSearchCriteriaFieldContainer>) iter.next();
			if (biggestFieldContainerList == null) {
				biggestFieldContainerList = fieldContainerList;
			} else {
				if (fieldContainerList.size() > biggestFieldContainerList.size()) {
					biggestFieldContainerList = fieldContainerList;
				}
			}
		}
		return biggestFieldContainerList;
	}
	
	public int getMaxColumnCount() {
		return Math.max(columnsPreSearchAttributes.size(), columnsPostSearchAttributes.size());
	}

    public List<List<StandardDocSearchCriteriaFieldContainer>> getColumnsPreSearchAttributes() {
		return this.columnsPreSearchAttributes;
	}

	public void setColumnsPreSearchAttributes(List<List<StandardDocSearchCriteriaFieldContainer>> columnsPreSearchAttributes) {
		this.columnsPreSearchAttributes = columnsPreSearchAttributes;
	}

	public List<List<StandardDocSearchCriteriaFieldContainer>> getColumnsPostSearchAttributes() {
		return this.columnsPostSearchAttributes;
	}

	public void setColumnsPostSearchAttributes(List<List<StandardDocSearchCriteriaFieldContainer>> columnsPostSearchAttributes) {
		this.columnsPostSearchAttributes = columnsPostSearchAttributes;
	}
	
}
