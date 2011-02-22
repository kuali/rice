package org.kuali.rice.shareddata.api.campus;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "campusServiceSoap", targetNamespace = "http://rice.kuali.org/shareddata/api/campus")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CampusService {

    
    /**
     * This will return a {@link Campus}.
     *
     * @param code the code of the campus to return
     * @throws IllegalArgumentException if the code is null
     * @throws IllegalStateException if the campus does not exist in the system under the
     * specific code
     */
    @WebMethod(operationName="getCampus")
    void getCampus(@WebParam(name = "code") String code);
    
    //lookup method?
}
