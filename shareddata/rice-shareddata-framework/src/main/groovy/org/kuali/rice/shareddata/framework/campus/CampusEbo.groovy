package org.kuali.rice.shareddata.framework.campus



import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.campus.CampusContract

public class CampusEbo extends PersistableBusinessObjectBase implements Inactivateable, CampusContract, ExternalizableBusinessObject {
	private static final long serialVersionUID = 787567094298971223L;

	def String code;
	def String name;
	def String shortName;
	def String campusTypeCode;
	def boolean active;
	def CampusTypeEbo campusType;
	
	@Override
	CampusTypeEbo getCampusType() {
		return campusType
	}
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static org.kuali.rice.shareddata.api.campus.Campus to(CampusEbo bo) {
	   if (bo == null) {
		   return null
	   }

	   return org.kuali.rice.shareddata.api.campus.Campus.Builder.create(bo).build();
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static CampusEbo from(org.kuali.rice.shareddata.api.campus.Campus im) {
	   if (im == null) {
		   return null
	   }

	   CampusEbo bo = new CampusEbo()
	   bo.code = im.code
	   bo.name = im.name
	   bo.shortName = im.shortName
	   bo.active = im.active
	   bo.campusTypeCode = im.campusType.code
 
	   bo.campusType = CampusTypeEbo.from(im.campusType)


	   return bo
   }
   
   void refresh() { }

}