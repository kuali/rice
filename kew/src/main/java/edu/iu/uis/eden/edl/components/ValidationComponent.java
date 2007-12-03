package edu.iu.uis.eden.edl.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * Executes validations that are defined on the EDL Definitions.  These validation exist in a form
 * similiar to the following:
 *
 * <validations>
 *   <validation type="xpath">
 *     <expression>wf:field('grade') = 'other' and not(wf:empty(wf:field('otherGrade'))</expression>
 *     <message>Other Grade is required when grade is marked as 'other'</message>
 *   </validation>
 * </validations>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ValidationComponent extends SimpleWorkflowEDLConfigComponent implements EDLModelComponent  {

	private static final String XPATH_TYPE = "xpath";
	private EDLContext edlContext;

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		String action = edlContext.getRequestParser().getParameterValue(WorkflowDocumentActions.USER_ACTION_REQUEST_KEY);


		if (EDLXmlUtils.isValidatableAction(action)) {
			try {
				Document edlDef = KEWServiceLocator.getEDocLiteService().getDefinitionXml(edlContext.getEdocLiteAssociation());
				List<EDLValidation> validations = parseValidations(edlDef);
				if (!validations.isEmpty()) {
					XPath xpath = XPathHelper.newXPath(dom);
					for (EDLValidation validation : validations) {
						executeValidation(xpath, dom, validation, edlContext);
					}
				}
			} catch (Exception e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}
				throw new WorkflowRuntimeException("Failed to execute EDL validations.", e);
			}
		}
	}

	protected List<EDLValidation> parseValidations(Document document) throws Exception {
		List<EDLValidation> validations = new ArrayList<EDLValidation>();
		XPath xpath = XPathHelper.newXPath(document);
		NodeList validationNodes = (NodeList)xpath.evaluate("/edl/validations/validation", document, XPathConstants.NODESET);
		for (int index = 0; index < validationNodes.getLength(); index++) {
			Element validationElem = (Element)validationNodes.item(index);
			EDLValidation validation = new EDLValidation();
			String type = validationElem.getAttribute("type");
			String key = validationElem.getAttribute("key");
			String expression = EDLXmlUtils.getChildElementTextValue(validationElem, "expression");
			String message = EDLXmlUtils.getChildElementTextValue(validationElem, "message");
			if (StringUtils.isBlank(type)) {
				throw new WorkflowRuntimeException("An improperly configured validation was found with an empty type.");
			}
			if (StringUtils.isBlank(expression)) {
				throw new WorkflowRuntimeException("An improperly configured validation was found with an empty expression.");
			}
			if (StringUtils.isBlank(message)) {
				throw new WorkflowRuntimeException("An improperly configured validation was found with an empty message.");
			}
			validation.setType(type);
			validation.setKey(key);
			validation.setExpression(expression);
			validation.setMessage(message);
			validations.add(validation);
		}
		return validations;
	}

	protected void executeValidation(XPath xpath, Document dom, EDLValidation validation, EDLContext edlContext) throws Exception {
		// TODO: in the future, allow this to be pluggable, hardcode for now
		if (XPATH_TYPE.equals(validation.getType())) {
			Boolean result = (Boolean)xpath.evaluate(validation.getExpression(), dom, XPathConstants.BOOLEAN);
			// if validation returns false, we'll flag the error
			if (!result) {
				String key = validation.getKey();
				if (!StringUtils.isEmpty(key)) {
					Map<String, String> fieldErrors = (Map<String, String>)edlContext.getRequestParser().getAttribute(RequestParser.GLOBAL_FIELD_ERRORS_KEY);
					fieldErrors.put(key, validation.getMessage());

					// set invalid attribute to true on corresponding field
					//TODO remove - handled this in the widgets
//					Element edlElement = EDLXmlUtils.getEDLContent(dom, false);
//					Element edlSubElement = EDLXmlUtils.getOrCreateChildElement(edlElement, "data", true);
//					NodeList versionNodes = edlSubElement.getChildNodes();
//					for (int i = 0; i < versionNodes.getLength(); i++) {
//						Element version = (Element) versionNodes.item(i);
//						String current = version.getAttribute("current");
//						if (current == "true") {
//							NodeList fieldNodes = version.getChildNodes();
//							for (int j = 0; j < fieldNodes.getLength(); j++) {
//								Element field = (Element) fieldNodes.item(j);
//								String fieldName = field.getAttribute("name");
//								if(fieldName.equals(key)) {
//									field.setAttribute("invalid", "true");
//									break;
//								}
//							}
//						}
//					}

				} else {
					List globalErrors = (List)edlContext.getRequestParser().getAttribute(RequestParser.GLOBAL_ERRORS_KEY);
					globalErrors.add(validation.getMessage());
				}
				edlContext.setInError(true);
			}
		} else {
			throw new WorkflowRuntimeException("Illegal validation type specified.  Only 'xpath' is currently supported.");
		}
	}


}
