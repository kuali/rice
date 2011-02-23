package org.kuali.rice.shareddata.api.campus;

import java.util.List;

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
    Campus getCampus(@WebParam(name = "code") String code);
    
    /**
     * This will return all {@link Campus}.
     *
     */
    @WebMethod(operationName="findAllCampuses")
    List<Campus> findAllCampuses();
    
    /**
     * This will return a {@link CampusType}.
     *
     * @param code the code of the campus type to return
     * @throws IllegalArgumentException if the code is null
     * @throws IllegalStateException if the campus does not exist in the system under the
     * specific code
     */
    @WebMethod(operationName="getCampus")
    CampusType getCampusType(@WebParam(name = "code") String code);
    
    /**
     * This will return all {@link CampusType}.
     *
     */
    @WebMethod(operationName="findAllCampusTypes")
    List<CampusType> findAllCampusTypes();
    
    //lookup method?
}
