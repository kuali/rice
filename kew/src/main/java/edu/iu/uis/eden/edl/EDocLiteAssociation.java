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

import java.io.Serializable;

/**
 * Association between WorkflowDocument type -&gt; EDocLite definition, EDocLite style
 * Table: en_edoclt_assoc_t
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDocLiteAssociation implements Serializable{

	private static final long serialVersionUID = 7300251507982374010L;
	/**
     * edoclt_assoc_id
     */
    private Long eDocLiteAssocId;
    /**
     * edoclt_assoc_doctype_nm
     */
    private String edlName;
    /**
     * edoclt_assoc_def_nm
     */
    private String definition;
    /**
     * edoclt_assoc_style_nm
     */
    private String style;
    /**
     * edoclt_assoc_actv_ind
     */
    private Boolean activeInd;
    /**
     * db_lock_ver_nbr
     */
    private Integer lockVerNbr;
    private String actionsUrl;//for quickfinder

    public Long getEDocLiteAssocId() {
        return eDocLiteAssocId;
    }
    public void setEDocLiteAssocId(Long docLiteAssocId) {
        eDocLiteAssocId = docLiteAssocId;
    }
    public String getEdlName() {
        return edlName;
    }
    public void setEdlName(String edlName) {
        this.edlName = edlName;
    }
    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }
    public Boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public String toString() {
        return "[EDocLiteAssociation: eDocLiteAssocId=" + eDocLiteAssocId
                                 + ", edlName=" + edlName
                                 + ", definition=" + definition
                                 + ", style=" + style
                                 + ", activeInd=" + activeInd
                                 + ", lockVerNbr=" + lockVerNbr
                                 + "]";
    }
	public String getActionsUrl() {
		return actionsUrl;
	}
	public void setActionsUrl(String actionsUrl) {
		this.actionsUrl = actionsUrl;
	}
}