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

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.Boolean01Converter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Association between WorkflowDocument type -&gt; EDocLite definition, EDocLite style
 * Table: en_edoclt_assoc_t
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_EDL_ASSCTN_T")
public class EDocLiteAssociation  extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 7300251507982374010L;
    /**
     * edoclt_assoc_id
     */
    @Id
    @GeneratedValue(generator="KREW_EDL_S")
    @PortableSequenceGenerator(name = "KREW_EDL_S")
    @Column(name="EDOCLT_ASSOC_ID", nullable = false)
    private Long edocLiteAssocId;
    /**
     * edoclt_assoc_doctype_nm
     */
    @Column(name="DOC_TYP_NM", nullable = false)
    private String edlName;
    /**
     * edoclt_assoc_def_nm
     */
    @Column(name="EDL_DEF_NM")
    private String definition;
    /**
     * edoclt_assoc_style_nm
     */
    @Column(name="STYLE_NM")
    private String style;
    /**
     * edoclt_assoc_actv_ind
     */
    @Convert(converter=Boolean01Converter.class)
    @Column(name="ACTV_IND", nullable = false)
    private Boolean activeInd;

    @Transient
    private String actionsUrl;//for quickfinder

    /**
     * Returns the edoc lite association id
     * @return the association id
     */
    public Long getEdocLiteAssocId() {
        return edocLiteAssocId;
    }

    /**
     *
     * @see #getEdocLiteAssocId()
     */
    public void setEdocLiteAssocId(Long edocLiteAssocId) {
        this.edocLiteAssocId = edocLiteAssocId;
    }

    /**
     * Returns the edoc lite name
     * @return the edoc lite name
     */
    public String getEdlName() {
        return edlName;
    }

    /**
     * @see #getEdlName()
     */
    public void setEdlName(String edlName) {
        this.edlName = edlName;
    }

    /**
     * Returns the definition
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     *
     * @see #getDefinition()
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * Returns the style.
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     *
     * @see #getStyle()
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Returns the records activity.
     * @return TRUE if the record is active, FALSE otherwise.
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

    /**
     * Returns actions url
     * @return the actions url
     */
    public String getActionsUrl() {
        return actionsUrl;
    }

    /**
     *
     * @see #getActionsUrl()
     */
    public void setActionsUrl(String actionsUrl) {
        this.actionsUrl = actionsUrl;
    }
}
