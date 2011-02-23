package org.kuali.rice.shareddata.impl.campus

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.campus.CampusType
import org.kuali.rice.shareddata.api.campus.CampusTypeContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.shareddata.api.campus.CampusContract;

@Entity
@Table(name="KRNS_CMP_TYP_T")
public class CampusTypeBo extends PersistableBusinessObjectBase implements Inactivateable, CampusTypeContract {
	@Id
	@Column(name="CAMPUS_TYP_CD")
	def String code;
	
	@Column(name="CMP_TYP_NM")
	def String name;
	
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	def boolean active;
	
	/**
	* Converts a mutable CountryBo to an immutable Country representation.
	* @param bo
	* @return an immutable Country
	*/
   static CampusType to(CampusTypeBo bo) {
	 if (bo == null) { return null }
	 return CampusType.Builder.create(bo).build()
   }
 
   /**
	* Creates a CountryBo business object from an immutable representation of a Country.
	* @param an immutable Country
	* @return a CountryBo
	*/
   static CampusTypeBo from(CampusType im) {
	 if (im == null) {return null}
 
	 CampusTypeBo bo = new CampusTypeBo()
	 bo.code = im.code
	 bo.name = im.name
	 bo.active = im.active
 
	 return bo;
   }
}