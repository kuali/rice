package org.kuali.rice.kim.framework.identity.affiliation

import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationTypeContract
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType

class EntityAffiliationTypeEbo implements EntityAffiliationTypeContract, ExternalizableBusinessObject {
    String code;
    String name;
    boolean active;
    String sortCode;
    boolean employmentAffiliationType;
    Long versionNumber;
	String objectId;


    /**
   * Converts a mutable EntityEmailTypeEbo to an immutable Type representation.
   * @param bo
   * @return an immutable Type
   */
  static EntityAffiliationType to(EntityAffiliationTypeEbo bo) {
    if (bo == null) { return null }
    return EntityAffiliationType.Builder.create(bo).build()
  }

  /**
   * Creates a Type business object from an immutable representation of a Type.
   * @param an immutable Type
   * @return a EntityAddressTypeEbo
   */
  static EntityAffiliationTypeEbo from(EntityAffiliationType immutable) {
    if (immutable == null) {return null}

    EntityAffiliationTypeEbo bo = new EntityAffiliationTypeEbo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.employmentAffiliationType = immutable.employmentAffiliationType
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }

    void refresh() { }
}
