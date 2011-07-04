package org.kuali.rice.kew.api.doctype;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentType.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentType.Constants.TYPE_NAME, propOrder = {
        DocumentType.Elements.ID,
        DocumentType.Elements.NAME,
        DocumentType.Elements.DOCUMENT_TYPE_VERSION,
        DocumentType.Elements.LABEL,
        DocumentType.Elements.DESCRIPTION,
        DocumentType.Elements.PARENT_ID,
        DocumentType.Elements.ACTIVE,
        DocumentType.Elements.DOC_HANDLER_URL,
        DocumentType.Elements.POST_PROCESSOR_NAME,
        DocumentType.Elements.APPLICATION_ID,
        DocumentType.Elements.CURRENT,
        DocumentType.Elements.BLANKET_APPROVE_GROUP_ID,
        DocumentType.Elements.SUPER_USER_GROUP_ID,
        DocumentType.Elements.POLICIES,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentType implements ModelObjectComplete, DocumentTypeContract {

    private static final long serialVersionUID = 6866926296038814812L;

    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Elements.DOCUMENT_TYPE_VERSION, required = false)
    private final Integer documentTypeVersion;

    @XmlElement(name = Elements.LABEL, required = false)
    private final String label;

    @XmlElement(name = Elements.DESCRIPTION, required = false)
    private final String description;

    @XmlElement(name = Elements.PARENT_ID, required = false)
    private final String parentId;

    @XmlElement(name = Elements.ACTIVE, required = true)
    private final boolean active;

    @XmlElement(name = Elements.DOC_HANDLER_URL, required = false)
    private final String docHandlerUrl;

    @XmlElement(name = Elements.POST_PROCESSOR_NAME, required = false)
    private final String postProcessorName;

    @XmlElement(name = Elements.APPLICATION_ID, required = false)
    private final String applicationId;

    @XmlElement(name = Elements.CURRENT, required = true)
    private final boolean current;

    @XmlElement(name = Elements.BLANKET_APPROVE_GROUP_ID, required = false)
    private final String blanketApproveGroupId;

    @XmlElement(name = Elements.SUPER_USER_GROUP_ID, required = false)
    private final String superUserGroupId;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = Elements.POLICIES, required = false)
    @XmlJavaTypeAdapter(DocumentTypePolicyMapAdapter.class)
    private final Map<DocumentTypePolicy, String> policies;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentType() {
        this.id = null;
        this.name = null;
        this.documentTypeVersion = null;
        this.label = null;
        this.description = null;
        this.parentId = null;
        this.active = false;
        this.docHandlerUrl = null;
        this.postProcessorName = null;
        this.applicationId = null;
        this.current = false;
        this.blanketApproveGroupId = null;
        this.superUserGroupId = null;
        this.policies = null;
        this.versionNumber = null;
    }

    private DocumentType(Builder builder) {
        this.name = builder.getName();
        this.id = builder.getId();
        this.documentTypeVersion = builder.getDocumentTypeVersion();
        this.label = builder.getLabel();
        this.description = builder.getDescription();
        this.parentId = builder.getParentId();
        this.active = builder.isActive();
        this.docHandlerUrl = builder.getDocHandlerUrl();
        this.postProcessorName = builder.getPostProcessorName();
        this.applicationId = builder.getApplicationId();
        this.current = builder.isCurrent();
        this.blanketApproveGroupId = builder.getBlanketApproveGroupId();
        this.superUserGroupId = builder.getSuperUserGroupId();
        this.policies = new HashMap<DocumentTypePolicy, String>(builder.getPolicies());
        this.versionNumber = builder.getVersionNumber();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getDocumentTypeVersion() {
        return this.documentTypeVersion;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String getDocHandlerUrl() {
        return this.docHandlerUrl;
    }

    @Override
    public String getPostProcessorName() {
        return this.postProcessorName;
    }

    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    @Override
    public boolean isCurrent() {
        return this.current;
    }

    @Override
    public String getBlanketApproveGroupId() {
        return this.blanketApproveGroupId;
    }

    @Override
    public String getSuperUserGroupId() {
        return this.superUserGroupId;
    }

    @Override
    public Map<DocumentTypePolicy, String> getPolicies() {
        return this.policies;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * A builder which can be used to construct {@link DocumentType} instances. Enforces the
     * constraints of the {@link DocumentTypeContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentTypeContract {

        private static final long serialVersionUID = 1678979180435181578L;
        
        private String id;
        private String name;
        private Integer documentTypeVersion;
        private String label;
        private String description;
        private String parentId;
        private boolean active;
        private String docHandlerUrl;
        private String postProcessorName;
        private String applicationId;
        private boolean current;
        private String blanketApproveGroupId;
        private String superUserGroupId;
        private Map<DocumentTypePolicy, String> policies;
        private Long versionNumber;

        private Builder(String name) {
            setName(name);
            setActive(true);
            setCurrent(true);
            this.policies = new HashMap<DocumentTypePolicy, String>();
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(DocumentTypeContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getName());
            builder.setId(contract.getId());
            builder.setDocumentTypeVersion(contract.getDocumentTypeVersion());
            builder.setLabel(contract.getLabel());
            builder.setDescription(contract.getDescription());
            builder.setParentId(contract.getParentId());
            builder.setActive(contract.isActive());
            builder.setDocHandlerUrl(contract.getDocHandlerUrl());
            builder.setPostProcessorName(contract.getPostProcessorName());
            builder.setApplicationId(contract.getApplicationId());
            builder.setCurrent(contract.isCurrent());
            builder.setBlanketApproveGroupId(contract.getBlanketApproveGroupId());
            builder.setSuperUserGroupId(contract.getSuperUserGroupId());
            builder.setPolicies(new HashMap<DocumentTypePolicy, String>(contract.getPolicies()));
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public DocumentType build() {
            return new DocumentType(this);
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Integer getDocumentTypeVersion() {
            return this.documentTypeVersion;
        }

        @Override
        public String getLabel() {
            return this.label;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public String getParentId() {
            return this.parentId;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        @Override
        public String getDocHandlerUrl() {
            return this.docHandlerUrl;
        }

        @Override
        public String getPostProcessorName() {
            return this.postProcessorName;
        }

        @Override
        public String getApplicationId() {
            return this.applicationId;
        }

        @Override
        public boolean isCurrent() {
            return this.current;
        }

        @Override
        public String getBlanketApproveGroupId() {
            return this.blanketApproveGroupId;
        }

        @Override
        public String getSuperUserGroupId() {
            return this.superUserGroupId;
        }

        @Override
        public Map<DocumentTypePolicy, String> getPolicies() {
            return this.policies;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name was null or blank");
            }
            this.name = name;
        }

        public void setDocumentTypeVersion(Integer documentTypeVersion) {
            this.documentTypeVersion = documentTypeVersion;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setDocHandlerUrl(String docHandlerUrl) {
            this.docHandlerUrl = docHandlerUrl;
        }

        public void setPostProcessorName(String postProcessorName) {
            this.postProcessorName = postProcessorName;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public void setBlanketApproveGroupId(String blanketApproveGroupId) {
            this.blanketApproveGroupId = blanketApproveGroupId;
        }

        public void setSuperUserGroupId(String superUserGroupId) {
            this.superUserGroupId = superUserGroupId;
        }

        public void setPolicies(Map<DocumentTypePolicy, String> policies) {
            this.policies = policies;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentType";
        final static String TYPE_NAME = "DocumentTypeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[]{CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this
     * object is marshalled to XML.
     */
    static class Elements {
        final static String ID = "id";
        final static String NAME = "name";
        final static String DOCUMENT_TYPE_VERSION = "documentTypeVersion";
        final static String LABEL = "label";
        final static String DESCRIPTION = "description";
        final static String PARENT_ID = "parentId";
        final static String ACTIVE = "active";
        final static String DOC_HANDLER_URL = "docHandlerUrl";
        final static String POST_PROCESSOR_NAME = "postProcessorName";
        final static String APPLICATION_ID = "applicationId";
        final static String CURRENT = "current";
        final static String BLANKET_APPROVE_GROUP_ID = "blanketApproveGroupId";
        final static String SUPER_USER_GROUP_ID = "superUserGroupId";
        final static String POLICIES = "policies";
    }

}
