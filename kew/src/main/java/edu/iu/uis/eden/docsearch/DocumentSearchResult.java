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
package edu.iu.uis.eden.docsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.web.KeyValueSort;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchResult {

	public static final String PROPERTY_NAME_ROUTE_HEADER_ID = "routeHeaderId";
	public static final String PROPERTY_NAME_DOC_TYPE_LABEL = "docTypeLabel";
	public static final String PROPERTY_NAME_DOCUMENT_TITLE = "documentTitle";
	public static final String PROPERTY_NAME_ROUTE_STATUS_DESC = "docRouteStatusCodeDesc"; 
	public static final String PROPERTY_NAME_INITIATOR = "initiator";
	public static final String PROPERTY_NAME_DATE_CREATED = "dateCreated";
	public static final String PROPERTY_NAME_ROUTE_LOG = "routeLog";
	
	public static final Set<String> PROPERTY_NAME_SET = new HashSet<String>();
	static {
		PROPERTY_NAME_SET.add(PROPERTY_NAME_ROUTE_HEADER_ID);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_DOC_TYPE_LABEL);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_DOCUMENT_TITLE);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_ROUTE_STATUS_DESC);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_INITIATOR);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_DATE_CREATED);
		PROPERTY_NAME_SET.add(PROPERTY_NAME_ROUTE_LOG);
	}
	
	private List<KeyValueSort> resultContainers = new ArrayList<KeyValueSort>();

	/**
	 * @return the resultContainers
	 */
	public List<KeyValueSort> getResultContainers() {
		return resultContainers;
	}

	/**
	 * @param resultContainers the resultContainers to set
	 */
	public void setResultContainers(List<KeyValueSort> resultContainers) {
		this.resultContainers = resultContainers;
	}

	/**
	 * @param result - a KeyValueSort object to add to the list 
	 */
	public void addResultContainer(KeyValueSort result) {
		this.resultContainers.add(result);
	}
	
    /**
     * Method for the JSP to use to pull in a search result by name
     * instead of by index location which is unreliable
     * 
     * @param key - Key of KeyLabelSort trying to be retrieved
     * @return  the matching KeyLabelSort in list of searchable attributes or an empty KeyLabelSort
     */
    public KeyValueSort getResultContainer(String key) {
    	if (key != null) {
	    	for (Iterator iter = resultContainers.iterator(); iter.hasNext();) {
				KeyValueSort pair = (KeyValueSort) iter.next();
				if (key.equals((String)pair.getKey())) {
					return pair;
				}
			}
    	}
    	return new KeyValueSort();
    }
}
