package org.kuali.rice.core.framework.component;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Defines the contract for a message queue which can be used to publish components to the Rice core component store.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "componentPublishingQueueSoap", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ComponentPublishingQueue {

    /**
     * Publishes the given list of components to make them available to the component system.  It should only ever be
     * necessary to invoke this service whenever published components for an application change.   When invoked, the
     * set of components know to the component system for the given application id will be updated.  Any existing
     * components for the application id which are not contained within the given list will be switched to "inactive".
     *
     * @param applicationId the id of the application under which to publish the given components, must be a valid
     * appplication id
     * @param components the components to publish, may be empty or null, in which case all components for the given
     * application id will be inactived in the component system
     *
     * @throws RiceIllegalArgumentException if applicationId is a null or blank value
     */
    @WebMethod(operationName = "publishComponents")
    void publishComponents(
            @WebParam(name = "applicationId") String applicationId,
            @WebParam(name = "components") List<Component> components
    ) throws RiceIllegalArgumentException;

}
