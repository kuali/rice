package org.kuali.rice.krms.impl.repository

import java.util.Map.Entry;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;

public class ActionBo extends PersistableBusinessObjectBase implements ActionDefinitionContract {

	def String id
	def String namespace
	def String name
	def String description
	def String typeId
	def String ruleId
	def Integer sequenceNumber
	
	def Set<ActionAttributeBo> attributeBos
	
	public Map<String, String> getAttributes() {
		HashMap<String, String> attributes = new HashMap<String, String>();
		for (attr in attributeBos) {
            if (attr.attributeDefinition == null) {
                attributes.put("", "");
            } else {
			  attributes.put( attr.attributeDefinition.name, attr.value )
            }
		}
		return attributes;
	}

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static ActionDefinition to(ActionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.action.ActionDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static ActionBo from(ActionDefinition im) {
	   if (im == null) { return null }

	   ActionBo bo = new ActionBo()
	   bo.id = im.id
	   bo.namespace = im.namespace
	   bo.name = im.name
	   bo.typeId = im.typeId
	   bo.description = im.description
	   bo.ruleId = im.ruleId
	   bo.sequenceNumber = im.sequenceNumber
	   bo.versionNumber = im.versionNumber
	   
	   // build the set of action attribute BOs
	   Set<ActionAttributeBo> attrs = new HashSet<ActionAttributeBo>();

	   // for each converted pair, build an ActionAttributeBo and add it to the set
	   ActionAttributeBo attributeBo;
	   for (Entry<String,String> entry  : im.getAttributes().entrySet()){
		   KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator
		   		.getKrmsAttributeDefinitionService()
		   		.getKrmsAttributeBo(entry.getKey(), im.getNamespace());
		   attributeBo = new ActionAttributeBo();
		   attributeBo.setActionId( im.getId() );
		   attributeBo.setAttributeDefinitionId( attrDefBo.getId() );
		   attributeBo.setValue( entry.getValue() );
		   attributeBo.setAttributeDefinition( attrDefBo );
		   attrs.add( attributeBo );
	   }
	   bo.setAttributeBos(attrs);
	   return bo
   }
 
} 