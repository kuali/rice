package org.kuali.rice.kew.framework.docsearch;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.springframework.beans.factory.InitializingBean;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.DOCUMENT_SEARCH_CUSTOMIZATION_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentSearchCustomizationService {

    @WebMethod(operationName = "getSearchAttributeFields")
	@WebResult(name = "searchAttributeFields")
	@XmlElementWrapper(name = "searchAttributeFields", required = false)
	@XmlElement(name = "searchAttributeField", required = false)
    List<AttributeFields> getSearchAttributeFields(String documentTypeName, List<String> searchableAttributeNames);


}
