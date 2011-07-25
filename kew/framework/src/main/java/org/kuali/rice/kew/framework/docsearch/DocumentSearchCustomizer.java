package org.kuali.rice.kew.framework.docsearch;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * TODO...
 */
@WebService(name = "documentSearchCustomizerServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentSearchCustomizer {

    @WebMethod(operationName = "getSearchFields")
    @WebResult(name = "searchFields")
    @XmlElementWrapper(name = "searchFields", required = false)
    @XmlElement(name = "searchField", required = false)
    public List<RemotableAttributeField> getSearchFields(@WebParam(name = "documentSearchContext") DocumentSearchContext documentSearchContext);

    // TODO add remaining methods from other doc search classes to form the customizer plug point

}
