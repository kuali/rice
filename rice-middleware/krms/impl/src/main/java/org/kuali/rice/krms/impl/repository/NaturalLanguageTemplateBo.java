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
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplateContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The mutable implementation of the @{link NaturalLanguageTemplateContract} interface, the counterpart to the immutable implementation {@link NaturalLanguageTemplate}.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@Entity
@Table(name = "KRMS_NL_TMPL_T")
public class NaturalLanguageTemplateBo implements NaturalLanguageTemplateContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @Transient
    private Map<String, String> attributes;

    @Column(name = "LANG_CD")
    private String languageCode;

    @Column(name = "NL_USAGE_ID")
    private String naturalLanguageUsageId;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "TMPL")
    private String template;

    @PortableSequenceGenerator(name = "KRMS_NL_TMPL_S")
    @GeneratedValue(generator = "KRMS_NL_TMPL_S")
    @Id
    @Column(name = "NL_TMPL_ID")
    private String id;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @OneToMany(targetEntity = NaturalLanguageTemplateAttributeBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "NL_TMPL_ID", referencedColumnName = "NL_TMPL_ID", insertable = false, updatable = false)
    private Set<NaturalLanguageTemplateAttributeBo> attributeBos;

    private static KrmsAttributeDefinitionService attributeDefinitionService;

    private static KrmsTypeRepositoryService typeRepositoryService;

    /**
     * Default Constructor
     * 
     */
    public NaturalLanguageTemplateBo() {
    }

    @Override
    public String getLanguageCode() {
        return this.languageCode;
    }

    @Override
    public String getNaturalLanguageUsageId() {
        return this.naturalLanguageUsageId;
    }

    @Override
    public String getTypeId() {
        return this.typeId;
    }

    @Override
    public String getTemplate() {
        return this.template;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    /**
     * Sets the value of languageCode on this builder to the given value.
     * 
     * @param languageCode the languageCode value to set.
     * 
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Sets the value of naturalLanguageUsageId on this builder to the given value.
     * 
     * @param naturalLanguageUsageId the naturalLanguageUsageId value to set.
     * 
     */
    public void setNaturalLanguageUsageId(String naturalLanguageUsageId) {
        this.naturalLanguageUsageId = naturalLanguageUsageId;
    }

    /**
     * Sets the value of typeId on this builder to the given value.
     * 
     * @param typeId the typeId value to set.
     * 
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * Sets the value of template on this builder to the given value.
     * 
     * @param template the template value to set.
     * 
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Sets the value of id on this builder to the given value.
     * 
     * @param id the id value to set.
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the value of active on this builder to the given value.
     * 
     * @param active the active value to set.
     * 
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Sets the value of versionNumber on this builder to the given value.
     * 
     * @param versionNumber the versionNumber value to set.
     * 
     */
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Sets the value of AttributeBos on this builder to the given value.
     * 
     * @param attributeBos the AttributeBos value to set.
     * 
     */
    public void setAttributeBos(List<NaturalLanguageTemplateAttributeBo> attributeBos) {
        this.attributeBos = new HashSet<NaturalLanguageTemplateAttributeBo>(attributeBos);
    }

    /**
     * Sets the value of AttributeBos on this builder to the given value.
     * 
     * @param attributeBos the AttributeBos value to set.
     * 
     */
    public void setAttributeBos(Set<NaturalLanguageTemplateAttributeBo> attributeBos) {
        this.attributeBos = new HashSet<NaturalLanguageTemplateAttributeBo>(attributeBos);
    }

    /**
     * Converts a mutable {@link NaturalLanguageTemplateBo} to its immutable counterpart, {@link NaturalLanguageTemplate}.
     * @param naturalLanguageTemplateBo the mutable business object.
     * @return a {@link NaturalLanguageTemplate} the immutable object.
     * 
     */
    public static NaturalLanguageTemplate to(NaturalLanguageTemplateBo naturalLanguageTemplateBo) {
        if (naturalLanguageTemplateBo == null) {
            return null;
        }

        return NaturalLanguageTemplate.Builder.create(naturalLanguageTemplateBo).build();
    }

    /**
     * Converts a immutable {@link NaturalLanguageTemplate} to its mutable {@link NaturalLanguageTemplateBo} counterpart.
     * @param naturalLanguageTemplate the immutable object.
     * @return a {@link NaturalLanguageTemplateBo} the mutable NaturalLanguageTemplateBo.
     * 
     */
    public static org.kuali.rice.krms.impl.repository.NaturalLanguageTemplateBo from(NaturalLanguageTemplate naturalLanguageTemplate) {
        if (naturalLanguageTemplate == null) {
            return null;
        }

        NaturalLanguageTemplateBo naturalLanguageTemplateBo = new NaturalLanguageTemplateBo();
        naturalLanguageTemplateBo.setLanguageCode(naturalLanguageTemplate.getLanguageCode());
        naturalLanguageTemplateBo.setNaturalLanguageUsageId(naturalLanguageTemplate.getNaturalLanguageUsageId());
        naturalLanguageTemplateBo.setTypeId(naturalLanguageTemplate.getTypeId());
        naturalLanguageTemplateBo.setTemplate(naturalLanguageTemplate.getTemplate());
        naturalLanguageTemplateBo.setId(naturalLanguageTemplate.getId());
        naturalLanguageTemplateBo.setActive(naturalLanguageTemplate.isActive());
        naturalLanguageTemplateBo.setVersionNumber(naturalLanguageTemplate.getVersionNumber());
        if (StringUtils.isNotBlank(naturalLanguageTemplate.getId())) {
           naturalLanguageTemplateBo.setAttributeBos(buildAttributeBoSet(naturalLanguageTemplate));
        }
        return naturalLanguageTemplateBo;
    }

    @Override
    public Map<String, String> getAttributes() {
        if (attributeBos == null) {
            return Collections.emptyMap();
        }

        HashMap<String, String> attributes = new HashMap<String, String>(attributeBos.size());

        for (NaturalLanguageTemplateAttributeBo attr : attributeBos) {
            attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());
        }

        return attributes;
    }

    /**
     * TODO
     * 
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributeBos = new HashSet<NaturalLanguageTemplateAttributeBo>();

        if (!org.apache.commons.lang.StringUtils.isBlank(this.typeId)) {
            List<KrmsAttributeDefinition> attributeDefinitions = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().findAttributeDefinitionsByType(this.getTypeId());
            Map<String, KrmsAttributeDefinition> attributeDefinitionsByName = new HashMap<String, KrmsAttributeDefinition>(attributeDefinitions.size());

            if (attributeDefinitions != null) for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
                attributeDefinitionsByName.put(attributeDefinition.getName(), attributeDefinition);
            }

            for (Map.Entry<String, String> attr : attributes.entrySet()) {
                KrmsAttributeDefinition attributeDefinition = attributeDefinitionsByName.get(attr.getKey());
                NaturalLanguageTemplateAttributeBo attributeBo = new NaturalLanguageTemplateAttributeBo();
                attributeBo.setNaturalLanguageTemplateId(this.getId());
                attributeBo.setAttributeDefinitionId((attributeDefinition == null) ? null : attributeDefinition.getId());
                attributeBo.setValue(attr.getValue());
                attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attributeDefinition));
                attributeBos.add(attributeBo);
            }
        }
    }

    private static Collection<NaturalLanguageTemplateAttributeBo> buildAttributes(NaturalLanguageTemplate im, Collection<NaturalLanguageTemplateAttributeBo> attributes) {
        KrmsTypeDefinition krmsTypeDefinition = getTypeRepositoryService().getTypeById(im.getTypeId());

        // for each entry, build a NaturalLanguageTemplateAttributeBo and add it 
        if (im.getAttributes() != null) {
            for (Map.Entry<String, String> entry : im.getAttributes().entrySet()) {
                KrmsAttributeDefinition attrDef = getAttributeDefinitionService().getAttributeDefinitionByNameAndNamespace(entry.getKey(), krmsTypeDefinition.getNamespace());

                if (attrDef != null) {
                    NaturalLanguageTemplateAttributeBo attributeBo = new NaturalLanguageTemplateAttributeBo();
                    attributeBo.setNaturalLanguageTemplateId(im.getId());
                    attributeBo.setAttributeDefinitionId(attrDef.getId());
                    attributeBo.setValue(entry.getValue());
                    attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));
                    attributes.add(attributeBo);
                } else {
                    throw new RiceIllegalStateException("there is no attribute definition with the name '" + entry.getKey() + "' that is valid for the naturalLanguageTemplate type with id = '" + im.getTypeId() + "'");
                }
            }
        }

        return attributes;
    }

    private static Set<NaturalLanguageTemplateAttributeBo> buildAttributeBoSet(NaturalLanguageTemplate im) {
        Set<NaturalLanguageTemplateAttributeBo> attributes = new HashSet<NaturalLanguageTemplateAttributeBo>();

        return (Set) buildAttributes(im, attributes);
    }

    private static List<NaturalLanguageTemplateAttributeBo> buildAttributeBoList(NaturalLanguageTemplate im) {
        List<NaturalLanguageTemplateAttributeBo> attributes = new LinkedList<NaturalLanguageTemplateAttributeBo>();

        return (List) buildAttributes(im, attributes);
    }

    public static void setAttributeDefinitionService(KrmsAttributeDefinitionService attributeDefinitionService) {
        NaturalLanguageTemplateBo.attributeDefinitionService = attributeDefinitionService;
    }

    public static KrmsTypeRepositoryService getTypeRepositoryService() {
        if (typeRepositoryService == null) {
            typeRepositoryService = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();
        }

        return typeRepositoryService;
    }

    public static void setTypeRepositoryService(KrmsTypeRepositoryService typeRepositoryService) {
        NaturalLanguageTemplateBo.typeRepositoryService = typeRepositoryService;
    }

    public static KrmsAttributeDefinitionService getAttributeDefinitionService() {
        if (attributeDefinitionService == null) {
            attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        }

        return attributeDefinitionService;
    }
}
