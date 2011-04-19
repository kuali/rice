package org.kuali.rice.krms.impl.repository

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterContract;

@Entity
@Table(name="KRMS_PROP_PARM_T")
public class PropositionParameterBo extends PersistableBusinessObjectBase implements PropositionParameterContract {

	@Id
	@GeneratedValue(generator="KRMS_PROP_PARM_S")
	@GenericGenerator(name="KRMS_PROP_PARM_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters=[
		@Parameter(name="sequence_name",value="KRMS_PROP_PARM_S"),
		@Parameter(name="value_column",value="id")
	])
	@Column(name="ID")
	def String id
	
	@Column(name="PROP_ID")
	def String propId
	
	@Column(name="PARM_VAL")
	def String value
	
	@Column(name="PARM_TYP_CD")
	def String parameterType
	
	@Column(name="SEQ_NO")
	def Integer sequenceNumber
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static PropositionParameter to(PropositionParameterBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.proposition.PropositionParameter.Builder.create(bo).build()
   }

	/**
	* Converts a list of mutable bos to it's immutable counterpart
	* @param bos the list of smutable business objects
	* @return and immutable list containing the immutable objects
	*/
   static List<PropositionParameter> to(List<PropositionParameterBo> bos) {
	   if (bos == null) { return null }
	   List<PropositionParameter> parms = new ArrayList<PropositionParameter>();
	   for (PropositionParameter p : bos){
		   parms.add(PropositionParameter.Builder.create(p).build())
	   }
	   return Collections.unmodifiableList(parms)
   }
   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static PropositionParameterBo from(PropositionParameter im) {
	   if (im == null) { return null }

	   PropositionParameterBo bo = new PropositionParameterBo()
	   bo.id = im.id
	   bo.propId = im.propId
	   bo.value = im.value
	   bo.parameterType = im.parameterType
	   bo.sequenceNumber = im.sequenceNumber

	   return bo
   }
   
   static List<PropositionParameterBo> from(List<PropositionParameter> ims){
	   if (ims == null) {return null }
	   List<PropositionParameterBo> bos = new ArrayList<PropositionParameterBo>();
	   for (PropositionParameterBo im : ims){
		   PropositionParameterBo bo = new PropositionParameterBo()
		   bo.id = im.id
		   bo.propId = im.propId
		   bo.value = im.value
		   bo.parameterType = im.parameterType
		   bo.sequenceNumber = im.sequenceNumber
	   	   bos.add(bo);
	   }
	   return Collections.unmodifiableList(bos)
   }
 
} 