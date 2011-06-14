package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract;


public class AgendaBo extends PersistableBusinessObjectBase implements AgendaDefinitionContract {

	def String id
	def String namespace
	def String name
	def String typeId
	def String contextId
	def Boolean active

	def String firstItemId
	def Set<AgendaAttributeBo> attributeBos
	
	def List<AgendaItemBo> items

	public String getNamespaceCode(){
		return namespace
	}

    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (attr in attributeBos) {
            attributes.put( attr.attributeDefinition.name, attr.value )
        }
        return attributes;
    }
    

	
//	/**
//	 * Converts a mutable bo to it's immutable counterpart
//	 * @param bo the mutable business object
//	 * @return the immutable object
//	 */
//	static AgendaDefinition to(AgendaBo bo) {
//		if (bo == null) { return null }
//		return org.kuali.rice.krms.api.repository.agenda.AgendaDefinition.Builder.create(bo).build()
//	}
//
//
//	/**
//	* Converts a immutable object to it's mutable bo counterpart
//	* TODO: move to() and from() to impl service
//	* @param im immutable object
//	* @return the mutable bo
//	*/
//   static public AgendaBo from(AgendaDefinition im) {
//	   if (im == null) { return null }
//
//	   AgendaBo bo = new AgendaBo()
//	   bo.setId( im.getId() )
//	   bo.setNamespace( im.getNamespaceCode() )
//	   bo.setName( im.getName() )
//	   bo.setTypeId( im.getTypeId() )
//	   bo.setContextId( im.getContextId() )
//	   bo.setFirstItemId( im.getFirstItemId() )
//
//	   Map<String,String> attrList = convertAttributeKeys
//	   Set<AgendaAttributeBo> attrList = new HashSet<AgendaAttributeBo>()
//	   for (attr in im.getAttributes()){
//		   
//		   attrList.add ( AgendaAttributeBo.from() )
//	   }
//	   bo.setAttributes(attrList)
//	   return bo
//   }
   

} 