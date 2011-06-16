package org.kuali.rice.kim.impl.identity.affiliation

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation


public class EntityAffiliationBo extends PersistableBusinessObjectBase implements EntityAffiliationContract {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_AFLTN_ID")
	String id;

	@Column(name = "ENTITY_ID")
	String entityId;

	@Column(name = "AFLTN_TYP_CD")
	String affiliationTypeCode;

	@Column(name = "CAMPUS_CD")
	String campusCode;

	@ManyToOne(targetEntity=EntityAffiliationTypeBo.class, fetch=FetchType.EAGER, cascade = [])
	@JoinColumn(name = "AFLTN_TYP_CD", insertable = false, updatable = false)
	EntityAffiliationTypeBo affiliationType;

    @Type(type="yes_no")
	@Column(name="DFLT_IND")
	boolean defaultValue;

    @Type(type="yes_no")
	@Column(name="ACTV_IND")
    boolean active;

  /*
   * Converts a mutable EntityAffiliationBo to an immutable EntityAffiliation representation.
   * @param bo
   * @return an immutable EntityAffiliation
   */
  static EntityAffiliation to(EntityAffiliationBo bo) {
    if (bo == null) { return null }
    return EntityAffiliation.Builder.create(bo).build()
  }

  /**
   * Creates a EntityAffiliationBo business object from an immutable representation of a EntityAffiliation.
   * @param an immutable EntityAffiliation
   * @return a EntityAffiliationBo
   */
  static EntityAffiliationBo from(EntityAffiliation immutable) {
    if (immutable == null) {return null}

    EntityAffiliationBo bo = new EntityAffiliationBo()
    bo.active = immutable.active
    if (immutable.affiliationType != null) {
    	bo.affiliationTypeCode = immutable.affiliationType.code
        bo.affiliationType = EntityAffiliationTypeBo.from(immutable.affiliationType)
  	}

    bo.id = immutable.id
    bo.campusCode = immutable.campusCode
    bo.entityId = immutable.entityId
    bo.active = immutable.active
    bo.defaultValue = immutable.defaultValue
    bo.versionNumber = immutable.versionNumber

    return bo;
  }


    @Override
    EntityAffiliationTypeBo getAffiliationType() {
        return this.affiliationType
    }

}
