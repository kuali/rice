package org.kuali.rice.kim.impl.identity.employment

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_EMP_STAT_T")
public class EntityEmploymentStatusBo extends PersistableBusinessObjectBase implements CodedAttributeContract {
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
  static CodedAttribute to(EntityEmploymentStatusBo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a EmploymentStatus business object from an immutable representation of a EmploymentStatus.
   * @param an immutable EmploymentStatus
   * @return a EmploymentStatusBo
   */
  static EntityEmploymentStatusBo from(CodedAttribute immutable) {
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
