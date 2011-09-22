package org.kuali.rice.kim.framework.identity

import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.kim.api.identity.CodedAttribute

import org.kuali.rice.krad.bo.ExternalizableBusinessObject

public class EntityTypeEbo implements CodedAttributeContract, ExternalizableBusinessObject {
    String code;
    String name;
    boolean active;
    String sortCode;
    Long versionNumber;
	String objectId;


    /**
   * Converts a mutable EntityTypeEbo to an immutable AddressType representation.
   * @param bo
   * @return an immutable Type
   */
  static CodedAttribute to(EntityTypeEbo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a EntityType external business object from an immutable representation of a Type.
   * @param an immutable Type
   * @return a EntityTypeEbo
   */
  static EntityTypeEbo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityTypeEbo bo = new EntityTypeEbo()
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
