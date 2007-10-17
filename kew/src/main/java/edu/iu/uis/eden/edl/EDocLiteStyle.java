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

/**
 * EDocLite XSLT stylesheet
 * Table: en_edoclt_style_t
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDocLiteStyle {
    /**
     * edoclt_style_id
     */
    private Long eDocLiteStyleId;
    /**
     * edoclt_style_nm
     */
    private String name;
    /**
     * edoclt_style_xml
     */
    private String xmlContent;
    /**
     * edoclt_style_actv_ind
     */
    private Boolean activeInd;
    /**
     * db_lock_ver_nbr
     */
    private Integer lockVerNbr;

    public Long getEDocLiteStyleId() {
        return eDocLiteStyleId;
    }
    public void setEDocLiteStyleId(Long docLiteStyleId) {
        eDocLiteStyleId = docLiteStyleId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getXmlContent() {
        return xmlContent;
    }
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
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
        return "[EDocLiteStyle: eDocLiteStyleId=" + eDocLiteStyleId
                           + ", name=" + name
                           + ", xml=" + (xmlContent == null ? xmlContent : xmlContent.length() + "chars")
                           + ", activeInd=" + activeInd
                           + ", lockVerNbr=" + lockVerNbr
                           + "]";
    }
}