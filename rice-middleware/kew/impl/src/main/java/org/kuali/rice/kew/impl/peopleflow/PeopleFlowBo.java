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

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowContract;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.impl.type.KewTypeBo;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.util.BeanPropertyComparator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Mapped entity for PeopleFlows
 *
 *  @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_PPL_FLW_T")
public class PeopleFlowBo implements Serializable, PeopleFlowContract, MutableInactivatable {

    private static final long serialVersionUID = -4911187431645573793L;

    @Id
    @GeneratedValue(generator = "KREW_PPL_FLW_S")
    @PortableSequenceGenerator(name = "KREW_PPL_FLW_S")
    @Column(name = "PPL_FLW_ID", nullable = false)
    private String id;

    @Column(name = "NM", nullable = false)
    private String name;

    @Column(name = "NMSPC_CD", nullable = false)
    private String namespaceCode;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "ACTV", nullable = false)
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Version
    @Column(name = "VER_NBR", nullable = false)
    private Long versionNumber;

    @ManyToOne
    @JoinColumn(name = "TYP_ID", insertable = false, updatable = false)
    private KewTypeBo typeBo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "peopleFlow", orphanRemoval = true)
    private List<PeopleFlowAttributeBo> attributeBos = new ArrayList<PeopleFlowAttributeBo>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peopleFlow", orphanRemoval = true)
    private List<PeopleFlowMemberBo> members = new ArrayList<PeopleFlowMemberBo>();

    // non-persisted, used for maintenance
    @Transient
    private Map<String, String> attributeValues = new HashMap<String, String>();

    public static PeopleFlowBo from(PeopleFlowDefinition peopleFlow, KewTypeDefinition kewTypeDefinition) {
        return PeopleFlowBo.fromAndUpdate(peopleFlow, kewTypeDefinition, null);
    }

    /**
     * Translates from the given PeopleFlowDefinition to a PeopleFlowBo, optionally updating the given "toUpdate" parameter
     * instead of creating a new PeopleFlowBo.  If it's not passed then a new PeopleFlowBo will be created.
     */
    public static PeopleFlowBo fromAndUpdate(PeopleFlowDefinition peopleFlow, KewTypeDefinition kewTypeDefinition,
            PeopleFlowBo toUpdate) {

        PeopleFlowBo result = toUpdate;

        if (null == toUpdate) {
            result = new PeopleFlowBo();
        }

        result.setId(peopleFlow.getId());
        result.setName(peopleFlow.getName());
        result.setNamespaceCode(peopleFlow.getNamespaceCode());
        result.setTypeId(peopleFlow.getTypeId());
        result.setDescription(peopleFlow.getDescription());
        result.setActive(peopleFlow.isActive());
        result.setVersionNumber(peopleFlow.getVersionNumber());
        
        // we need to translate attributes over, this is a bit more work, first let's do some validation
        if (null == peopleFlow.getTypeId()) {
            if (null != kewTypeDefinition) {
                throw new RiceIllegalArgumentException("PeopleFlow has no type id, but a KewTypeDefinition was " +
                        "supplied when it should not have been.");
            }
        }
        if (null != peopleFlow.getTypeId()) {
            if (kewTypeDefinition == null) {
                throw new RiceIllegalArgumentException("PeopleFlow has a type id of '" + peopleFlow.getTypeId() +
                        "' but no KewTypeDefinition was supplied.");
            }
            if (!kewTypeDefinition.getId().equals(peopleFlow.getTypeId())) {
                throw new RiceIllegalArgumentException("Type id of given KewTypeDefinition does not match PeopleFlow " +
                        "type id:  " + kewTypeDefinition.getId() + " != " + peopleFlow.getTypeId());
            }
        }

        // now we need to effectively do a diff with the given attributes, first let's add new entries and update
        // existing ones
        // TODO - ensure this is correct
        ArrayList attributesToAdd = new ArrayList<PeopleFlowAttributeBo>();
        // if type is null drop attributes
        if (null != peopleFlow.getTypeId()) {
            for (String key : peopleFlow.getAttributes().keySet()) {
                KewAttributeDefinition attributeDefinition = kewTypeDefinition.getAttributeDefinitionByName(key);
                if (null == attributeDefinition) {
                    throw new RiceIllegalArgumentException("There is no attribute definition for the given attribute " +
                            "name '" + key + "'");
                }
                attributesToAdd.add(PeopleFlowAttributeBo.from(attributeDefinition, null, result,
                        peopleFlow.getAttributes().get(key)));
            }
            result.setAttributeBos(attributesToAdd);
        }
        // TODO - END
        handleMembersUpdate(result, peopleFlow);

        return result;
    }

    /**
     * Translate the members, if the members have changed at all, we want to clear so that the current set of members
     * are removed by OJB's removal aware list.
     */
    private static void handleMembersUpdate(PeopleFlowBo peopleFlowBo, PeopleFlowDefinition peopleFlow) {

        Set<PeopleFlowMember> currentMembers = new HashSet<PeopleFlowMember>();

        if (null == peopleFlowBo.getMembers()) {
            peopleFlowBo.setMembers(new ArrayList<PeopleFlowMemberBo>());
        }
        for (PeopleFlowMemberBo pplFlwMbr : peopleFlowBo.getMembers()) {
            currentMembers.add(PeopleFlowMember.Builder.create(pplFlwMbr).build());
        }

        if (!currentMembers.equals(new HashSet<PeopleFlowMember>(peopleFlow.getMembers()))) {
            // this means that the membership has been updated, we need to rebuild it
//            peopleFlowBo.getMembers().clear();
            ArrayList<PeopleFlowMemberBo> membersToAdd = new ArrayList<PeopleFlowMemberBo>();
            for (PeopleFlowMember member : peopleFlow.getMembers()) {
                membersToAdd.add(PeopleFlowMemberBo.from(member, peopleFlowBo));
            }
            peopleFlowBo.setMembers(membersToAdd);
        }
    }

    public static PeopleFlowDefinition maintenanceCopy(PeopleFlowBo peopleFlowBo) {
        if (null == peopleFlowBo) {
            return null;
        }
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.createMaintenanceCopy(peopleFlowBo);

        return builder.build();
    }

    public static PeopleFlowDefinition to(PeopleFlowBo peopleFlowBo) {
        if (null == peopleFlowBo) {
            return null;
        }
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.create(peopleFlowBo);

        return builder.build();
    }

    /**
     * Default constructor.
     */
    public PeopleFlowBo() { }

    public PeopleFlowBo(PeopleFlowDefinition pfDef) {
        PeopleFlowBo newPFBo = new PeopleFlowBo();

        this.id = pfDef.getId();
        this.active = pfDef.isActive();
        this.name = pfDef.getName();
        this.namespaceCode = pfDef.getNamespaceCode();
        this.typeId = pfDef.getTypeId();
        this.description = pfDef.getDescription();
        this.versionNumber = pfDef.getVersionNumber();
    }

    /**
     * Invoked to rebuild the type attribute bos and attributes value map based on the type id
     */
    public void rebuildTypeAttributes() {
        this.attributeBos = new ArrayList<PeopleFlowAttributeBo>();
        this.attributeValues = new HashMap<String, String>();

        KewTypeDefinition typeDefinition = KewApiServiceLocator.getKewTypeRepositoryService().getTypeById(this.typeId);
        if ((typeDefinition.getAttributes() != null) && !typeDefinition.getAttributes().isEmpty()) {
            List<KewTypeAttribute> typeAttributes = new ArrayList<KewTypeAttribute>(typeDefinition.getAttributes());

            List<String> sortAttributes = new ArrayList<String>();
            sortAttributes.add(KEWPropertyConstants.SEQUENCE_NUMBER);
            Collections.sort(typeAttributes, new BeanPropertyComparator(sortAttributes));

            for (KewTypeAttribute typeAttribute: typeAttributes) {
                PeopleFlowAttributeBo attributeBo = PeopleFlowAttributeBo.from(typeAttribute.getAttributeDefinition(),
                        null, this, null);
                this.attributeBos.add(attributeBo);
                this.attributeValues.put(typeAttribute.getAttributeDefinition().getName(), "");
            }
        }
    }

    /**
     * Updates the values in the attribute bos from the attribute values map.
     */
    public void updateAttributeBoValues() {
        for (PeopleFlowAttributeBo attributeBo : this.attributeBos) {
            if (this.attributeValues.containsKey(attributeBo.getAttributeDefinition().getName())) {
                String attributeValue = this.attributeValues.get(attributeBo.getAttributeDefinition().getName());
                attributeBo.setValue(attributeValue);
            }
        }
    }

    /**
     * Updates the values in the attribute values map from the attribute bos and updates the members.
     */
    @PostLoad
    protected void postLoad() {
        this.attributeValues = new HashMap<String, String>();
        for (PeopleFlowAttributeBo attributeBo: attributeBos) {
            this.attributeValues.put(attributeBo.getAttributeDefinition().getName(), attributeBo.getValue());
        }
        for (PeopleFlowMemberBo member: members) {
            if (member.getMemberName() == null) {
                member.updateRelatedObject();
            }
            for (PeopleFlowDelegateBo delegate: member.getDelegates()) {
                if (delegate.getMemberName() == null) {
                    delegate.updateRelatedObject();
                }
            }
        }
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

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<PeopleFlowAttributeBo> getAttributeBos() {
        return this.attributeBos;
    }

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> results = new HashMap<String, String>();

        if (null != this.attributeBos)
            for (PeopleFlowAttributeBo attr: this.attributeBos) {
                results.put(attr.getAttributeDefinition().getName(), attr.getValue());
            }

        return results;
    }

    public void setAttributeBos(List<PeopleFlowAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }

    public List<PeopleFlowMemberBo> getMembers() {
        return members;
    }

    public void setMembers(List<PeopleFlowMemberBo> members) {
        this.members = members;
    }

    public Map<String, String> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Map<String, String> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public KewTypeBo getTypeBo() {
        return typeBo;
    }

    public void setTypeBo(KewTypeBo typeBo) {
        this.typeBo = typeBo;
    }
}
