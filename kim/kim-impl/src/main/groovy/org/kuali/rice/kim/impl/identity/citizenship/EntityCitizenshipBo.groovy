package org.kuali.rice.kim.impl.identity.citizenship

import javax.persistence.Table
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column
import java.sql.Timestamp
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name = "KRIM_ENTITY_CTZNSHP_T")
class EntityCitizenshipBo extends PersistableBusinessObjectBase implements EntityCitizenshipContract {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_CTZNSHP_ID")
	String id;

	@Column(name = "ENTITY_ID")
	String entityId;
	
	@Column(name = "POSTAL_CNTRY_CD")
	String countryCode;

	@Column(name = "CTZNSHP_STAT_CD")
	String statusCode;

	@Column(name = "strt_dt")
	Timestamp startDate;

	@Column(name = "end_dt")
	Timestamp endDate;

	@ManyToOne(targetEntity=EntityCitizenshipStatusBo.class, fetch=FetchType.EAGER, cascade=[])
	@JoinColumn(name = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
	EntityCitizenshipStatusBo status;

    @Type(type="yes_no")
	@Column(name="ACTV_IND")
    boolean active;
    
  /*
   * Converts a mutable EntityCitizenshipBo to an immutable EntityCitizenship representation.
   * @param bo
   * @return an immutable EntityCitizenship
   */
  static EntityCitizenship to(EntityCitizenshipBo bo) {
    if (bo == null) { return null }
    return EntityCitizenship.Builder.create(bo).build()
  }

  /**
   * Creates a EntityCitizenshipBo business object from an immutable representation of a EntityCitizenship.
   * @param an immutable EntityCitizenship
   * @return a EntityCitizenshipBo
   */
  static EntityCitizenshipBo from(EntityCitizenship immutable) {
    if (immutable == null) {return null}

    EntityCitizenshipBo bo = new EntityCitizenshipBo()
    bo.active = immutable.active
    if (immutable.status != null) {
    	bo.statusCode = immutable.status.code
        bo.status = EntityCitizenshipStatusBo.from(immutable.status)
  	}
    bo.id = immutable.id
    bo.entityId = immutable.entityId
    bo.countryCode = immutable.countryCode
    bo.startDate = immutable.startDate
    bo.endDate = immutable.endDate
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }


    @Override
    EntityCitizenshipStatusBo getStatus() {
        return this.status
    }
}
