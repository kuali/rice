package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.kew.impl.type.KewAttributeDefinitionBo
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition

class PeopleFlowAttributeBo extends PersistableBusinessObjectBase {

    String id
	String attributeDefinitionId
	String value
    String peopleFlowId

	def KewAttributeDefinitionBo attributeDefinition

    public void setAttributeDefinition(KewAttributeDefinitionBo attrDef) {
        if (attrDef != null) {
            this.attributeDefinitionId = attrDef.getId();
        } else {
            this.attributeDefinitionId = null;
        }
        this.attributeDefinition = attrDef;
    }

    public static PeopleFlowAttributeBo from(KewAttributeDefinition attributeDefinition, String id, String peopleFlowId, String value) {
        if (attributeDefinition == null) {
            return null;
        }
        PeopleFlowAttributeBo peopleFlowAttributeBo = new PeopleFlowAttributeBo();
        peopleFlowAttributeBo.setId(id);
        peopleFlowAttributeBo.setPeopleFlowId(peopleFlowId);
        peopleFlowAttributeBo.setValue(value);
        peopleFlowAttributeBo.setAttributeDefinition(KewAttributeDefinitionBo.from(attributeDefinition));
        peopleFlowAttributeBo.setAttributeDefinitionId(attributeDefinition.getId());
        return peopleFlowAttributeBo;
    }
    
}
