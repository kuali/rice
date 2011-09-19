package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.bo.MutableInactivatable
import org.kuali.rice.kew.api.peopleflow.PeopleFlowContract
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember

import org.kuali.rice.kew.api.repository.type.KewTypeDefinition
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException

import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberContract
import org.apache.commons.collections.CollectionUtils

/**
 * Mapped entity for PeopleFlows
 */
class PeopleFlowBo extends PersistableBusinessObjectBase implements MutableInactivatable, PeopleFlowContract {

    String id
    String name
    String namespaceCode
    String typeId
    String description
    boolean active = true

    List<PeopleFlowAttributeBo> attributeBos = new ArrayList<PeopleFlowAttributeBo>();
    List<PeopleFlowMemberBo> members = new ArrayList<PeopleFlowMemberBo>();

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> results = new HashMap<String, String>();

        if (attributeBos != null) for (PeopleFlowAttributeBo attr : attributeBos) {
            results.put(attr.attributeDefinition.name, attr.value);
        }

        return results;
    }

    public static PeopleFlowBo from(PeopleFlowContract peopleFlow, KewTypeDefinition kewTypeDefinition) {
        return PeopleFlowBo.fromAndUpdate(peopleFlow, kewTypeDefinition, null);
    }

    /**
     * Translates from the given PeopleFlowContract to a PeopleFlowBo, optionally updating the given "toUpdate" parameter
     * instead of creating a new PeopleFlowBo.  If it's not passed then a new PeopleFlowBo will be created.
     */
    public static PeopleFlowBo fromAndUpdate(PeopleFlowContract peopleFlow, KewTypeDefinition kewTypeDefinition, PeopleFlowBo toUpdate) {
        PeopleFlowBo result = toUpdate;
        if (toUpdate == null) {
            result = new PeopleFlowBo();
        }

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

        // now we need to effectively do a diff with the given attributes, first let's add new entries and update existing ones
        result.attributeBos = new ArrayList<PeopleFlowAttributeBo>();
        peopleFlow.getAttributes().each { key, value ->
            KewAttributeDefinition attributeDefinition = kewTypeDefinition.getAttributeDefinitionByName(key);
            if (attributeDefinition == null) {
                throw new RiceIllegalArgumentException("There is no attribute definition for the given attribute name '" + key + "'");
            }
            // they have no way to pass us the id of the attribute from the given contract
            result.attributeBos.add(PeopleFlowAttributeBo.from(attributeDefinition, null, peopleFlow.getId(), value));
        }

        handleMembersUpdate(result, peopleFlow);

        return result;
    }

    /**
     * Translate the members, if the members have changed at all, we want to clear so that the current set of members
     * are removed by OJB's removal aware list.
     */
    private static void handleMembersUpdate(PeopleFlowBo peopleFlowBo, PeopleFlowDefinition peopleFlow) {
        Set<PeopleFlowMember> currentMembers = new HashSet<PeopleFlowMember>();
        if (peopleFlowBo.getMembers() == null) {
            peopleFlowBo.setMembers(new ArrayList<PeopleFlowMemberBo>());
        }
        peopleFlowBo.getMembers().each {
            currentMembers.add(PeopleFlowMember.Builder.create(it).build());
        }
        if (!currentMembers.equals(new HashSet<PeopleFlowMember>(peopleFlow.getMembers()))) {
            // this means that the membership has been updated, we need to rebuild it
            peopleFlowBo.getMembers().clear();
            peopleFlow.getMembers().each {
                peopleFlowBo.getMembers().add(PeopleFlowMemberBo.from(it));
            }
        }
    }

    public static PeopleFlowDefinition to(PeopleFlowBo peopleFlowBo) {
        if (peopleFlowBo == null) {
            return null;
        }
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.create(peopleFlowBo);
        return builder.build();
    }
}
