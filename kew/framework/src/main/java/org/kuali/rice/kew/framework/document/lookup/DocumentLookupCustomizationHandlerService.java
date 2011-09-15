package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A remotable service which handles processing of a client application's document lookup customizations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.DOCUMENT_LOOKUP_CUSTOMIZATION_HANDLER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentLookupCustomizationHandlerService {

    /**
     * Retrieves the custom {@code DocumentLookupCriteriaConfiguration} to use for the document type with the given name
     * and for the given list of searchable attributes.  This method is invoked by the document lookup implementation in
     * order to help assemble the final criteria attribute fields (which includes configuration for all searchable
     * attributes on the document type).
     *
     * <p>The given list of searchable attribute names may not necessary include all searchable attribute on the
     * document type, only those which need to be handled by the client application hosting this service.  This
     * determination is made based on the applicationId which is associated with the searchable attribute.
     * Implementations of this method will assemble this information by invoking the
     * {@link org.kuali.rice.kew.framework.document.attribute.SearchableAttribute#getSearchFields(org.kuali.rice.kew.api.extension.ExtensionDefinition, String)}
     * methods on each of the requested searchable attributes.</p>
     *
     * @param documentTypeName the document type name for which to retrieve the configuration
     * @param searchableAttributeNames the names of the searchable attributes from which to assemble criteria
     * configuration which are owned by the application hosting this service
     *
     * @return the custom document lookup criteria configuration for the given searchable attribute, or null if no
     * custom configuration is needed
     * 
     * @throws RiceIllegalArgumentException if documentTypeName is a null or blank value
     */
    @WebMethod(operationName = "getDocumentLookupConfiguration")
	@WebResult(name = "documentLookupConfiguration")
	@XmlElement(name = "documentLookupConfiguration", required = false)
    DocumentLookupCriteriaConfiguration getDocumentLookupConfiguration(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames
    ) throws RiceIllegalArgumentException;

    /**
     * Executes validation of the given {@code DocumentLookupCriteria} against the searchable attributes with the given
     * names..  This method is invoked by the document lookup implementation in order to allow for validation to be
     * customized via custom searchable attribute implementations.
     *
     * <p>The given list of searchable attribute names may not necessary include all searchable attribute on the
     * document type, only those which need to be handled by the client application hosting this service.  This
     * determination is made based on the applicationId which is associated with the searchable attribute.
     * Implementations of this method execute this validationby invoking the
     * {@link org.kuali.rice.kew.framework.document.attribute.SearchableAttribute#validateDocumentAttributeCriteria(org.kuali.rice.kew.api.extension.ExtensionDefinition, org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria)}
     * methods on each of the requested searchable attributes.</p>
     *
     * @param documentLookupCriteria the criteria against which to perform the validation
     * @param searchableAttributeNames the names of the searchable attributes against which to execute validation which
     * are owned by the application hosting this service
     *
     * @return a list or remotable attribute errors in the case that any validation errors were raised by the
     * requested searchable attributes
     *
     * @throws RiceIllegalArgumentException if documentTypeName is a null or blank value
     */
    @WebMethod(operationName = "validateCriteria")
    @WebResult(name = "errors")
    @XmlElementWrapper(name = "errors", required = false)
    @XmlElement(name = "errors", required = false)
    List<RemotableAttributeError> validateCriteria(@WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames
    ) throws RiceIllegalArgumentException;

    /**
     * Executes criteria customization against the given criteria using the {@link DocumentLookupCustomizer} with the
     * given customizer name.  This name is the name of the {@code ExtensionDefinition} that defines the customizer
     * where the customizer extension's applicationId is the same as the application hosting this service.
     *
     * <p>This method effectively invokes the {@link DocumentLookupCustomizer#customizeCriteria(org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria)}
     * on the requested customizer which is owned by this application.
     *
     * @param documentLookupCriteria the criteria to customize
     * @param customizerName the name of the extension definition for the {@code DocumentLookupCustomizer} which should
     * be used in order to execute the customization
     *
     * @return the customized criteria, or null if no customization was performed
     *
     * @throws RiceIllegalArgumentException if documentLookupCriteria is null
     * @throws RiceIllegalArgumentException if customizerName is a null or blank value
     */
    @WebMethod(operationName = "customizeCriteria")
    @WebResult(name = "documentLookupCriteria")
    @XmlElement(name = "documentLookupCriteria", required = false)
    DocumentLookupCriteria customizeCriteria(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    /**
     * Executes custom criteria clearing against the given criteria using the {@link DocumentLookupCustomizer} with the
     * given customizer name.  This name is the name of the {@code ExtensionDefinition} that defines the customizer
     * where the customizer extension's applicationId is the same as the application hosting this service.
     *
     * <p>This method effectively invokes the {@link DocumentLookupCustomizer#customizeClearCriteria(org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria)}
     * on the requested customizer which is owned by this application.
     *
     * @param documentLookupCriteria the criteria on which to perform custom clearing
     * @param customizerName the name of the extension definition for the {@code DocumentLookupCustomizer} which should
     * be used in order to execute the customization
     *
     * @return the cleared criteria, or null if no custom clear was performed
     *
     * @throws RiceIllegalArgumentException if documentLookupCriteria is null
     * @throws RiceIllegalArgumentException if customizerName is a null or blank value
     */
    @WebMethod(operationName = "customizeClearCriteria")
    @WebResult(name = "documentLookupCriteria")
    @XmlElement(name = "documentLookupCriteria", required = false)
    DocumentLookupCriteria customizeClearCriteria(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    /**
     * Executes customization of document lookup results using the {@link DocumentLookupCustomizer} with the
     * given customizer name.  This name is the name of the {@code ExtensionDefinition} that defines the customizer
     * where the customizer extension's applicationId is the same as the application hosting this service.
     *
     * <p>This method effectively invokes the {@link DocumentLookupCustomizer#customizeResults(org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria, java.util.List)}
     * on the requested customizer which is owned by this application.
     *
     * @param documentLookupCriteria the criteria that was used to perform the lookup
     * @param results the results that were returned from the lookup
     * @param customizerName the name of the extension definition for the {@code DocumentLookupCustomizer} which should
     * be used in order to execute the customization
     *
     * @return the customized document lookup results values, or null if no customization was performed
     *
     * @throws RiceIllegalArgumentException if documentLookupCriteria is null
     * @throws RiceIllegalArgumentException if results is null
     * @throws RiceIllegalArgumentException if customizerName is a null or blank value
     */
    @WebMethod(operationName = "customizeResults")
    @WebResult(name = "resultValues")
    @XmlElement(name = "resultValues", required = false)
    DocumentLookupResultValues customizeResults(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "results") List<DocumentLookupResult> results,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    /**
     * Executes customization of document lookup result set configuration using the {@link DocumentLookupCustomizer}
     * with the given customizer name.  This name is the name of the {@code ExtensionDefinition} that defines the
     * customizer where the customizer extension's applicationId is the same as the application hosting this service.
     *
     * <p>This method effectively invokes the {@link DocumentLookupCustomizer#customizeResultSetConfiguration(org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria)} 
     * on the requested customizer which is owned by this application.
     *
     * @param documentLookupCriteria the criteria that was used to perform the lookup
     * @param customizerName the name of the extension definition for the {@code DocumentLookupCustomizer} which should
     * be used in order to execute the customization
     *
     * @return the customized document lookup result configuration, or null if no customization was performed
     *
     * @throws RiceIllegalArgumentException if documentLookupCriteria is null
     * @throws RiceIllegalArgumentException if customizerName is a null or blank value
     */
    @WebMethod(operationName = "customizeResultSetConfiguration")
    @WebResult(name = "resultSetConfiguration")
    @XmlElement(name = "resultSetConfiguration", required = false)
    DocumentLookupResultSetConfiguration customizeResultSetConfiguration(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "customizerName") String customizerName) throws RiceIllegalArgumentException;

    /**
     * Returns the set of customizations that are enabled and should be executed for the {@link DocumentLookupCustomizer}
     * with the given customizer name.  This name is the name of the {@code ExtensionDefinition} that defines the
     * customizer where the customizer extension's applicationId is the same as the application hosting this service.
     *
     * <p>This method essentially invokes the various boolean methods on the {@code DocumentLookupCustomizer} which
     * indicate which customizations the implementation provides.  This primarily serves as a means of optimization to
     * reduce the number of remote callbacks that the document lookup implementation needs to make to the various
     * customizations provided by this service and the customizers it delegates too.</p>
     *
     * @param documentTypeName the name of the document type against which to check for enabled customizations on the
     * specified customizer
     * @param customizerName the name of the extension definition for the {@code DocumentLookupCustomizer} which should
     * be used in order to check for enabled customizations
     *
     * @return the set of customizations that are enabled
     * 
     * @throws RiceIllegalArgumentException if documentTypeName is a null or blank value
     * @throws RiceIllegalArgumentException if customizerName is a null or blank value
     */
    @WebMethod(operationName = "getEnabledCustomizations")
    @WebResult(name = "enabledCustomizations")
    @XmlElementWrapper(name = "enabledCustomizations", required = false)
    @XmlElement(name = "enabledCustomization", required = false)
    Set<DocumentLookupCustomization> getEnabledCustomizations(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

}
