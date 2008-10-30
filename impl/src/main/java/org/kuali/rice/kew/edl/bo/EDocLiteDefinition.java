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
package org.kuali.rice.kew.edl.bo;

import java.util.LinkedHashMap;

import javax.persistence.Version;
import javax.persistence.FetchType;
import javax.persistence.Basic;
import javax.persistence.Lob;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * EDocLite document definition
 * Table: en_edoclt_def_t
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_EDL_DEF_T")
public class EDocLiteDefinition  extends PersistableBusinessObjectBase {
    /**
     * edoclt_def_id
     */
    @Id
	@Column(name="edoclt_def_id")
	private Long eDocLiteDefId;
    /**
     * edoclt_def_nm
     */
    @Column(name="edoclt_def_nm")
	private String name;
    /**
     * edoclt_def_xml
     */
    @Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="edoclt_def_xml")
	private String xmlContent;
    /**
     * edoclt_def_actv_ind
     */
    @Column(name="edoclt_def_actv_ind")
	private Boolean activeInd;
    /**
     * db_lock_ver_nbr
     */
    @Version
	@Column(name="db_lock_ver_nbr")
	private Integer lockVerNbr;

    public Long getEDocLiteDefId() {
        return eDocLiteDefId;
    }
    public void setEDocLiteDefId(Long eDocLiteDefId) {
        this.eDocLiteDefId = eDocLiteDefId;
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
        return "[EDocLiteDefinition: eDocLiteDefId=" + eDocLiteDefId
                                + ", name=" + name
                                + ", xml=" + (xmlContent == null ? xmlContent : xmlContent.length() + "chars")
                                + ", activeInd=" + activeInd
                                + ", lockVerNbr=" + lockVerNbr
                                + "]";
    }
    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
		propMap.put("eDocLiteDefId",getEDocLiteDefId());
		propMap.put("name",getName());
		propMap.put("xml",(xmlContent == null ? xmlContent : xmlContent.length() + "chars"));
		propMap.put("activeInd",getActiveInd());
		propMap.put("lockVerNbr",getLockVerNbr());		
	    return propMap;		
	}
}
