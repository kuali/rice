package org.kuali.rice.kew.api.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = PeopleFlow.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PeopleFlow.Constants.TYPE_NAME, propOrder = {
        PeopleFlow.Elements.ID,
        PeopleFlow.Elements.NAME,
        PeopleFlow.Elements.NAMESPACE,
        PeopleFlow.Elements.TYPE_ID,
        PeopleFlow.Elements.DESCRIPTION,
        PeopleFlow.Elements.MEMBERS,
        PeopleFlow.Elements.ATTRIBUTES,
        PeopleFlow.Elements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class PeopleFlow extends AbstractDataTransferObject implements PeopleFlowContract {

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Elements.ATTRIBUTES, required = false)
    @XmlJavaTypeAdapter(MapStringStringAdapter.class)
    private final Map<String, String> attributes;

    @XmlElement(name = Elements.NAMESPACE, required = true)
    private final String namespace;

    @XmlElement(name = Elements.TYPE_ID, required = false)
    private final String typeId;

    @XmlElement(name = Elements.DESCRIPTION, required = false)
    private final String description;

    @XmlElementWrapper(name = Elements.MEMBERS, required = false)
    @XmlElement(name = Elements.MEMBER, required = false)
    private final List<PeopleFlowMember> members;
    
    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private PeopleFlow() {
        this.name = null;
        this.attributes = null;
        this.namespace = null;
        this.typeId = null;
        this.description = null;
        this.members = null;
        this.id = null;
        this.active = false;
        this.versionNumber = null;
    }

    private PeopleFlow(Builder builder) {
        this.name = builder.getName();
        this.attributes = builder.getAttributes();
        this.namespace = builder.getNamespace();
        this.typeId = builder.getTypeId();
        this.description = builder.getDescription();
        this.members = ModelObjectUtils.buildImmutableCopy(builder.getMembers());
        this.id = builder.getId();
        this.active = builder.isActive();
        this.versionNumber = builder.getVersionNumber();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map getAttributes() {
        return this.attributes;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public String getTypeId() {
        return this.typeId;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List getMembers() {
        return this.members;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    /**
     * A builder which can be used to construct {@link PeopleFlow} instances.  Enforces the constraints of the
     * {@link PeopleFlowContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, PeopleFlowContract {

        private String name;
        private Map<String, String> attributes;
        private String namespace;
        private String typeId;
        private String description;
        private List<PeopleFlowMember.Builder> members;
        private String id;
        private boolean active;
        private Long versionNumber;

        private Builder(String namespace, String name) {
            setNamespace(namespace);
            setName(name);
            setActive(true);
            setAttributes(new HashMap<String, String>());
            setMembers(new ArrayList<PeopleFlowMember.Builder>());
        }

        public static Builder create(String namespace, String name) {
            return new Builder(namespace, name);
        }

        public static Builder create(PeopleFlowContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getNamespace(), contract.getName());
            if (contract.getAttributes() != null) {
                builder.getAttributes().putAll(contract.getAttributes());
            }
            builder.setTypeId(contract.getTypeId());
            builder.setDescription(contract.getDescription());
            if (contract.getMembers() != null) {
                for (PeopleFlowMemberContract member : contract.getMembers()) {
                    builder.getMembers().add(PeopleFlowMember.Builder.create(member));
                }
            }
            builder.setId(contract.getId());
            builder.setActive(contract.isActive());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public PeopleFlow build() {
            return new PeopleFlow(this);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Map getAttributes() {
            return this.attributes;
        }

        @Override
        public String getNamespace() {
            return this.namespace;
        }

        @Override
        public String getTypeId() {
            return this.typeId;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public List getMembers() {
            return this.members;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name was null or blank");
            }
            this.name = name;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        public void setNamespace(String namespace) {
            if (StringUtils.isBlank(namespace)) {
                throw new IllegalArgumentException("namespace was null or blank");
            }
            this.namespace = namespace;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMembers(List<PeopleFlowMember.Builder> members) {
            this.members = members;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "peopleFlow";
        final static String TYPE_NAME = "PeopleFlowType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String NAME = "name";
        final static String ATTRIBUTES = "attributes";
        final static String NAMESPACE = "namespace";
        final static String TYPE_ID = "typeId";
        final static String DESCRIPTION = "description";
        final static String MEMBERS = "members";
        final static String MEMBER = "member";
        final static String ID = "id";
        final static String ACTIVE = "active";
    }

}
