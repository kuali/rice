package org.kuali.rice.krms.impl.repository

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.KrmsTypeContract;

@Entity
@Table(name="KRMS_TYP_T")
public class KrmsTypeBo extends PersistableBusinessObjectBase implements Inactivateable, KrmsTypeContract {

	@Id
	@Column(name="TYP_ID")
	def String id;
	
	@Column(name="NM")
	def String name;
	
	@Column(name="NMSPC_CD")
	def String namespace;
	
	@Column(name="SRVC_NM")
	def String serviceName;
	
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	def boolean active; 
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static org.kuali.rice.krms.api.repository.KrmsType to(KrmsTypeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.KrmsType.Builder.create(bo).build();
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static KrmsTypeBo from(org.kuali.rice.krms.api.repository.KrmsType im) {
	   if (im == null) { return null }

	   KrmsTypeBo bo = new KrmsTypeBo()
	   bo.id = im.id
	   bo.name = im.name
	   bo.namespace = im.namespace
	   bo.serviceName = im.serviceName
	   bo.active = im.active

	   return bo
   }
 
} 