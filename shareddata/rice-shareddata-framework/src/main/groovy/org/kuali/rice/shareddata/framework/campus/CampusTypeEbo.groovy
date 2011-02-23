package org.kuali.rice.shareddata.framework.campus

import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.campus.CampusType
import org.kuali.rice.shareddata.api.campus.CampusTypeContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.shareddata.api.campus.CampusContract;

public class CampusTypeEbo extends PersistableBusinessObjectBase implements Inactivateable, CampusTypeContract, ExternalizableBusinessObject {

	def String code;
	def String name;
	def boolean active;
	
	/**
	* Converts a mutable CountryBo to an immutable Country representation.
	* @param bo
	* @return an immutable Country
	*/
   static CampusType to(CampusTypeEbo bo) {
	 if (bo == null) { return null }
	 return CampusType.Builder.create(bo).build()
   }
 
   /**
	* Creates a CountryBo business object from an immutable representation of a Country.
	* @param an immutable Country
	* @return a CountryBo
	*/
   static CampusTypeEbo from(CampusType im) {
	 if (im == null) {return null}
 
	 CampusTypeEbo bo = new CampusTypeEbo()
	 bo.code = im.code
	 bo.name = im.name
	 bo.active = im.active
 
	 return bo;
   }
}