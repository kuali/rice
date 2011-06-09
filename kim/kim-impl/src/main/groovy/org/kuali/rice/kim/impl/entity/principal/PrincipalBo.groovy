package org.kuali.rice.kim.impl.entity.principal

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.Column
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.entity.principal.PrincipalContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.kim.api.entity.principal.Principal

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_PRNCPL_T")
class PrincipalBo extends PersistableBusinessObjectBase implements PrincipalContract {
    private static final long serialVersionUID = 4480581610252159267L;

	@Id
	@Column(name="PRNCPL_ID", columnDefinition="VARCHAR(40)")
	String principalId;

	@Column(name="PRNCPL_NM")
	String principalName;

	@Column(name="ENTITY_ID")
	String entityId;

	@Column(name="PRNCPL_PSWD")
	String password;

	@Column(name="ACTV_IND")
	@Type(type="yes_no")
	boolean active;
    
     /*
   * Converts a mutable PrincipalBo to an immutable Principal representation.
   * @param bo
   * @return an immutable Principal
   */
  static Principal to(PrincipalBo bo) {
    if (bo == null) { return null }
    return Principal.Builder.create(bo).build()
  }

  /**
   * Creates a PrincipalBo business object from an immutable representation of a Principal.
   * @param an immutable Principal
   * @return a PrincipalBo
   */
  static PrincipalBo from(Principal immutable) {
    if (immutable == null) {return null}

    PrincipalBo bo = new PrincipalBo()
    bo.active = immutable.active
    bo.principalId = immutable.principalId
    bo.entityId = immutable.entityId
    bo.principalName = immutable.principalName
    bo.password = immutable.password
    bo.active = immutable.active
    bo.versionNumber = immutable.versionNumber
    bo.objectId = immutable.objectId

    return bo;
  }
}
