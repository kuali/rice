/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krms.api.repository.term;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.KrmsConstants;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * The TermRepositoryService provides the basic access to terms and term resolvers in the repository needed
 * for executing rules.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "termRepositoryService", targetNamespace = KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface TermRepositoryService {


    /**
     * Retrieves all {@link TermResolverDefinition}s for the given namespace.
     *
     * @since 2.1.1
     * @param namespace the namespace for which to get all term resolvers.
     * @return the List of {@link TermResolverDefinition}s for the given namespace. May be empty, but never null.
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if the namespace is null or blank.
     */
    @WebMethod(operationName = "findTermResolversByNamespace")
    @XmlElementWrapper(name = "termResolvers", required = true)
    @XmlElement(name = "termResolver", required = false)
    @WebResult(name = "termResolvers")
    @Cacheable(value= TermResolverDefinition.Cache.NAME, key="'namespace=' + #p0")
    List<TermResolverDefinition> findTermResolversByNamespace(@WebParam(name = "namespace") String namespace) throws RiceIllegalArgumentException;;

    /**
     * Retrieves the {@link TermDefinition} with the given termId.
     *
     * @since 2.1.1
     * @param termId the identifier of the term to retrieve.
     * @return the {@link TermDefinition} with the given termId.  May be null if there is no term with the given termId
     * in the repository.
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if the termId is null or blank.
     */
    @WebMethod(operationName = "getTerm")
    @WebResult(name = "term")
    @Cacheable(value= TermDefinition.Cache.NAME, key="'id=' + #p0")
    TermDefinition getTerm(@WebParam(name = "termId") String termId) throws RiceIllegalArgumentException;;

    /**
     * Retrieves all the {@link TermSpecificationDefinition}s that are valid for the context with the given contextId.
     *
     * @since 2.1.4
     * @param contextId the identifier for the context whose valid {@link TermSpecificationDefinition}s are to be retrieved. 
     * @return all the {@link TermSpecificationDefinition}s that are valid for the context with the given contextId. May be empty but never null
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if the contextId is null or blank.
     */
    @WebMethod(operationName = "findAllTermSpecificationsByContextId")
    @XmlElementWrapper(name = "termSpecifications", required = true)
    @XmlElement(name = "termSpecification", required = false)
    @WebResult(name = "termSpecifications")
    @Cacheable(value= TermSpecificationDefinition.Cache.NAME, key="'id=' + #p0")
    List<TermSpecificationDefinition> findAllTermSpecificationsByContextId(@WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException;;
}
