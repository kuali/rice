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
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinitionContract;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "KRMS_CNTXT_T")
public class ContextBo implements ContextDefinitionContract, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONTEXT_SEQ_NAME = "KRMS_CNTXT_S";

    @PortableSequenceGenerator(name = CONTEXT_SEQ_NAME)
    @GeneratedValue(generator = CONTEXT_SEQ_NAME)
    @Id
    @Column(name = "CNTXT_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @OneToMany(mappedBy = "context")
    @JoinColumn(name = "CNTXT_ID", referencedColumnName = "CNTXT_ID", insertable = false, updatable = false)
    private List<AgendaBo> agendas = new ArrayList<AgendaBo>();

    @OneToMany(
            targetEntity = ContextAttributeBo.class, orphanRemoval = true, mappedBy = "context",
            cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST },
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "CNTXT_ID", referencedColumnName = "CNTXT_ID", insertable = true, updatable = true)
    private List<ContextAttributeBo> attributeBos = new ArrayList<ContextAttributeBo>();

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @Override
    public List<AgendaBo> getAgendas() {
        return agendas;
    }

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> attributes = new HashMap<String, String>();

        if (attributeBos != null) for (ContextAttributeBo attr : attributeBos) {
            ((HashMap<String, String>) attributes).put(attr.getAttributeDefinition().getName(), attr.getValue());
        }

        return attributes;
    }

    public ContextBo copyContext(String additionalNameText) {
        ContextBo copy = (ContextBo) SerializationUtils.deepCopy(this);

        //
        // set all IDs to null
        //

        copy.setId(null);

        // copying a context does not copy the associated agendas
        copy.setAgendas(null);
        for (ContextAttributeBo attributeBo : copy.getAttributeBos()) {
            attributeBo.setId(null);
        }

        if (!StringUtils.isEmpty(additionalNameText)) {
            copy.setName(copy.getName() + additionalNameText);
        }

        return copy;
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static ContextDefinition to(ContextBo bo) {
        if (bo == null) {
            return null;
        }

        return ContextDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ContextBo from(ContextDefinition im) {
        if (im == null) {
            return null;
        }

        ContextBo bo = new ContextBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.typeId = im.getTypeId();
        bo.description = im.getDescription();
        bo.active = im.isActive();
        bo.agendas = new ArrayList<AgendaBo>();
        for (AgendaDefinition agenda : im.getAgendas()) {
            bo.agendas.add(KrmsRepositoryServiceLocator.getAgendaBoService().from(agenda));
        }

        // build the list of agenda attribute BOs
        List<ContextAttributeBo> attrs = new ArrayList<ContextAttributeBo>();

        // for each converted pair, build an AgendaAttributeBo and add it to the list
        ContextAttributeBo attributeBo;
        for (Map.Entry<String, String> entry : im.getAttributes().entrySet()) {
            KrmsAttributeDefinitionBo attrDefBo =
                    KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().getKrmsAttributeBo(entry.getKey(), im.getNamespace());
            attributeBo = new ContextAttributeBo();
            attributeBo.setContext(bo);
            attributeBo.setValue(entry.getValue());
            attributeBo.setAttributeDefinition(attrDefBo);
            attrs.add(attributeBo);
        }

        bo.setAttributeBos(attrs);
        bo.versionNumber = im.getVersionNumber();

        return bo;
    }

     /*
    This is being done because there is a  major issue with lazy relationships, in ensuring that the relationship is
    still available after the object has been detached, or serialized. For most JPA providers, after serialization
    any lazy relationship that was not instantiated will be broken, and either throw an error when accessed,
    or return null.
     */

    private void writeObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        agendas.size();
        attributeBos.size();
        stream.defaultWriteObject();
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setAgendas(List<AgendaBo> agendas) {
        this.agendas = agendas;
    }

    public List<ContextAttributeBo> getAttributeBos() {
        return attributeBos;
    }

    public void setAttributeBos(List<ContextAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
