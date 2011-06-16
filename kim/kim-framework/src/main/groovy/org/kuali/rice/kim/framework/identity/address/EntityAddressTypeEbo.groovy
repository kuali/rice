package org.kuali.rice.kim.framework.identity.address

import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject


class EntityAddressTypeEbo implements TypeContract, ExternalizableBusinessObject {
    String code;
    String name;
    boolean active;
    String sortCode;
    Long versionNumber;
	String objectId;


    /**
   * Converts a mutable AddressTypeEbo to an immutable Type representation.
   * @param bo
   * @return an immutable Type
   */
  static Type to(EntityAddressTypeEbo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a Type business object from an immutable representation of a Type.
   * @param an immutable Type
   * @return a EntityAddressTypeEbo
   */
  static EntityAddressTypeEbo from(Type immutable) {
    if (immutable == null) {return null}

    EntityAddressTypeEbo bo = new EntityAddressTypeEbo()
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
