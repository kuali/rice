package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * Service interface for PeopleFlowDefinition types.  TODO: ...
 */
// TODO: additional JAX-WS annotations
public interface PeopleFlowTypeService {


    PeopleFlow loadPeopleFlow(PeopleFlowDefinition peopleFlowDefinition);

    List<String> filterToSelectableRoleIds(List<String> roleIds);

    Map<String,String> resolveRoleQualifiers(String roleId, Document document, DocumentContent documentContent);

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
