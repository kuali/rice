package org.kuali.rice.kew.impl.type

import org.kuali.rice.kew.api.repository.type.KewTypeAttribute
import org.kuali.rice.kew.api.repository.type.KewTypeAttributeContract
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase


public class KewTypeAttributeBo extends PersistableBusinessObjectBase implements MutableInactivatable, KewTypeAttributeContract {
	
	def String id
	def String typeId
	def String attributeDefinitionId
	def Integer sequenceNumber
	def boolean active = true
	def KewAttributeDefinitionBo attributeDefinition

    public void setAttributeDefinition(KewAttributeDefinitionBo attrDef) {
        if (attrDef != null) {
             attributeDefinitionId = attrDef.getId()
       } else {
       // TODO:  why is attributeDefinitionId getting set to null here?   Can this be removed?
       //attributeDefinitionId = null;
      }
      this.attributeDefinition = attrDef;
   }

    /**
    * Converts a mutable bo to it's immutable counterpart
    * @param bo the mutable business object
    * @return the immutable object
    */
   static KewTypeAttribute to(KewTypeAttributeBo bo) {
       if (bo == null) { return null }
       return org.kuali.rice.kew.api.repository.type.KewTypeAttribute.Builder.create(bo).build()
   }

   /**
    * Converts a immutable object to it's mutable bo counterpart
    * @param im immutable object
    * @return the mutable bo
    */
   static KewTypeAttributeBo from(KewTypeAttribute im) {
       if (im == null) { return null }

       KewTypeAttributeBo bo = new KewTypeAttributeBo()
       bo.id = im.id
       bo.typeId = im.typeId
       bo.attributeDefinitionId = im.attributeDefinitionId
       bo.sequenceNumber = im.sequenceNumber
       bo.active = im.active
       bo.attributeDefinition = KewAttributeDefinitionBo.from(im.attributeDefinition)
       return bo
   }
   
   @Override
   KewAttributeDefinitionBo getAttributeDefinition() {
       return attributeDefinition
   }
}