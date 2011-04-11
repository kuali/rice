package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.Action
import org.kuali.rice.krms.api.repository.ActionContract

public class ActionBo extends PersistableBusinessObjectBase implements ActionContract {

	def String actionId
	def String namespace
	def String name
	def String description
	def String typeId
	def String ruleId
	def Integer sequenceNumber
	def List<ActionAttributeBo> attributes
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static Action to(ActionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.Action.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static ActionBo from(Action im) {
	   if (im == null) { return null }

	   ActionBo bo = new ActionBo()
	   bo.actionId = im.actionId
	   bo.namespace = im.namespace
	   bo.name = im.name
	   bo.typeId = im.typeId
	   bo.description = im.description
	   bo.ruleId = im.ruleId
	   bo.attributes = new ArrayList<ActionAttributeBo>()
	   for (attr in im.attributes){
		   bo.attributes.add (ActionAttributeBo.from(attr))
	   }

	   return bo
   }
 
} 