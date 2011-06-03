package org.kuali.rice.kim.framework.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.entity.Type
import org.kuali.rice.kim.api.entity.TypeContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.kns.bo.ExternalizableBusinessObject

public class EntityTypeEbo implements TypeContract, ExternalizableBusinessObject {
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
  static Type to(EntityTypeEbo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a EntityType external business object from an immutable representation of a Type.
   * @param an immutable Type
   * @return a EntityTypeEbo
   */
  static EntityTypeEbo from(Type immutable) {
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
