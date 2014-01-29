/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.krad.data.jpa.converters.Boolean01Converter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * EDocLite document definition
 * Table: en_edoclt_def_t
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_EDL_DEF_T")
public class EDocLiteDefinition extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6230450806784021509L;
    /**
     * edoclt_def_id
     */
    @Id
    @GeneratedValue(generator="KREW_EDL_S")
    @PortableSequenceGenerator(name = "KREW_EDL_S")
    @Column(name = "EDOCLT_DEF_ID", nullable = false)
    private Long id;
    /**
     * edoclt_def_nm
     */
    @Column(name="NM", nullable = false)
    private String name;
    /**
     * edoclt_def_xml
     */
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name="XML", nullable = false)
    private String xmlContent;
    /**
     * edoclt_def_actv_ind
     */
    @Convert(converter=Boolean01Converter.class)
    @Column(name="ACTV_IND", nullable = false)
    private Boolean activeInd;

    /**
     * Returns the edoc lite definition id.
     * @return the definition id
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @see #getId()
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @see #getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the xml content.
     * @return the xml content
     */
    public String getXmlContent() {
        return xmlContent;
    }

    /**
     *
     * @see #getXmlContent()
     */
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    /**
     * Returns true if the record is active, false otherwise.
     * @return TRUE if the record is active, FALSE otherwise
     */
    public Boolean getActiveInd() {
        return activeInd;
    }

    /**
     *
     * @see #getActiveInd()
     */
    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }
}
