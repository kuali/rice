package org.kuali.rice.shareddata.impl.campus

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.shareddata.api.campus.CampusContract

@Entity
@Table(name="KRNS_CAMPUS_T")
public class CampusBo extends PersistableBusinessObjectBase implements CampusContract {
	private static final long serialVersionUID = 787567094298971223L;
	@Id
	@Column(name="CAMPUS_CD")
	def String code;
	
	@Column(name="CAMPUS_NM")
	def String name;
	
	@Column(name="CAMPUS_SHRT_NM")
	def String shortName;
	
	@Column(name="CAMPUS_TYP_CD")
	def String campusTypeCode;
	
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	def boolean active; 

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CAMPUS_TYP_CD", insertable=false, updatable=false)
	def CampusTypeBo campusType;
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static org.kuali.rice.shareddata.api.campus.Campus to(CampusBo bo) {
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
   static CampusBo from(org.kuali.rice.shareddata.api.campus.Campus im) {
	   if (im == null) {
		   return null
	   }

	   CampusBo bo = new CampusBo()
	   bo.code = im.code
	   bo.name = im.name
	   bo.shortName = im.shortName
	   bo.active = im.active
	   bo.campusTypeCode = im.campusType.code
 
	   bo.campusType = CampusTypeBo.from(im.campusType)


	   return bo
   }
   
   @Override
   CampusTypeBo getCampusType() {
	   return campusType
   }
} 