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

    @WebMethod(operationName = "publishComponents")
    void publishComponents(
            @WebParam(name = "applicationId") String applicationId,
            @WebParam(name = "components") List<Component> components
    ) throws RiceIllegalArgumentException;

}
