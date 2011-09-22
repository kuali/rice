package org.kuali.rice.kim.impl.identity.address

import javax.persistence.Column
import javax.persistence.Id

import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import javax.persistence.Entity

import javax.persistence.Table

import org.kuali.rice.kim.api.identity.CodedAttributeContract

@Entity
@Table(name="KRIM_ADDR_TYP_T")
public class EntityAddressTypeBo extends PersistableBusinessObjectBase implements CodedAttributeContract {
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
  static CodedAttribute to(EntityAddressTypeBo bo) {
    if (bo == null) { return null }
    return CodedAttribute.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityAddressTypeBo from(CodedAttribute immutable) {
    if (immutable == null) {return null}

    EntityAddressTypeBo bo = new EntityAddressTypeBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
