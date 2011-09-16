package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.bo.MutableInactivatable
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowContract
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowDefinition
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowMemberDefinition

/**
 * Mapped entity for PeopleFlows
 */
class PeopleFlowBo extends PersistableBusinessObjectBase implements MutableInactivatable, PeopleFlowContract {

    def String id
    def String name
    def String namespace
    def String typeId
    def String description
    def boolean active = true

    def List<PeopleFlowAttributeBo> attributeBos = new ArrayList<PeopleFlowAttributeBo>();
    def List<PeopleFlowMemberBo> members = new ArrayList<PeopleFlowMemberBo>();

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> results = new HashMap<String, String>();

        if (attributeBos != null) for (PeopleFlowAttributeBo attr : attributeBos) {
            results.put(attr.attributeDefinition.name, attr.value);
        }

        return results;
    }

    public static PeopleFlowBo from(PeopleFlowDefinition peopleFlow) {
        PeopleFlowBo result = new PeopleFlowBo();

        result.id = peopleFlow.getId();
        result.name = peopleFlow.getName();
        result.namespace = peopleFlow.getNamespace();
        result.typeId = peopleFlow.getTypeId();
        result.description = peopleFlow.getDescription();
        result.active = peopleFlow.isActive();
        result.versionNumber = peopleFlow.getVersionNumber();

        result.attributeBos = null;  // TODO: Convert map to PeopleFlowAttributeBo list

        result.members = new ArrayList<PeopleFlowMemberBo>();
        for (PeopleFlowMemberDefinition member : peopleFlow.getMembers()) {
            result.members.add(PeopleFlowMemberBo.from(member));
        }

        return result;
    }
}
