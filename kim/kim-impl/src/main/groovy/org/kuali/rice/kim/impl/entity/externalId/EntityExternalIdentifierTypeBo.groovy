package org.kuali.rice.kim.impl.entity.externalId

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.kuali.rice.kim.api.entity.Type
import org.kuali.rice.kim.api.entity.TypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_EXT_ID_TYP_T")
public class EntityExternalIdentifierTypeBo extends PersistableBusinessObjectBase implements TypeContract {
    @Id
    @Column(name="EXT_ID_TYP_CD")
    String code;
    @Column(name="NM")
    String name;
    @org.hibernate.annotations.Type(type="yes_no")
    @Column(name="ACTV_IND")
    boolean active;
    @Column(name="DISPLAY_SORT_CD")
    String sortCode;

    //TODO: need to create contract that includes this field
    @org.hibernate.annotations.Type(type="yes_no")
	@Column(name="ENCR_REQ_IND")
	protected boolean encryptionRequired;


    /**
   * Converts a mutable AddressTypeBo to an immutable AddressType representation.
   * @param bo
   * @return an immutable AddressType
   */
  static Type to(EntityExternalIdentifierTypeBo bo) {
    if (bo == null) { return null }
    return Type.Builder.create(bo).build()
  }

  /**
   * Creates a AddressType business object from an immutable representation of a AddressType.
   * @param an immutable AddressType
   * @return a AddressTypeBo
   */
  static EntityExternalIdentifierTypeBo from(Type immutable) {
    if (immutable == null) {return null}

    EntityExternalIdentifierTypeBo bo = new EntityExternalIdentifierTypeBo()
    bo.code = immutable.code
    bo.name = immutable.name
    bo.sortCode = immutable.sortCode
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
