/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.core.api.style;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.core.api.CoreConstants;

@WebService(name = "styleRepositoryServiceSoap", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface StyleRepositoryService {
	    
	@WebMethod(operationName="getStyle")
    @WebResult(name = "style")
    public Style getStyle(@WebParam(name = "styleName") String styleName);
    
	@WebMethod(operationName="getStyleNames")
    @WebResult(name = "styleNames")
    public List<String> getStyleNames();
    
	@WebMethod(operationName="getStyles")
    @WebResult(name = "styles")
    public List<Style> getStyles();
    
	@WebMethod(operationName="saveStyle")
    public void saveStyle(@WebParam(name = "style") Style style);
    
}
