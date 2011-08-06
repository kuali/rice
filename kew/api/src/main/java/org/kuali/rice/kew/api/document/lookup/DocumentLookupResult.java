package org.kuali.rice.kew.api.document.lookup;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentLookupResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResult.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResult.Elements.RESULT_DATA,
    DocumentLookupResult.Elements.DOCUMENT_ID,
    DocumentLookupResult.Elements.STATUS,
    DocumentLookupResult.Elements.DATE_CREATED,
    DocumentLookupResult.Elements.DATE_LAST_MODIFIED,
    DocumentLookupResult.Elements.DATE_APPROVED,
    DocumentLookupResult.Elements.DATE_FINALIZED,
    DocumentLookupResult.Elements.TITLE,
    DocumentLookupResult.Elements.APPLICATION_DOCUMENT_ID,
    DocumentLookupResult.Elements.INITIATOR_PRINCIPAL_ID,
    DocumentLookupResult.Elements.ROUTED_BY_PRINCIPAL_ID,
    DocumentLookupResult.Elements.DOCUMENT_TYPE_NAME,
    DocumentLookupResult.Elements.DOCUMENT_TYPE_ID,
    DocumentLookupResult.Elements.DOCUMENT_HANDLER_URL,
    DocumentLookupResult.Elements.APPLICATION_DOCUMENT_STATUS,
    DocumentLookupResult.Elements.APPLICATION_DOCUMENT_STATUS_DATE,
    DocumentLookupResult.Elements.VARIABLES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResult
    extends AbstractDataTransferObject
    implements DocumentLookupResultContract
{

    @XmlElement(name = Elements.RESULT_DATA, required = false)
    private final List resultData;
    @XmlElement(name = Elements.DOCUMENT_ID, required = false)
    private final String documentId;
    @XmlElement(name = Elements.STATUS, required = false)
    private final DocumentStatus status;
    @XmlElement(name = Elements.DATE_CREATED, required = false)
    private final DateTime dateCreated;
    @XmlElement(name = Elements.DATE_LAST_MODIFIED, required = false)
    private final DateTime dateLastModified;
    @XmlElement(name = Elements.DATE_APPROVED, required = false)
    private final DateTime dateApproved;
    @XmlElement(name = Elements.DATE_FINALIZED, required = false)
    private final DateTime dateFinalized;
    @XmlElement(name = Elements.TITLE, required = false)
    private final String title;
    @XmlElement(name = Elements.APPLICATION_DOCUMENT_ID, required = false)
    private final String applicationDocumentId;
    @XmlElement(name = Elements.INITIATOR_PRINCIPAL_ID, required = false)
    private final String initiatorPrincipalId;
    @XmlElement(name = Elements.ROUTED_BY_PRINCIPAL_ID, required = false)
    private final String routedByPrincipalId;
    @XmlElement(name = Elements.DOCUMENT_TYPE_NAME, required = false)
    private final String documentTypeName;
    @XmlElement(name = Elements.DOCUMENT_TYPE_ID, required = false)
    private final String documentTypeId;
    @XmlElement(name = Elements.DOCUMENT_HANDLER_URL, required = false)
    private final String documentHandlerUrl;
    @XmlElement(name = Elements.APPLICATION_DOCUMENT_STATUS, required = false)
    private final String applicationDocumentStatus;
    @XmlElement(name = Elements.APPLICATION_DOCUMENT_STATUS_DATE, required = false)
    private final DateTime applicationDocumentStatusDate;
    @XmlElement(name = Elements.VARIABLES, required = false)
    private final Map variables;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private DocumentLookupResult() {
        this.resultData = null;
        this.documentId = null;
        this.status = null;
        this.dateCreated = null;
        this.dateLastModified = null;
        this.dateApproved = null;
        this.dateFinalized = null;
        this.title = null;
        this.applicationDocumentId = null;
        this.initiatorPrincipalId = null;
        this.routedByPrincipalId = null;
        this.documentTypeName = null;
        this.documentTypeId = null;
        this.documentHandlerUrl = null;
        this.applicationDocumentStatus = null;
        this.applicationDocumentStatusDate = null;
        this.variables = null;
    }

    private DocumentLookupResult(Builder builder) {
        this.resultData = builder.getResultData();
        this.documentId = builder.getDocumentId();
        this.status = builder.getStatus();
        this.dateCreated = builder.getDateCreated();
        this.dateLastModified = builder.getDateLastModified();
        this.dateApproved = builder.getDateApproved();
        this.dateFinalized = builder.getDateFinalized();
        this.title = builder.getTitle();
        this.applicationDocumentId = builder.getApplicationDocumentId();
        this.initiatorPrincipalId = builder.getInitiatorPrincipalId();
        this.routedByPrincipalId = builder.getRoutedByPrincipalId();
        this.documentTypeName = builder.getDocumentTypeName();
        this.documentTypeId = builder.getDocumentTypeId();
        this.documentHandlerUrl = builder.getDocumentHandlerUrl();
        this.applicationDocumentStatus = builder.getApplicationDocumentStatus();
        this.applicationDocumentStatusDate = builder.getApplicationDocumentStatusDate();
        this.variables = builder.getVariables();
    }

    @Override
    public List getResultData() {
        return this.resultData;
    }

    @Override
    public String getDocumentId() {
        return this.documentId;
    }

    @Override
    public DocumentStatus getStatus() {
        return this.status;
    }

    @Override
    public DateTime getDateCreated() {
        return this.dateCreated;
    }

    @Override
    public DateTime getDateLastModified() {
        return this.dateLastModified;
    }

    @Override
    public DateTime getDateApproved() {
        return this.dateApproved;
    }

    @Override
    public DateTime getDateFinalized() {
        return this.dateFinalized;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getApplicationDocumentId() {
        return this.applicationDocumentId;
    }

    @Override
    public String getInitiatorPrincipalId() {
        return this.initiatorPrincipalId;
    }

    @Override
    public String getRoutedByPrincipalId() {
        return this.routedByPrincipalId;
    }

    @Override
    public String getDocumentTypeName() {
        return this.documentTypeName;
    }

    @Override
    public String getDocumentTypeId() {
        return this.documentTypeId;
    }

    @Override
    public String getDocumentHandlerUrl() {
        return this.documentHandlerUrl;
    }

    @Override
    public String getApplicationDocumentStatus() {
        return this.applicationDocumentStatus;
    }

    @Override
    public DateTime getApplicationDocumentStatusDate() {
        return this.applicationDocumentStatusDate;
    }

    @Override
    public Map getVariables() {
        return this.variables;
    }


    /**
     * A builder which can be used to construct {@link DocumentLookupResult} instances.  Enforces the constraints of the {@link DocumentLookupResultContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, DocumentLookupResultContract
    {

        private List resultData;
        private String documentId;
        private DocumentStatus status;
        private DateTime dateCreated;
        private DateTime dateLastModified;
        private DateTime dateApproved;
        private DateTime dateFinalized;
        private String title;
        private String applicationDocumentId;
        private String initiatorPrincipalId;
        private String routedByPrincipalId;
        private String documentTypeName;
        private String documentTypeId;
        private String documentHandlerUrl;
        private String applicationDocumentStatus;
        private DateTime applicationDocumentStatusDate;
        private Map variables;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(DocumentLookupResultContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setResultData(contract.getResultData());
            builder.setDocumentId(contract.getDocumentId());
            builder.setStatus(contract.getStatus());
            builder.setDateCreated(contract.getDateCreated());
            builder.setDateLastModified(contract.getDateLastModified());
            builder.setDateApproved(contract.getDateApproved());
            builder.setDateFinalized(contract.getDateFinalized());
            builder.setTitle(contract.getTitle());
            builder.setApplicationDocumentId(contract.getApplicationDocumentId());
            builder.setInitiatorPrincipalId(contract.getInitiatorPrincipalId());
            builder.setRoutedByPrincipalId(contract.getRoutedByPrincipalId());
            builder.setDocumentTypeName(contract.getDocumentTypeName());
            builder.setDocumentTypeId(contract.getDocumentTypeId());
            builder.setDocumentHandlerUrl(contract.getDocumentHandlerUrl());
            builder.setApplicationDocumentStatus(contract.getApplicationDocumentStatus());
            builder.setApplicationDocumentStatusDate(contract.getApplicationDocumentStatusDate());
            builder.setVariables(contract.getVariables());
            return builder;
        }

        public DocumentLookupResult build() {
            return new DocumentLookupResult(this);
        }

        @Override
        public List getResultData() {
            return this.resultData;
        }

        @Override
        public String getDocumentId() {
            return this.documentId;
        }

        @Override
        public DocumentStatus getStatus() {
            return this.status;
        }

        @Override
        public DateTime getDateCreated() {
            return this.dateCreated;
        }

        @Override
        public DateTime getDateLastModified() {
            return this.dateLastModified;
        }

        @Override
        public DateTime getDateApproved() {
            return this.dateApproved;
        }

        @Override
        public DateTime getDateFinalized() {
            return this.dateFinalized;
        }

        @Override
        public String getTitle() {
            return this.title;
        }

        @Override
        public String getApplicationDocumentId() {
            return this.applicationDocumentId;
        }

        @Override
        public String getInitiatorPrincipalId() {
            return this.initiatorPrincipalId;
        }

        @Override
        public String getRoutedByPrincipalId() {
            return this.routedByPrincipalId;
        }

        @Override
        public String getDocumentTypeName() {
            return this.documentTypeName;
        }

        @Override
        public String getDocumentTypeId() {
            return this.documentTypeId;
        }

        @Override
        public String getDocumentHandlerUrl() {
            return this.documentHandlerUrl;
        }

        @Override
        public String getApplicationDocumentStatus() {
            return this.applicationDocumentStatus;
        }

        @Override
        public DateTime getApplicationDocumentStatusDate() {
            return this.applicationDocumentStatusDate;
        }

        @Override
        public Map getVariables() {
            return this.variables;
        }

        public void setResultData(List resultData) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.resultData = resultData;
        }

        public void setDocumentId(String documentId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.documentId = documentId;
        }

        public void setStatus(DocumentStatus status) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.status = status;
        }

        public void setDateCreated(DateTime dateCreated) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.dateCreated = dateCreated;
        }

        public void setDateLastModified(DateTime dateLastModified) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.dateLastModified = dateLastModified;
        }

        public void setDateApproved(DateTime dateApproved) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.dateApproved = dateApproved;
        }

        public void setDateFinalized(DateTime dateFinalized) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.dateFinalized = dateFinalized;
        }

        public void setTitle(String title) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.title = title;
        }

        public void setApplicationDocumentId(String applicationDocumentId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.applicationDocumentId = applicationDocumentId;
        }

        public void setInitiatorPrincipalId(String initiatorPrincipalId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.initiatorPrincipalId = initiatorPrincipalId;
        }

        public void setRoutedByPrincipalId(String routedByPrincipalId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.routedByPrincipalId = routedByPrincipalId;
        }

        public void setDocumentTypeName(String documentTypeName) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.documentTypeName = documentTypeName;
        }

        public void setDocumentTypeId(String documentTypeId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.documentTypeId = documentTypeId;
        }

        public void setDocumentHandlerUrl(String documentHandlerUrl) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.documentHandlerUrl = documentHandlerUrl;
        }

        public void setApplicationDocumentStatus(String applicationDocumentStatus) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.applicationDocumentStatus = applicationDocumentStatus;
        }

        public void setApplicationDocumentStatusDate(DateTime applicationDocumentStatusDate) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.applicationDocumentStatusDate = applicationDocumentStatusDate;
        }

        public void setVariables(Map variables) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.variables = variables;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "documentLookupResult";
        final static String TYPE_NAME = "DocumentLookupResultType";

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String RESULT_DATA = "resultData";
        final static String DOCUMENT_ID = "documentId";
        final static String STATUS = "status";
        final static String DATE_CREATED = "dateCreated";
        final static String DATE_LAST_MODIFIED = "dateLastModified";
        final static String DATE_APPROVED = "dateApproved";
        final static String DATE_FINALIZED = "dateFinalized";
        final static String TITLE = "title";
        final static String APPLICATION_DOCUMENT_ID = "applicationDocumentId";
        final static String INITIATOR_PRINCIPAL_ID = "initiatorPrincipalId";
        final static String ROUTED_BY_PRINCIPAL_ID = "routedByPrincipalId";
        final static String DOCUMENT_TYPE_NAME = "documentTypeName";
        final static String DOCUMENT_TYPE_ID = "documentTypeId";
        final static String DOCUMENT_HANDLER_URL = "documentHandlerUrl";
        final static String APPLICATION_DOCUMENT_STATUS = "applicationDocumentStatus";
        final static String APPLICATION_DOCUMENT_STATUS_DATE = "applicationDocumentStatusDate";
        final static String VARIABLES = "variables";

    }

}
