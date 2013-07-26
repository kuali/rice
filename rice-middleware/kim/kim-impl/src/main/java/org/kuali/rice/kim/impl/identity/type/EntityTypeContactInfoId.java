package org.kuali.rice.kim.impl.identity.type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Id;

public class EntityTypeContactInfoId {
    private EntityTypeContactInfoId() {
        entityTypeCode = null;
        entityId = null;
    }

    public EntityTypeContactInfoId(String entityId, String entityTypeCode) {
        this.entityId = entityId;
        this.entityTypeCode = entityTypeCode;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public final String getEntityTypeCode() {
        return entityTypeCode;
    }

    public final String getEntityId() {
        return entityId;
    }

    @Id @Column(name = "ENT_TYP_CD") private final String entityTypeCode;
    @Id @Column(name = "ENTITY_ID") private final String entityId;
}
