package org.kuali.rice.kim.impl.identity.citizenship

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_CTZNSHP_STAT_T")
public class EntityCitizenshipStatusBo extends PersistableBusinessObjectBase implements TypeContract {
    @Id
    @Column(name="CTZNSHP_STAT_CD")
    String code;
    @Column(name="NM")
    String name;
    @org.hibernate.annotations.Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;
    @Column(name="DISPLAY_SORT_CD")
    String sortCode;


    /**
   * Converts a mutable EntityCitizenshipStatusBo to an immutable EntityCitizenshipStatus representation.
   * @param bo
   * @return an immutable EntityCitizenshipStatus
   */
  static Type to(EntityCitizenshipStatusBo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a EntityCitizenshipStatusBo business object from an immutable representation of a EntityCitizenshipStatus.
   * @param an immutable EntityCitizenshipStatus
   * @return a EntityCitizenshipStatusBo
   */
  static EntityCitizenshipStatusBo from(Type immutable) {
    if (immutable == null) {return null}

    EntityCitizenshipStatusBo bo = new EntityCitizenshipStatusBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
