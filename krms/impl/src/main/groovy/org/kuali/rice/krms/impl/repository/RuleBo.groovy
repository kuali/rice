package org.kuali.rice.krms.impl.repository

import java.util.Map;
import java.util.Map.Entry;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;


public class RuleBo extends PersistableBusinessObjectBase implements RuleDefinitionContract {

	def String id
	def String namespace
    def String description
	def String name
	def String typeId
	def String propId

	def PropositionBo proposition
	def List<ActionBo> actions	
	def Set<RuleAttributeBo> attributeBos
    //def List<PropositionBo> allChildPropositions
	
   public PropositionBo getProposition(){
	   return proposition
   }
   
	public Map<String, String> getAttributes() {
		HashMap<String, String> attributes = new HashMap<String, String>();
		for (attr in attributeBos) {
			attributes.put( attr.attributeDefinition.name, attr.value )
		}
		return attributes;
	}
	
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
	   bo.id = im.id
	   bo.namespace = im.namespace
	   bo.name = im.name
       bo.description = im.description
	   bo.typeId = im.typeId
	   bo.propId = im.propId
	   bo.proposition = PropositionBo.from(im.proposition)
	   bo.versionNumber = im.versionNumber
	   
	   bo.actions = new ArrayList<ActionBo>()
	   for (action in im.actions){
		   bo.actions.add( ActionBo.from(action) )
	   }

	   // build the set of agenda attribute BOs
	   Set<RuleAttributeBo> attrs = new HashSet<RuleAttributeBo>();

	   // for each converted pair, build an AgendaAttributeBo and add it to the set
	   RuleAttributeBo attributeBo;
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