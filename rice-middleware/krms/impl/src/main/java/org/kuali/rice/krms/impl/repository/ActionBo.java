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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Action Business Object is the Action mutable class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ActionDefinition
 * @see ActionDefinitionContract
 * @see org.kuali.rice.krms.framework.engine.Action
 */
@Entity
@Table(name = "KRMS_ACTN_T")
public class ActionBo implements ActionDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_ACTN_S")
    @GeneratedValue(generator = "KRMS_ACTN_S")
    @Id
    @Column(name = "ACTN_ID")
    private String id;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "NM")
    private String name;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "TYP_ID")
    private String typeId;

    @ManyToOne()
    @JoinColumn(name = "RULE_ID")
    private RuleBo rule;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @OneToMany(orphanRemoval = true, mappedBy = "action",
            cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST })
    private List<ActionAttributeBo> attributeBos;

    @Override
    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>();
        if (attributeBos != null) for (ActionAttributeBo attr : attributeBos) {
            if (attr.getAttributeDefinition() == null) {
                attributes.put("", "");
            } else {
                attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());
            }
        }
        return attributes;
    }

    /**
     * Set the Action Attributes
     *
     * @param attributes to add to this Action
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributeBos = new ArrayList<ActionAttributeBo>();
        if (!StringUtils.isBlank(this.typeId)) {
            List<KrmsAttributeDefinition> attributeDefinitions = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().findAttributeDefinitionsByType(this.getTypeId());
            Map<String, KrmsAttributeDefinition> attributeDefinitionsByName = new HashMap<String, KrmsAttributeDefinition>();

            if (attributeDefinitions != null) {
                for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
                    attributeDefinitionsByName.put(attributeDefinition.getName(), attributeDefinition);
                }
            }

            for (Map.Entry<String, String> attr : attributes.entrySet()) {
                KrmsAttributeDefinition attributeDefinition = attributeDefinitionsByName.get(attr.getKey());

                if (attributeDefinition != null) {
                    ActionAttributeBo attributeBo = new ActionAttributeBo();
                    attributeBo.setAction(this);
                    attributeBo.setValue(attr.getValue());
                    attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attributeDefinition));
                    attributeBos.add(attributeBo);
                }
            }
        }
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static ActionDefinition to(ActionBo bo) {
        if (bo == null) {
            return null;
        }

        return ActionDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ActionBo from(ActionDefinition im) {
        if (im == null) {
            return null;
        }

        ActionBo bo = new ActionBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.typeId = im.getTypeId();
        bo.description = im.getDescription();

        // we don't set the rule because we only have the ruleId in the ActionDefinition.  If you need the RuleBo as
        // well, use RuleBo.from to convert the RuleDefinition and all it's children as well.

        bo.sequenceNumber = im.getSequenceNumber();
        bo.setVersionNumber(im.getVersionNumber());

        // build the list of action attribute BOs
        List<ActionAttributeBo> attrs = new ArrayList<ActionAttributeBo>();

        // for each converted pair, build an ActionAttributeBo and add it to the set
        for (Map.Entry<String, String> entry : im.getAttributes().entrySet()) {
            KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().getKrmsAttributeBo(entry.getKey(), im.getNamespace());
            ActionAttributeBo attributeBo = new ActionAttributeBo();
            attributeBo.setAction(bo);
            attributeBo.setValue(entry.getValue());
            attributeBo.setAttributeDefinition(attrDefBo);
            attrs.add(attributeBo);
        }

        bo.setAttributeBos(attrs);

        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getRuleId() {
        if (rule != null) {
            return rule.getId();
        }

        return null;
    }

    public RuleBo getRule() {
        return rule;
    }

    public void setRule(RuleBo rule) {
        this.rule = rule;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<ActionAttributeBo> getAttributeBos() {
        return attributeBos;
    }

    public void setAttributeBos(List<ActionAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }
}
