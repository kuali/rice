package org.kuali.rice.krms.framework.type;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * Interface to be extended by type services that have remotable attributes that will need to be rendered and
 * validated
 */
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RemotableAttributeOwner {

    /**
     *
     * @return
     */
    @WebMethod(operationName="getAttributeFields")
    @WebResult(name = "attributeFields")
    public List<RemotableAttributeField> getAttributeFields();

    @WebMethod(operationName="validateAttributes")
    @XmlElementWrapper(name = "attributeErrors", required = true)
    @XmlElement(name = "attributeError", required = false)
    @WebResult(name = "attributeErrors")
    public List<RemotableAttributeError> validateAttributes(
            @WebParam(name = "attributes")
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            Map<String, String> attributes
    )  throws RiceIllegalArgumentException;

    @WebMethod(operationName="validateAttributesAgainstExisting")
    @XmlElementWrapper(name = "attributeErrors", required = true)
    @XmlElement(name = "attributeError", required = false)
    @WebResult(name = "attributeErrors")
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "newAttributes")
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes")
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            Map<String, String> oldAttributes
    ) throws RiceIllegalArgumentException;

}
