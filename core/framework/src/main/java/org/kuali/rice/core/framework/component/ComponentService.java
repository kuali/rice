package org.kuali.rice.core.framework.component;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Defines the contract for a service which can be used to interact with the Rice core component store.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "componentServiceSoap", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ComponentService {

    @WebMethod(operationName = "getComponentByCode")
    @WebResult(name = "component")
    Component getComponentByCode(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "componentCode") String componentCode
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getAllComponentsByNamespaceCode")
    @WebResult(name = "components")
    @XmlElementWrapper(name = "components", required = true)
	@XmlElement(name = "component", required = false)
    List<Component> getAllComponentsByNamespaceCode(
            @WebParam(name = "namespaceCode") String namespaceCode
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getApplicationComponents")
    @WebResult(name = "components")
    @XmlElementWrapper(name = "components", required = true)
	@XmlElement(name = "component", required = false)
    List<Component> getApplicationComponents(
            @WebParam(name = "applicationId") String applicationId);

    /**
     * Publishes the given list of components to make them available to the component system.  It should only ever be
     * necessary to invoke this service whenever published components for an application change.   When invoked, the
     * set of components know to the component system for the given application id will be updated.  Any previously published
     * components for the application id which are not contained within the given list will be switched to "inactive".
     *
     * @param applicationId the id of the application under which to publish the given components, must be a valid
     * appplication id
     * @param components the components to publish, may be empty or null, in which case all published components for the
     * given application id will be inactived in the component system
     *
     * @throws RiceIllegalArgumentException if applicationId is a null or blank value
     */
    @WebMethod(operationName = "publishComponents")
    void publishComponents(
            @WebParam(name = "applicationId") String applicationId,
            @WebParam(name = "components") List<Component> components
    ) throws RiceIllegalArgumentException;

}
