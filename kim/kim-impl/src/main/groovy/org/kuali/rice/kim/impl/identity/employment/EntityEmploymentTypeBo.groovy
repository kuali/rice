package org.kuali.rice.kim.impl.identity.employment

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_EMP_TYP_T")
public class EntityEmploymentTypeBo extends PersistableBusinessObjectBase implements CodedAttributeContract {
    @Id
    @Column(name="EMP_TYP_CD")
    String code;
    @Column(name="NM")
    String name;
    @org.hibernate.annotations.Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;
    @Column(name="DISPLAY_SORT_CD")
    String sortCode;


    /**
   * Converts a mutable EmploymentTypeBo to an immutable EmploymentType representation.
   * @param bo
   * @return an immutable EmploymentType
   */
  static CodedAttribute to(EntityEmploymentTypeBo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a EmploymentType business object from an immutable representation of a EmploymentType.
   * @param an immutable EmploymentType
   * @return a EmploymentTypeBo
   */
  static EntityEmploymentTypeBo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityEmploymentTypeBo bo = new EntityEmploymentTypeBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
