package org.kuali.rice.kim.framework.identity.external

import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierTypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

class EntityExternalIdentifierTypeEbo extends PersistableBusinessObjectBase implements EntityExternalIdentifierTypeContract {
    String code;
    String name;
    boolean active;
    String sortCode;
	boolean encryptionRequired;


    /**
   * Converts a mutable AddressTypeBo to an immutable AddressType representation.
   * @param bo
   * @return an immutable AddressType
   */
  static EntityExternalIdentifierType to(EntityExternalIdentifierTypeEbo bo) {
    if (bo == null) { return null }
    return EntityExternalIdentifierType.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityExternalIdentifierTypeEbo from(EntityExternalIdentifierType immutable) {
    if (immutable == null) {return null}

    EntityExternalIdentifierTypeEbo bo = new EntityExternalIdentifierTypeEbo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.encryptionRequired = immutable.encryptionRequired
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }

  void refresh() { }
}
