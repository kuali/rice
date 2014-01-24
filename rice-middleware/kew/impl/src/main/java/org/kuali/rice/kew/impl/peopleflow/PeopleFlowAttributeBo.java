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
package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition;
import org.kuali.rice.kew.impl.type.KewAttributeDefinitionBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = "KREW_PPL_FLW_ATTR_T")
public class PeopleFlowAttributeBo implements Serializable, Versioned {

    @Id
    @GeneratedValue(generator = "KREW_PPL_FLW_ATTR_S")
    @PortableSequenceGenerator(name = "KREW_PPL_FLW_ATTR_S")
    @Column(name="PPL_FLW_ATTR_ID", nullable = false)
    private String id;

    @Column(name="ATTR_VAL")
    private String value;

    @Version
    @Column(name="VER_NBR", nullable = false)
    private Long versionNumber;

    @ManyToOne
    @JoinColumn(name = "PPL_FLW_ID", nullable = false)
    private PeopleFlowBo peopleFlow;

    @ManyToOne
    @JoinColumn(name="ATTR_DEFN_ID", nullable = false)
    private KewAttributeDefinitionBo attributeDefinition;

    public static PeopleFlowAttributeBo from(KewAttributeDefinition attributeDefinition, String id, PeopleFlowBo peopleFlow,
            String value) {

        if (null == attributeDefinition) {
            return null;
        }

        PeopleFlowAttributeBo peopleFlowAttributeBo = new PeopleFlowAttributeBo();
        peopleFlowAttributeBo.setId(id);
        peopleFlowAttributeBo.setPeopleFlow(peopleFlow);
        peopleFlowAttributeBo.setValue(value);
        peopleFlowAttributeBo.setAttributeDefinition(KewAttributeDefinitionBo.from(attributeDefinition));

        return peopleFlowAttributeBo;
    }

    /**
     * Default constructor.
     */
    public PeopleFlowAttributeBo() { }

    /**
     * Returns the people flow attribute id.
     * @return the people flow attribute id
     */
    public String getId() {
        return id;
    }

    /**
     * @see #getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    public PeopleFlowBo getPeopleFlow() {
        return peopleFlow;
    }

    public void setPeopleFlow(PeopleFlowBo peopleFlow) {
        this.peopleFlow = peopleFlow;
    }

    /**
     * Returns the attribute value.
     * @return the attribute value
     */
    public String getValue() {
        return value;
    }

    /**
     * @see #getValue()
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the version number.
     * @return the version number
     */
    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * @see #getVersionNumber()
     */
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Returns a {@link KewAttributeDefinitionBo}
     * @return {@link KewAttributeDefinitionBo}
     */
    public KewAttributeDefinitionBo getAttributeDefinition() {
        return attributeDefinition;
    }

    /**
     * @see #getAttributeDefinitionId()
     */
    public void setAttributeDefinition(KewAttributeDefinitionBo attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

    /**
     * Returns the @{link KewAttributeDefinitionBo} id
     * @return the @{link KewAttributeDefinitonBo} id
     */
    public String getAttributeDefinitionId() {
        if (null != this.attributeDefinition) {
            return this.attributeDefinition.getId();
        } else {
            return null;
        }
    }
}
