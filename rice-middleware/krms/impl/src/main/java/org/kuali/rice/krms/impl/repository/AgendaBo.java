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
import org.kuali.rice.core.api.util.io.SerializationUtils;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "KRMS_AGENDA_T")
public class AgendaBo implements AgendaDefinitionContract, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String AGENDA_SEQ_NAME = "KRMS_AGENDA_S";
    static final RepositoryBoIncrementer agendaIdIncrementer = new RepositoryBoIncrementer(AGENDA_SEQ_NAME);

    @PortableSequenceGenerator(name = AGENDA_SEQ_NAME)
    @GeneratedValue(generator = AGENDA_SEQ_NAME)
    @Id
    @Column(name = "AGENDA_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "CNTXT_ID")
    private String contextId;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "INIT_AGENDA_ITM_ID")
    private String firstItemId;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @OneToMany(orphanRemoval = true, mappedBy = "agenda", cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "AGENDA_ID", referencedColumnName = "AGENDA_ID", insertable = true, updatable = true)
    private Set<AgendaAttributeBo> attributeBos;

    @OneToMany(orphanRemoval = true, targetEntity = AgendaItemBo.class,
            cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "AGENDA_ID", referencedColumnName = "AGENDA_ID", insertable = false, updatable = false)
    private List<AgendaItemBo> items;

    @ManyToOne(targetEntity = ContextBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "CNTXT_ID", referencedColumnName = "CNTXT_ID", insertable = false, updatable = false)
    private ContextBo context;

    public AgendaBo() {
        active = true;
        items = new ArrayList<AgendaItemBo>();
    }

    public AgendaBo getAgenda() {
        return this;
    }

    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>();

        if (attributeBos != null) for (AgendaAttributeBo attr : attributeBos) {
            attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());
        }

        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributeBos = new HashSet<AgendaAttributeBo>();

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
                AgendaAttributeBo attributeBo = new AgendaAttributeBo();
                attributeBo.setAgenda(this);
                attributeBo.setValue(attr.getValue());
                attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attributeDefinition));
                attributeBos.add(attributeBo);
            }
        }
    }

    /**
     * Returns of copy of this agenda, with the given newAgendaName and new ids.
     *
     * @param newAgendaName name of the newly copied AgendaBo
     * @param dateTimeStamp to append to the names of objects
     * @return AgendaBo copy of this Agenda with new ids and name
     */
    public AgendaBo copyAgenda(String newAgendaName, String dateTimeStamp) {
        List<AgendaItemBo> agendaItems = this.getItems();
        AgendaBo copiedAgenda = (AgendaBo) SerializationUtils.deepCopy(this);
        copiedAgenda.setName(newAgendaName);

        // Using a copiedAgenda we don't mess with the existing agenda at all.
        copiedAgenda.setId(agendaIdIncrementer.getNewId());

        String initAgendaItemId = this.getFirstItemId();
        List<AgendaItemBo> copiedAgendaItems = new ArrayList<AgendaItemBo>();
        Map<String, RuleBo> oldRuleIdToNew = new HashMap<String, RuleBo>();
        Map<String, AgendaItemBo> oldAgendaItemIdToNew = new HashMap<String, AgendaItemBo>();

        for (AgendaItemBo agendaItem : agendaItems) {
            if (!oldAgendaItemIdToNew.containsKey(agendaItem.getId())) {
                AgendaItemBo copiedAgendaItem =
                        agendaItem.copyAgendaItem(copiedAgenda, oldRuleIdToNew, oldAgendaItemIdToNew, copiedAgendaItems, dateTimeStamp);

                if (initAgendaItemId != null && initAgendaItemId.equals(agendaItem.getId())) {
                    copiedAgenda.setFirstItemId(copiedAgendaItem.getId());
                }

                copiedAgendaItems.add(copiedAgendaItem);
                oldAgendaItemIdToNew.put(agendaItem.getId(), copiedAgendaItem);
            }
        }

        copiedAgenda.setItems(copiedAgendaItems);

        return copiedAgenda;
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object AgendaDefinition
     */
    public static AgendaDefinition to(AgendaBo bo) {
        if (bo == null) {
            return null;
        }

        return AgendaDefinition.Builder.create(bo).build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirstItemId() {
        return firstItemId;
    }

    public void setFirstItemId(String firstItemId) {
        this.firstItemId = firstItemId;
    }

    public Set<AgendaAttributeBo> getAttributeBos() {
        return attributeBos;
    }

    public void setAttributeBos(Set<AgendaAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }

    public List<AgendaItemBo> getItems() {
        return items;
    }

    public void setItems(List<AgendaItemBo> items) {
        this.items = items;
    }

    public ContextBo getContext() {
        return context;
    }

    public void setContext(ContextBo context) {
        this.context = context;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
