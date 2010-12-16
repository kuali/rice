/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kew.web.RowStyleable;


/**
 * Bean that representing a row displayed in a document search.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchDTO implements Serializable, RowStyleable {

	private static final long serialVersionUID = 7850758046316186962L;
	private static final String URL_SUFFIX = "?" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=";

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
	private String appDocStatus;

	private List<KeyValueSort> searchableAttributes = new ArrayList<KeyValueSort>();

	public DocSearchDTO() {
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

	public String getAppDocStatus() {
		return this.appDocStatus;
	}

	public void setAppDocStatus(String appDocStatus) {
		this.appDocStatus = appDocStatus;
	}

    public String getDocRouteStatusCodeDesc() {
        if (this.docRouteStatusCode == null || org.kuali.rice.kew.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode) == null || "".equalsIgnoreCase(org.kuali.rice.kew.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode))) {
            return KEWConstants.UNKNOWN_STATUS;
        } else {
            return org.kuali.rice.kew.util.CodeTranslator.getRouteStatusLabel(docRouteStatusCode);
        }
    }

    public String getDocHandlerUrl() {
        if (StringUtils.isBlank(getDocTypeHandlerUrl())) {
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
    	return DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase(superUserSearch);
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
        for (KeyValueSort searchableAttribute : searchableAttributes)
        {
            if (key.equals(searchableAttribute.getKey()))
            {
                returnPair = searchableAttribute;
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
	public void setSearchableAttributes(List<KeyValueSort> searchableAttributes) {
		this.searchableAttributes = searchableAttributes;
	}
	/**
	 * @return Returns the searchableAttributes.
	 */
	public List<KeyValueSort> getSearchableAttributes() {
		return searchableAttributes;
	}
}
