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

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.typerelation.RelationshipType;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelationContract;
import org.kuali.rice.krms.impl.repository.jpa.RelationshipTypeConverter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * The mutable implementation of the @{link TypeTypeRelationContract} interface, the counterpart to the immutable implementation {@link TypeTypeRelation}.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@Entity
@Table(name = "KRMS_TYP_RELN_T")
public class TypeTypeRelationBo implements TypeTypeRelationContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @Column(name = "FROM_TYP_ID")
    private String fromTypeId;

    @Column(name = "TO_TYP_ID")
    private String toTypeId;

    @Column(name = "RELN_TYP")
    @Convert(converter = RelationshipTypeConverter.class)

    @Enumerated(value = EnumType.ORDINAL)
    private RelationshipType relationshipType;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @PortableSequenceGenerator(name = "KRMS_TYP_RELN_S")
    @GeneratedValue(generator = "KRMS_TYP_RELN_S")
    @Id
    @Column(name = "TYP_RELN_ID")
    private String id;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToOne(targetEntity = KrmsTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "FROM_TYP_ID", referencedColumnName = "TYP_ID", insertable = false, updatable = false)
    private KrmsTypeBo fromType;

    @ManyToOne(targetEntity = KrmsTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "TO_TYP_ID", referencedColumnName = "TYP_ID", insertable = false, updatable = false)
    private KrmsTypeBo toType;

    /**
     * Default Constructor
     * 
     */
    public TypeTypeRelationBo() {
    }

    @Override
    public String getFromTypeId() {
        return this.fromTypeId;
    }

    @Override
    public String getToTypeId() {
        return this.toTypeId;
    }

    @Override
    public RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    @Override
    public Integer getSequenceNumber() {
        return this.sequenceNumber;
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
     * Sets the value of fromTypeId on this builder to the given value.
     * 
     * @param fromTypeId the fromTypeId value to set.
     * 
     */
    public void setFromTypeId(String fromTypeId) {
        this.fromTypeId = fromTypeId;
    }

    /**
     * Sets the value of toTypeId on this builder to the given value.
     * 
     * @param toTypeId the toTypeId value to set.
     * 
     */
    public void setToTypeId(String toTypeId) {
        this.toTypeId = toTypeId;
    }

    /**
     * Sets the value of relationshipType on this builder to the given value.
     * 
     * @param relationshipType the relationshipType value to set.
     * 
     */
    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * Sets the value of sequenceNumber on this builder to the given value.
     * 
     * @param sequenceNumber the sequenceNumber value to set.
     * 
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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
     * Converts a mutable {@link TypeTypeRelationBo} to its immutable counterpart, {@link TypeTypeRelation}.
     * @param typeTypeRelationBo the mutable business object.
     * @return a {@link TypeTypeRelation} the immutable object.
     * 
     */
    public static TypeTypeRelation to(TypeTypeRelationBo typeTypeRelationBo) {
        if (typeTypeRelationBo == null) {
            return null;
        }

        return TypeTypeRelation.Builder.create(typeTypeRelationBo).build();
    }

    /**
     * Converts a immutable {@link TypeTypeRelation} to its mutable {@link TypeTypeRelationBo} counterpart.
     * @param typeTypeRelation the immutable object.
     * @return a {@link TypeTypeRelationBo} the mutable TypeTypeRelationBo.
     * 
     */
    public static org.kuali.rice.krms.impl.repository.TypeTypeRelationBo from(TypeTypeRelation typeTypeRelation) {
        if (typeTypeRelation == null) {
            return null;
        }

        TypeTypeRelationBo typeTypeRelationBo = new TypeTypeRelationBo();
        typeTypeRelationBo.setFromTypeId(typeTypeRelation.getFromTypeId());
        typeTypeRelationBo.setToTypeId(typeTypeRelation.getToTypeId());
        typeTypeRelationBo.setRelationshipType(typeTypeRelation.getRelationshipType());
        typeTypeRelationBo.setSequenceNumber(typeTypeRelation.getSequenceNumber());
        typeTypeRelationBo.setId(typeTypeRelation.getId());
        typeTypeRelationBo.setActive(typeTypeRelation.isActive());
        typeTypeRelationBo.setVersionNumber(typeTypeRelation.getVersionNumber());

        return typeTypeRelationBo;
    }

    public KrmsTypeBo getFromType() {
        return fromType;
    }

    public void setFromType(KrmsTypeBo fromType) {
        this.fromType = fromType;
    }

    public KrmsTypeBo getToType() {
        return toType;
    }

    public void setToType(KrmsTypeBo toType) {
        this.toType = toType;
    }
}
