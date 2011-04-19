package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleAttribute;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;


public class RuleBo extends PersistableBusinessObjectBase implements RuleDefinitionContract {

	def String ruleId
	def String namespace
	def String name
	def String typeId
	def String propId

	def PropositionBo proposition
	def List<ActionBo> actions	
	def Set<RuleAttributeBo> attributes
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static RuleDefinition to(RuleBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.rule.RuleDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static RuleBo from(RuleDefinition im) {
	   if (im == null) { return null }

	   RuleBo bo = new RuleBo()
	   bo.ruleId = im.ruleId
	   bo.namespace = im.namespace
	   bo.name = im.name
	   bo.typeId = im.typeId
	   bo.propId = im.propId
	   bo.attributes = new HashSet<RuleAttributeBo>()
	   for (attr in im.attributes){
		   bo.attributes.add ( RuleAttributeBo.from(attr) )
	   }
	   bo.actions = new ArrayList<ActionBo>()
	   for (action in im.actions){
		   bo.actions.add( ActionBo.from(action) )
	   }
	   return bo
   }
 
   public PropositionBo getProposition(){
	   return proposition
   }
   
} 