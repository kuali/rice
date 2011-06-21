package org.kuali.rice.kim.framework.identity.employment

import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public class EntityEmploymentTypeEbo extends PersistableBusinessObjectBase implements TypeContract {
    String code;
    String name;
    boolean active;
    String sortCode;


    /**
   * Converts a mutable EmploymentTypeEbo to an immutable EmploymentType representation.
   * @param bo
   * @return an immutable EmploymentType
   */
  static Type to(EntityEmploymentTypeEbo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a EmploymentType business object from an immutable representation of a EmploymentType.
   * @param an immutable EmploymentType
   * @return a EmploymentTypeBo
   */
  static EntityEmploymentTypeEbo from(Type immutable) {
    if (immutable == null) {return null}

    EntityEmploymentTypeEbo bo = new EntityEmploymentTypeEbo()
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
