/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.opensource.org/licenses/ecl2.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kuali.rice.shareddata.api.campus;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.shareddata.api.SharedDataConstants;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "campusService", targetNamespace = SharedDataConstants.Namespaces.SHAREDDATA_NAMESPACE )
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
    Campus getCampus(@WebParam(name = "code") String code) throws RiceIllegalArgumentException;
    
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
     * @return CampusType object represented by the passed in code
     * @throws IllegalArgumentException if the code is null
     * @throws IllegalStateException if the campus does not exist in the system under the
     * specific code
     */
    @WebMethod(operationName="getCampusType")
    CampusType getCampusType(@WebParam(name = "code") String code) throws RiceIllegalArgumentException;
    
    /**
     * This will return all {@link CampusType}.
     *
     */
    @WebMethod(operationName="findAllCampusTypes")
    List<CampusType> findAllCampusTypes();
    
    //lookup method?
}
