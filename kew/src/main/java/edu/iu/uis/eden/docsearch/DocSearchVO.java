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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.web.KeyValueSort;
import edu.iu.uis.eden.web.RowStyleable;

/**
 * Bean that representing a row displayed in a document search.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocSearchVO implements Serializable, RowStyleable {

	private static final long serialVersionUID = 7850758046316186962L;
	private static String UNKNOWN_ROUTING_STATUS = "UNKNOWN";
    private static final String URL_SUFFIX = "?" + IDocHandler.COMMAND_PARAMETER + "=" + IDocHandler.DOCSEARCH_COMMAND + "&" + IDocHandler.ROUTEHEADER_ID_PARAMETER + "=";
    private static final String ROUTE_LOG_URL = "EdenServices/GetRouteHeader.do?docId=";

	private Long routeHeaderId;
	private String docRouteStatusCode;
	private java.sql.Timestamp dateCreated;
	private String documentTitle;
	private String activeIndicatorCode;
  private String docTypeName;
	private String docTypeLabel;
	private String initiatorNetworkId;
	private String initiatorWorkflowId;
	private String initiatorName;
	private String initiatorEmailAddress;
	private String initiatorLastName;
	private String initiatorFirstName;
    private String initiatorTransposedName;
	private String docTypeHandlerUrl;
	private String rowStyleClass;
	private String superUserSearch;

	private List searchableAttributes = new ArrayList();

	public DocSearchVO() {
	}

	public String getSuperUserSearch() {
        return superUserSearch;
    }
    public void setSuperUserSearch(String superUserSearch) {
        this.superUserSearch = superUserSearch;
    }
	public String getDocRouteStatusCode() {
		return docRouteStatusCode;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public void setDocRouteStatusCode(String docRouteStatusCode) {
		this.docRouteStatusCode = docRouteStatusCode;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

	public String getActiveIndicatorCode() {
		return activeIndicatorCode;
	}

	public String getDocTypeName() {
    return docTypeName;
  }

  public void setDocTypeName(String docTypeName) {
    this.docTypeName = docTypeName;
  }

  public String getDocTypeLabel() {
		return docTypeLabel;
	}

	public String getDocTypeHandlerUrl() {
		return docTypeHandlerUrl;
	}

	public String getInitiatorWorkflowId() {
		return initiatorWorkflowId;
	}

	public String getInitiatorEmailAddress() {
		return initiatorEmailAddress;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setActiveIndicatorCode(String activeIndicatorCode) {
		this.activeIndicatorCode = activeIndicatorCode;
	}

	public void setDocTypeLabel(String docTypeLabel) {
		this.docTypeLabel = docTypeLabel;
	}

	public void setDocTypeHandlerUrl(String docTypeHandlerUrl) {
		this.docTypeHandlerUrl = docTypeHandlerUrl;
	}

	public void setInitiatorWorkflowId(String initiatorEmplId) {
		this.initiatorWorkflowId = initiatorEmplId;
	}

	public void setInitiatorEmailAddress(String initiatorEmailAddress) {
		this.initiatorEmailAddress = initiatorEmailAddress;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public java.sql.Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(java.sql.Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getInitiatorNetworkId() {
		return initiatorNetworkId;
	}

	public void setInitiatorNetworkId(String string) {
		initiatorNetworkId = string;
	}

	public String getInitiatorFirstName() {
		return initiatorFirstName;
	}

	public String getInitiatorLastName() {
		return initiatorLastName;
	}

	public void setInitiatorFirstName(String initiatorFirstName) {
		this.initiatorFirstName = initiatorFirstName;
	}

	public void setInitiatorLastName(String initiatorLastName) {
		this.initiatorLastName = initiatorLastName;
	}

    public String getDocRouteStatusCodeDesc() {
        if (this.docRouteStatusCode == null || edu.iu.uis.eden.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode) == null || "".equalsIgnoreCase(edu.iu.uis.eden.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode))) {
            return UNKNOWN_ROUTING_STATUS;
        } else {
            return edu.iu.uis.eden.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode);
        }
    }

    public String getDocHandlerUrl() {
        if ("".equals(getDocTypeHandlerUrl())) {
            return "";
        } else {
            if (isUsingSuperUserSearch()) {
                return "SuperUser.do?command=displayFrame&routeHeaderId=" + this.getRouteHeaderId().toString();
            } else {
                return this.getDocTypeHandlerUrl() + URL_SUFFIX + this.getRouteHeaderId().toString();
            }
        }
    }

    public boolean isUsingSuperUserSearch() {
    	return "YES".equalsIgnoreCase(superUserSearch);
    }

    public String getRouteLogUrl() {
        return ROUTE_LOG_URL + this.routeHeaderId.toString();
    }

    public String getRowStyleClass() {
        return rowStyleClass;
    }

    public void setRowStyleClass(String rowStyleClass) {
        this.rowStyleClass = rowStyleClass;
    }

    public String getInitiatorTransposedName() {
        return initiatorTransposedName;
    }

    public void setInitiatorTransposedName(String initiatorTransposedName) {
        this.initiatorTransposedName = initiatorTransposedName;
    }

    /**
     * Method for the JSP to use to pull in searchable attributes by name
     * instead of by index location which is unreliable
     *
     * @param key Key of KeyValueSort trying to be retrieved
     * @return  the matching KeyValueSort in list of searchable attributes or an empty KeyValueSort
     */
    public KeyValueSort getSearchableAttribute(String key) {
    	KeyValueSort returnPair = new KeyValueSort("","","",null);
    	if (key == null) {
    		return returnPair;
    	}
    	for (Iterator iter = searchableAttributes.iterator(); iter.hasNext();) {
    		KeyValueSort pair = (KeyValueSort) iter.next();
			if (key.equals((String)pair.getKey())) {
				returnPair = pair;
				break;
			}
		}
    	return returnPair;
    }

    public void addSearchableAttribute(KeyValueSort searchableAttribute) {
    	searchableAttributes.add(searchableAttribute);
    }
	/**
	 * @param searchableAttributes The searchableAttributes to set.
	 */
	public void setSearchableAttributes(List searchableAttributes) {
		this.searchableAttributes = searchableAttributes;
	}
	/**
	 * @return Returns the searchableAttributes.
	 */
	public List getSearchableAttributes() {
		return searchableAttributes;
	}
}
