package org.kuali.rice.core.framework.component;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.ksb.api.KsbApiConstants;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A service which provides an application's additional components to the core's component system.  This service is
 * meant to be invoked by the Rice standalone server and individual "publishers" can be registered with this service in
 * order to publish components in the ways in which the application wishes to publish them.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "componentProviderServiceSoap", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ComponentProviderService {

    /**
     * Returns components which should be published by the application hosting this service in addition to those
     * which are store permanently within the component system.
     *
     * @return an unmodifiable list of components which this provider wishes to publish, this list may be empty or null
     * which indicates the provider does not have any components to publish
     */
    @WebMethod(operationName = "getAdditionalComponents")
	@WebResult(name = "components")
	@XmlElementWrapper(name = "components", required = false)
	@XmlElement(name = "component", required = false)
    List<Component> getAdditionalComponents();

    @WebMethod(exclude = true)
    void registerPublisher(ComponentPublisher publisher);

}
