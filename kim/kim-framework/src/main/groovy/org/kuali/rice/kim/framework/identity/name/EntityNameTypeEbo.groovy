package org.kuali.rice.kim.framework.identity.name

import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.kim.api.identity.Type


class EntityNameTypeEbo implements TypeContract, ExternalizableBusinessObject {
    String code
    String name
    boolean active
    String sortCode
    Long versionNumber
	String objectId


    /**
   * Converts a mutable AddressTypeBo to an immutable AddressType representation.
   * @param bo
   * @return an immutable AddressType
   */
  static Type to(EntityNameTypeEbo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityNameTypeEbo from(Type immutable) {
    if (immutable == null) {return null}

    EntityNameTypeEbo bo = new EntityNameTypeEbo()
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
