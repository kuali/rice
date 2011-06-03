package org.kuali.rice.kim.impl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.entity.Type
import org.kuali.rice.kim.api.entity.TypeContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_ADDR_TYP_T")
public class EntityTypeBo extends PersistableBusinessObjectBase implements TypeContract {
    @Id
    @Column(name="ADDR_TYP_CD")
    String code;
    @Column(name="NM")
    String name;
    @org.hibernate.annotations.Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;
    @Column(name="DISPLAY_SORT_CD")
    String sortCode;


    /**
   * Converts a mutable AddressTypeBo to an immutable AddressType representation.
   * @param bo
   * @return an immutable AddressType
   */
  static Type to(EntityTypeBo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityTypeBo from(Type immutable) {
    if (immutable == null) {return null}

    EntityTypeBo bo = new EntityTypeBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
