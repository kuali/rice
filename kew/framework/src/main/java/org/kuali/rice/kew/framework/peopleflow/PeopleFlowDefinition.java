package org.kuali.rice.kew.framework.peopleflow;

import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model object for a PeopleFlow.  TODO: ...
 */
// TODO: JAX-WS annotate
public class PeopleFlowDefinition extends AbstractDataTransferObject implements PeopleFlowContract {

    private final String id;
    private final String name;
    private final String namespace;
    private final String typeId;
    private final String description;
    private final List<PeopleFlowMemberDefinition> members;
    private final Map<String,String> attributes;
    private final Long versionNumber;
    private final boolean active;

    private PeopleFlowDefinition() {
        id = null;
        name = null;
        namespace = null;
        typeId = null;
        description = null;
        members = null;
        attributes = null;
        versionNumber = null;
        active = true;
    }

    private PeopleFlowDefinition(Builder builder) {
        id = builder.getId();
        name = builder.getName();
        namespace = builder.getNamespace();
        typeId = builder.getTypeId();
        description = builder.getDescription();
        members = ModelObjectUtils.buildImmutableCopy(builder.getMembers());
        attributes = Collections.unmodifiableMap(new HashMap<String, String>(builder.getAttributes()));
        versionNumber = builder.getVersionNumber();
        active = builder.isActive();
    }


    // TODO: validate constraints in builder
    public static class Builder implements ModelBuilder, PeopleFlowContract {

        private Builder(String name, String namespace, String typeId) {
            this.name = name;
            this.namespace = namespace;
            this.typeId = typeId;
            active = true;
        }

        public static Builder create(PeopleFlowContract peopleFlow) {
            Builder builder = new Builder(peopleFlow.getName(), peopleFlow.getNamespace(), peopleFlow.getTypeId());

            builder.setId(peopleFlow.getId());
            builder.setDescription(peopleFlow.getDescription());
            builder.setMembers(ModelObjectUtils.transform(peopleFlow.getMembers(), PeopleFlowMemberDefinition.Builder.toBuilder));
            builder.setAttributes(new HashMap<String, String>(peopleFlow.getAttributes()));
            builder.setVersionNumber(peopleFlow.getVersionNumber());
            builder.setActive(peopleFlow.isActive());

            return builder;
        }

        public static Builder create(String name, String namespace, String typeId) {
            return new Builder(name, namespace, typeId);
        }

        private String id;
        private String name;
        private String namespace;
        private String typeId;
        private String description;
        private List<PeopleFlowMemberDefinition.Builder> members;
        private Map<String,String> attributes;
        private Long versionNumber;
        private boolean active;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<PeopleFlowMemberDefinition.Builder> getMembers() {
            return members;
        }

        public void setMembers(List<PeopleFlowMemberDefinition.Builder> members) {
            this.members = members;
        }

        public Map<String, String> getAttributes() {
            if (attributes == null) { attributes = new HashMap<String, String>(); }
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public Object build() {
            return new PeopleFlowDefinition(this);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getTypeId() {
        return typeId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PeopleFlowMemberDefinition> getMembers() {
        return members;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }

    @Override
    public boolean isActive() {
        return active;
    }

}
