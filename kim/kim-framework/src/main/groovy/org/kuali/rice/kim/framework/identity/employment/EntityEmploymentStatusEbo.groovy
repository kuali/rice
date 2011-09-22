package org.kuali.rice.kim.framework.identity.employment

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public class EntityEmploymentStatusEbo extends PersistableBusinessObjectBase implements CodedAttributeContract {
    String code;
    String name;
    boolean active;
    String sortCode;


    /**
   * Converts a mutable EmploymentStatusEbo to an immutable EmploymentStatus representation.
   * @param bo
   * @return an immutable EmploymentStatus
   */
  static CodedAttribute to(EntityEmploymentStatusEbo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a EmploymentStatus business object from an immutable representation of a EmploymentStatus.
   * @param an immutable EmploymentStatus
   * @return a EmploymentStatusBo
   */
  static EntityEmploymentStatusEbo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityEmploymentStatusEbo bo = new EntityEmploymentStatusEbo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }

  void refresh() { }
}
