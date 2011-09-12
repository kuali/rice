/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kew.api.KewApiConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@WebService(name = "ruleServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RuleService {
    /**
     * gets a Rule identified by the passed in id
     *
     * @param id unique idea for the Rule
     *
     * @return Rule with the passed in unique id
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if {@code id} is null
     * @throws org.kuali.rice.core.api.exception.RiceIllegalStateException if Rule does not exist
     */
    @WebMethod(operationName = "getRule")
    @WebResult(name = "rule")
	Rule getRule(@WebParam(name="id") String id)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * gets a Rule identified by the passed in rule name
     *
     * @param name name of the Rule
     *
     * @return Rule with the passed in unique id
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if {@code name} is null
     * @throws org.kuali.rice.core.api.exception.RiceIllegalStateException if Rule does not exist
     */
    @WebMethod(operationName = "getRuleByName")
    @WebResult(name = "rule")
	Rule getRuleByName(@WebParam(name="name") String name)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * Query for rules based on the given search criteria which is a Map of rule field names to values.
     *
     * <p>
     * This method returns it's results as a List of Rules that match the given search criteria.
     * </p>
     *
     * @param queryByCriteria the criteria.  Cannot be null.
     * @return a list of Rule objects in which the given criteria match Rule properties.  An empty list is returned if an invalid or
     *         non-existent criteria is supplied.
     */
    @WebMethod(operationName = "findRules")
    @WebResult(name = "findRules")
    RuleQueryResults findRules(@WebParam(name = "query") QueryByCriteria queryByCriteria)
        throws RiceIllegalArgumentException;

    /**
     * Executes a simulation of a document to get all previous and future route information
     *
     * @param reportCriteria criteria for the rule report to follow
     *
     * @return list of Rules representing the results of the rule report
     *
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if {@code reportCriteria} is null
     */
    @WebMethod(operationName = "ruleReport")
    @WebResult(name = "rules")
    @XmlElementWrapper(name = "rules", required = true)
    @XmlElement(name = "rule", required = true)
    List<Rule> ruleReport(
            @WebParam(name = "ruleCriteria") RuleReportCriteria reportCriteria)
            throws RiceIllegalArgumentException;
	
}
