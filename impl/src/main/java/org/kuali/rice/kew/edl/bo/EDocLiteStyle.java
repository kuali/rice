/*
 * Copyright 2005-2009 The Kuali Foundation.
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * EDocLite XSLT stylesheet
 * Table: en_edoclt_style_t
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_STYLE_T")
@Sequence(name="KREW_EDL_S", property="edocLiteStyleId")
public class EDocLiteStyle  extends PersistableBusinessObjectBase{
    private static final long serialVersionUID = 2020611019976731725L;
    /**
     * edoclt_style_id
     */
    @Id
	@Column(name="STYLE_ID")
	private Long edocLiteStyleId;
    /**
     * edoclt_style_nm
     */
    @Column(name="NM")
	private String name;
    /**
     * edoclt_style_xml
     */
    @Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="XML")
	private String xmlContent;
    /**
     * edoclt_style_actv_ind
     */
    @Column(name="ACTV_IND")
	private Boolean activeInd;

    @PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KNSServiceLocator.getEntityManagerFactory().createEntityManager());
    }

    public Long getEdocLiteStyleId() {
        return edocLiteStyleId;
    }
    public void setEdocLiteStyleId(Long docLiteStyleId) {
        edocLiteStyleId = docLiteStyleId;
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

    public String toString() {
        return "[EDocLiteStyle: edocLiteStyleId=" + edocLiteStyleId
                           + ", name=" + name
                           + ", xml=" + (xmlContent == null ? xmlContent : xmlContent.length() + "chars")
                           + ", activeInd=" + activeInd
                           + ", versionNumber=" + versionNumber
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
		propMap.put("edocLiteStyleId",getEdocLiteStyleId());
		propMap.put("name",getName());
		propMap.put("xmlContent",(xmlContent == null ? xmlContent : xmlContent.length() + "chars"));
		propMap.put("activeInd",getActiveInd());
		propMap.put("versionNumber",getVersionNumber());
	    return propMap;

	}
}
