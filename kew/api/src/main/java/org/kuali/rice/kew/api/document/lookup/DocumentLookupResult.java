package org.kuali.rice.kew.api.document.lookup;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentLookupResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResult.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResult.Elements.RESULT_DATA,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResult
    extends AbstractDataTransferObject
    implements DocumentLookupResultContract
{

    @XmlElement(name = Elements.RESULT_DATA, required = false)
    private final List resultData;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private DocumentLookupResult() {
        this.resultData = null;
    }

    private DocumentLookupResult(Builder builder) {
        this.resultData = builder.getResultData();
    }

    @Override
    public List getResultData() {
        return this.resultData;
    }


    /**
     * A builder which can be used to construct {@link DocumentLookupResult} instances.  Enforces the constraints of the {@link DocumentLookupResultContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, DocumentLookupResultContract
    {

        private List resultData;

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
            return builder;
        }

        public DocumentLookupResult build() {
            return new DocumentLookupResult(this);
        }

        @Override
        public List getResultData() {
            return this.resultData;
        }

        public void setResultData(List resultData) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.resultData = resultData;
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

    }

}
