package org.kuali.rice.kim.impl.identity.employment

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_EMP_STAT_T")
public class EntityEmploymentStatusBo extends PersistableBusinessObjectBase implements TypeContract {
    @Id
    @Column(name="EMP_STAT_CD")
    String code;
    @Column(name="NM")
    String name;
    @org.hibernate.annotations.Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;
    @Column(name="DISPLAY_SORT_CD")
    String sortCode;


    /**
   * Converts a mutable EmploymentStatusBo to an immutable EmploymentStatus representation.
   * @param bo
   * @return an immutable EmploymentStatus
   */
  static Type to(EntityEmploymentStatusBo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a EmploymentStatus business object from an immutable representation of a EmploymentStatus.
   * @param an immutable EmploymentStatus
   * @return a EmploymentStatusBo
   */
  static EntityEmploymentStatusBo from(Type immutable) {
    if (immutable == null) {return null}

    EntityEmploymentStatusBo bo = new EntityEmploymentStatusBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
