package org.kuali.rice.kew.api.document.lookup;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = DocumentLookupResults.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResults.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResults.Elements.LOOKUP_RESULTS,
    DocumentLookupResults.Elements.CRITERIA,
    DocumentLookupResults.Elements.CRITERIA_MODIFIED,
    DocumentLookupResults.Elements.OVER_THRESHOLD,
    DocumentLookupResults.Elements.NUMBER_OF_SECURITY_FILTERED_RESULTS,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResults extends AbstractDataTransferObject implements DocumentLookupResultsContract {

    @XmlElementWrapper(name = Elements.LOOKUP_RESULTS, required = true)
    @XmlElement(name = Elements.LOOKUP_RESULT, required = false)
    private final List<DocumentLookupResult> lookupResults;

    @XmlElement(name = Elements.CRITERIA, required = true)
    private final DocumentLookupCriteria criteria;

    @XmlElement(name = Elements.CRITERIA_MODIFIED, required = true)
    private final boolean criteriaModified;

    @XmlElement(name = Elements.OVER_THRESHOLD, required = true)
    private final boolean overThreshold;

    @XmlElement(name = Elements.NUMBER_OF_SECURITY_FILTERED_RESULTS, required = true)
    private final int numberOfSecurityFilteredResults;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @SuppressWarnings("unused")
    private DocumentLookupResults() {
        this.lookupResults = null;
        this.criteria = null;
        this.criteriaModified = false;
        this.overThreshold = false;
        this.numberOfSecurityFilteredResults = 0;
    }

    private DocumentLookupResults(Builder builder) {
        this.lookupResults = ModelObjectUtils.buildImmutableCopy(builder.getLookupResults());
        this.criteria = builder.getCriteria().build();
        this.criteriaModified = builder.isCriteriaModified();
        this.overThreshold = builder.isOverThreshold();
        this.numberOfSecurityFilteredResults = builder.getNumberOfSecurityFilteredResults();
    }

    @Override
    public List<DocumentLookupResult> getLookupResults() {
        return this.lookupResults;
    }

    @Override
    public DocumentLookupCriteria getCriteria() {
        return this.criteria;
    }

    @Override
    public boolean isCriteriaModified() {
        return this.criteriaModified;
    }

    @Override
    public boolean isOverThreshold() {
        return this.overThreshold;
    }

    @Override
    public int getNumberOfSecurityFilteredResults() {
        return this.numberOfSecurityFilteredResults;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupResults} instances.  Enforces the constraints of
     * the {@link DocumentLookupResultsContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupResultsContract {

        private List<DocumentLookupResult.Builder> lookupResults;
        private DocumentLookupCriteria.Builder criteria;
        private boolean criteriaModified;
        private boolean overThreshold;
        private int numberOfSecurityFilteredResults;

        private Builder(DocumentLookupCriteria.Builder criteria) {
            setLookupResults(new ArrayList<DocumentLookupResult.Builder>());
            setCriteria(criteria);
            setCriteriaModified(false);
            setOverThreshold(false);
            setNumberOfSecurityFilteredResults(0);

        }

        public static Builder create(DocumentLookupCriteria.Builder criteria) {
            return new Builder(criteria);
        }

        public static Builder create(DocumentLookupResultsContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(DocumentLookupCriteria.Builder.create(contract.getCriteria()));
            if (!CollectionUtils.isEmpty(contract.getLookupResults())) {
                for (DocumentLookupResultContract lookupResultContract : contract.getLookupResults()) {
                    builder.getLookupResults().add(DocumentLookupResult.Builder.create(lookupResultContract));
                }
            }
            builder.setCriteriaModified(contract.isCriteriaModified());
            builder.setOverThreshold(contract.isOverThreshold());
            builder.setNumberOfSecurityFilteredResults(contract.getNumberOfSecurityFilteredResults());
            return builder;
        }

        public DocumentLookupResults build() {
            return new DocumentLookupResults(this);
        }

        @Override
        public List<DocumentLookupResult.Builder> getLookupResults() {
            return this.lookupResults;
        }

        @Override
        public DocumentLookupCriteria.Builder getCriteria() {
            return this.criteria;
        }

        @Override
        public boolean isCriteriaModified() {
            return this.criteriaModified;
        }

        @Override
        public boolean isOverThreshold() {
            return this.overThreshold;
        }

        @Override
        public int getNumberOfSecurityFilteredResults() {
            return this.numberOfSecurityFilteredResults;
        }

        public void setLookupResults(List<DocumentLookupResult.Builder> lookupResults) {
            this.lookupResults = lookupResults;
        }

        public void setCriteria(DocumentLookupCriteria.Builder criteria) {
            if (criteria == null) {
                throw new IllegalArgumentException("criteria was null");
            }
            this.criteria = criteria;
        }

        public void setCriteriaModified(boolean criteriaModified) {
            this.criteriaModified = criteriaModified;
        }

        public void setOverThreshold(boolean overThreshold) {
            this.overThreshold = overThreshold;
        }

        public void setNumberOfSecurityFilteredResults(int numberOfSecurityFilteredResults) {
            this.numberOfSecurityFilteredResults = numberOfSecurityFilteredResults;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupResults";
        final static String TYPE_NAME = "DocumentLookupResultsType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String LOOKUP_RESULTS = "lookupResults";
        final static String LOOKUP_RESULT = "lookupResult";
        final static String CRITERIA = "criteria";
        final static String CRITERIA_MODIFIED = "criteriaModified";
        final static String OVER_THRESHOLD = "overThreshold";
        final static String NUMBER_OF_SECURITY_FILTERED_RESULTS = "numberOfSecurityFilteredResults";
    }

}
