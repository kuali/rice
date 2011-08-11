package org.kuali.rice.kew.impl.type

import org.kuali.rice.krad.bo.MutableInactivatable

// TODO: implement contract interface
public class KewTypeAttributeBo extends org.kuali.rice.krad.bo.PersistableBusinessObjectBase implements MutableInactivatable {

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
            attributeDefinitionId = null;
        }
        this.attributeDefinition = attrDef;
    }

//	/**
//	 * Converts a mutable bo to it's immutable counterpart
//	 * @param bo the mutable business object
//	 * @return the immutable object
//	 */
//	static KewTypeAttribute to(KewTypeAttributeBo bo) {
//      throw new java.lang.UnsupportedOperationException();
//	}
//
//	/**
//	 * Converts a immutable object to it's mutable bo counterpart
//	 * @param im immutable object
//	 * @return the mutable bo
//	 */
//	static KewTypeAttributeBo from(KrmsTypeAttribute im) {
//      throw new java.lang.UnsupportedOperationException();
//	}

}