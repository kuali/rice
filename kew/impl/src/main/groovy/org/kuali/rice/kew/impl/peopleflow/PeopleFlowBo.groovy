package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.bo.MutableInactivatable
import org.kuali.rice.kew.api.peopleflow.PeopleFlowContract
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberDefinition
import org.kuali.rice.kew.api.KewApiServiceLocator
import org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException
import org.kuali.rice.core.api.exception.RiceIllegalStateException
import org.apache.commons.collections.CollectionUtils
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition

/**
 * Mapped entity for PeopleFlows
 */
class PeopleFlowBo extends PersistableBusinessObjectBase implements MutableInactivatable, PeopleFlowContract {

    def String id
    def String name
    def String namespaceCode
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

    public static PeopleFlowBo from(PeopleFlowContract peopleFlow, KewTypeDefinition kewTypeDefinition) {
        PeopleFlowBo result = new PeopleFlowBo();

        result.id = peopleFlow.getId();
        result.name = peopleFlow.getName();
        result.namespaceCode = peopleFlow.getNamespaceCode();
        result.typeId = peopleFlow.getTypeId();
        result.description = peopleFlow.getDescription();
        result.active = peopleFlow.isActive();
        result.versionNumber = peopleFlow.getVersionNumber();

        // we need to translate attributes over, this is a bit more work, first let's do some validation
        if (peopleFlow.getTypeId() == null) {
            if (!peopleFlow.getAttributes().isEmpty()) {
                throw new RiceIllegalArgumentException("Given PeopleFlow definition does not have a type, but does have attribute values");
            }
            if (kewTypeDefinition != null) {
                throw new RiceIllegalArgumentException("PeopleFlow has no type id, but a KewTypeDefinition was supplied when it should not have been.");
            }
        }
        if (peopleFlow.getTypeId() != null) {
            if (kewTypeDefinition == null) {
                throw new RiceIllegalArgumentException("PeopleFlow has a type id of '" + peopleFlow.getTypeId() + "' but no KewTypeDefinition was supplied.");
            }
            if (!kewTypeDefinition.getId().equals(peopleFlow.getTypeId())) {
                throw new RiceIllegalArgumentException("Type id of given KewTypeDefinition does not match PeopleFlow type id:  " + kewTypeDefinition.getId() + " != " + peopleFlow.getTypeId());
            }
        }
        result.attributeBos = new ArrayList<PeopleFlowAttributeBo>();
        peopleFlow.getAttributes().each { key, value ->
            KewAttributeDefinition attributeDefinition = kewTypeDefinition.getAttributeDefinitionByName(key);
            if (attributeDefinition == null) {
                throw new RiceIllegalArgumentException("There is no attribute definition for the given attribute name '" + key + "'");
            }
            // they have no way to pass us the id of the attribute from the given contract
            result.attributeBos.add(PeopleFlowAttributeBo.from(attributeDefinition, null, peopleFlow.getId(), value));
        }

        // now translate the members
        result.members = new ArrayList<PeopleFlowMemberBo>();
        for (PeopleFlowMemberDefinition member : peopleFlow.getMembers()) {
            result.members.add(PeopleFlowMemberBo.from(member));
        }

        return result;
    }

    public static PeopleFlowDefinition to(PeopleFlowBo peopleFlowBo) {
        if (peopleFlowBo == null) {
            return null;
        }
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.create(peopleFlowBo);
        return builder.build();
    }
}
