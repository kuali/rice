package org.kuali.rice.kim.impl.identity.residency;

import org.eclipse.persistence.annotations.Customizer;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.kuali.rice.kim.api.identity.residency.EntityResidency;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_ENTITY_RESIDENCY_ID_S", property="id")
@Table(name = "KRIM_ENTITY_RESIDENCY_T")
public class EntityResidencyBo extends PersistableBusinessObjectBase implements EntityResidencyContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "DETERMINATION_METHOD")
    private String determinationMethod;
    @Column(name = "IN_STATE")
    private String inState;

    public static EntityResidency to(EntityResidencyBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityResidency.Builder.create(bo).build();
    }

    /**
     * Creates a EntityResidencyBo business object from an immutable representation of a EntityResidency.
     *
     * @param immutable an immutable EntityResidency
     * @return a EntityResidencyBo
     */
    public static EntityResidencyBo from(EntityResidency immutable) {
        if (immutable == null) {
            return null;
        }

        EntityResidencyBo bo = new EntityResidencyBo();
        bo.entityId = immutable.getEntityId();
        bo.id = immutable.getId();
        bo.determinationMethod = immutable.getDeterminationMethod();
        bo.inState = immutable.getInState();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public DateTime getEstablishedDate() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DateTime getChangeDate() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCountryCode() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCountyCode() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getStateProvinceCode() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CodedAttributeContract getResidencyStatus() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CodedAttributeContract getResidencyType() {
        return null;//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getDeterminationMethod() {
        return determinationMethod;
    }

    public void setDeterminationMethod(String determinationMethod) {
        this.determinationMethod = determinationMethod;
    }

    @Override
    public String getInState() {
        return inState;
    }

    public void setInState(String inState) {
        this.inState = inState;
    }

}
