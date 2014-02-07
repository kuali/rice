package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.BaseAttributeContract;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * This class contains the common elements of a KRMS attribute.
 * <p>
 * Attributes provide a way to attach custom data to an entity based on that entity's type.
 * Rules, Actions, Contexts, Agendas and Term Resolvers have their own specific
 * attribute types. This class contains their common fields.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class BaseAttributeBo implements BaseAttributeContract, Versioned {

    @Column(name="ATTR_VAL")
    private String value;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    public String getAttributeDefinitionId() {
        if (getAttributeDefinition() != null) {
            return getAttributeDefinition().getId();
        }

        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
