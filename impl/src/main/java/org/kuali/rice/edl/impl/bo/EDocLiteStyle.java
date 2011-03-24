/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.edl.impl.bo;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

/**
 * EDocLite XSLT stylesheet
 * Table: en_edoclt_style_t
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_STYLE_T")
//@Sequence(name="KREW_EDL_S", property="edocLiteStyleId")
public class EDocLiteStyle  extends PersistableBusinessObjectBase{
    private static final long serialVersionUID = 2020611019976731725L;
    /**
     * edoclt_style_id
     */
    @Id
    @GeneratedValue(generator="KREW_EDL_S")
	@GenericGenerator(name="KREW_EDL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_EDL_S"),
			@Parameter(name="value_column",value="id")
	})
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
}
