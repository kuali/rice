package org.kuali.rice.kew.framework.rule.attribute;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.rule.RoleName;
import org.kuali.rice.kew.api.validation.ValidationResults;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * A remotable service which handles processing of a client application's document lookup customizations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.WORKFLOW_RULE_ATTRIBUTE_HANDLER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface WorkflowRuleAttributeHandlerService {

    /**
     * Returns true if the attribute with the provided name is an instance of WorkflowRuleAttribute
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "isWorkflowRuleAttribute")
    @WebResult(name = "workflowRuleAttribute")
    @XmlElement(name = "workflowRuleAttribute", required = false)
    boolean isWorkflowRuleAttribute(String attributeName);

    /**
     * Gets a List of {@link RemotableAttributeField} based on the passed workflow rule attribute name.  This
     * method loads loads up a WorkflowRuleAttribute and determines the fields for a RuleExtension
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @return an immutable list of RemotableAttributeField. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "getRuleRows")
    @XmlElementWrapper(name = "ruleRows", required = true)
    @XmlElement(name = "ruleRow", required = false)
    @WebResult(name = "ruleRows")
    List<RemotableAttributeField> getRuleRows(@WebParam(name="attributeName") String attributeName)
        throws RiceIllegalArgumentException;

    /**
     * Gets a List of {@link RemotableAttributeField} based on the passed workflow rule or searchable attribute name.
     * This method loads loads up either a WorkflowRuleAttribute or WorkflowRuleSearchAttribute and determines the
     * fields for a RuleExtension.
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @return an immutable list of RemotableAttributeField. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "getSearchRows")
    @XmlElementWrapper(name = "searchRows", required = true)
    @XmlElement(name = "searchRow", required = false)
    @WebResult(name = "searchRows")
    List<RemotableAttributeField> getSearchRows(@WebParam(name="attributeName") String attributeName)
        throws RiceIllegalArgumentException;


    /**
     * Gets a List of {@link RemotableAttributeField} based on the passed workflow rule or searchable attribute name.
     * This method loads loads up either a WorkflowRuleAttribute or WorkflowRuleSearchAttribute and determines the
     * routing data fields for a RuleExtension.
     *
     * <p> RoutingDataRows contain Rows describing the UI-level presentation of the ruleData fields
     * used to determine where a given document would be routed according to the associated rule.</p>
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @return an immutable list of RemotableAttributeField. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "getRoutingDataRows")
    @XmlElementWrapper(name = "routingDataRows", required = true)
    @XmlElement(name = "routingDataRow", required = false)
    @WebResult(name = "routingDataRows")
    List<RemotableAttributeField> getRoutingDataRows(@WebParam(name="attributeName") String attributeName)
        throws RiceIllegalArgumentException;

    /**
     * Validates routingData values in the incoming map.  Called by the UI during rule creation.
     *
     * This method is responsible for validating and setting the data entered on the form from the UI of the routing report to the Rule's attribute.
     * The values will be in a Map with the key being the key of the RuleExtensionValue and the value being the value of the data entered from the
     * UI. This method is used for the routing report which may have different fields than the rule data.
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @param paramMap Map containing the names and values of the routing data for this Attribute
     * @return ValidationResults. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "validateRoutingData")
    @WebResult(name = "validationResults")
    @XmlElement(name = "validationResults", required = false)
    ValidationResults validateRoutingData(@WebParam(name="attributeName")
                             String attributeName,
                             @WebParam(name = "parmMap")
                             @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
                             Map<String, String> paramMap)
        throws RiceIllegalArgumentException;

    /**
     * Validates searchData values in the incoming map.  Called by the UI during rule creation.
     *
     * This method is responsible for validating and setting the data entered on the form from the UI of the routing report to the Rule's attribute.
     * The values will be in a Map with the key being the key of the RuleExtensionValue and the value being the value of the data entered from the
     * UI.
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @param paramMap Map containing the names and values of the routing data for this Attribute
     * @return ValidationResults. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "validateSearchData")
    @WebResult(name = "validationResults")
    @XmlElement(name = "validationResults", required = false)
    ValidationResults validateSearchData(@WebParam(name="attributeName")
                             String attributeName,
                             @WebParam(name = "parmMap")
                             @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
                             Map<String, String> paramMap)
        throws RiceIllegalArgumentException;

    /**
     * Validates ruleExtension values in the incoming map.  Called by the UI during rule creation.
     *
     * This method is responsible for validating and setting the data entered on the form from the UI of the rule creation to the Rule's attribute.
     * The values will be in a Map with the key being the key of the RuleExtensionValue and the value being the value of the data entered from the UI.
     * This method is used for rule creation which may have different fields than the routing report data.
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @param paramMap Map containing the names and values of the routing data for this Attribute
     * @return ValidationResults. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the WorkflowRuleAttribute is not found
     */
    @WebMethod(operationName = "validateRuleData")
    @WebResult(name = "validationResults")
    @XmlElement(name = "validationResults", required = false)
    ValidationResults validateRuleData(@WebParam(name="attributeName")
                             String attributeName,
                             @WebParam(name = "parmMap")
                             @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
                             Map<String, String> paramMap)
        throws RiceIllegalArgumentException;


    /**
     * Gets a List of {@link RoleName} based on the passed role attribute name.  This
     * method loads loads up a RoleAttribute and determines the RoleNames for a RuleExtension
     *
     * @param attributeName name of the WorkflowRuleAttribute.  cannot be null or blank.
     * @return an immutable list of RoleName. Will not return null.
     * @throws RiceIllegalArgumentException if the attributeName is null or blank
     * @throws RiceIllegalArgumentException if the RoleAttribute is not found
     */
    @WebMethod(operationName = "getRoleNames")
    @XmlElementWrapper(name = "roleNames", required = true)
    @XmlElement(name = "roleName", required = false)
    @WebResult(name = "roleNames")
    List<RoleName> getRoleNames(@WebParam(name="attributeName") String attributeName)
        throws RiceIllegalArgumentException;
}
