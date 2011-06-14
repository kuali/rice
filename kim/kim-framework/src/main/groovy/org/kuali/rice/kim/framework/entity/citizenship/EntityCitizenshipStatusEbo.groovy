package org.kuali.rice.kim.framework.entity.citizenship

import org.kuali.rice.kim.api.entity.TypeContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.kim.api.entity.Type

class EntityCitizenshipStatusEbo implements TypeContract, ExternalizableBusinessObject {
    String code;
    String name;
    boolean active;
    String sortCode;
    Long versionNumber;
	String objectId;


    /**
   * Converts a mutable EntityCitizenshipStatusEbo to an immutable EntityCitizenshipStatus representation.
   * @param bo
   * @return an immutable EntityCitizenshipStatus
   */
  static Type to(EntityCitizenshipStatusEbo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a EntityCitizenshipStatusEbo business object from an immutable representation of a EntityCitizenshipStatus.
   * @param an immutable EntityCitizenshipStatus
   * @return a EntityCitizenshipStatusEbo
   */
  static EntityCitizenshipStatusEbo from(Type immutable) {
    if (immutable == null) {return null}

    EntityCitizenshipStatusEbo bo = new EntityCitizenshipStatusEbo()
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
