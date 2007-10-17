package edu.iu.uis.eden.edl.components;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.PropertyDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * Executes validations and generates XML for workflow attributes that are defined on the EDL Definitions.
 * These attribute definitions exist in a form similiar to the following:
 *
 * <attributes>
 *   <attribute name="AccountAttribute">
 *     <field edlField="finCoaCd" attributeField="finCoaCd"/>
 *     <field edlField="accountNbr" attributeField="accountNbr"/>
 *     <field edlField="totalDollarAmount" attributeField="totalDollarAmount"/>
 *   </attribute>
 * </attributes>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GlobalAttributeComponent extends SimpleWorkflowEDLConfigComponent implements EDLModelComponent  {

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		RequestParser requestParser = edlContext.getRequestParser();
		try {
			WorkflowDocument document = (WorkflowDocument)requestParser.getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
			//			 clear attribute content so that duplicate attribute values are not added during submission of a new EDL form values version
			document.clearAttributeContent();
			Document edlDef = KEWServiceLocator.getEDocLiteService().getDefinitionXml(edlContext.getEdocLiteAssociation());
			XPath xpath = XPathHelper.newXPath(edlDef);
			NodeList attributeNodes = (NodeList)xpath.evaluate("/edl/attributes/attribute", edlDef, XPathConstants.NODESET);
			for (int index = 0; index < attributeNodes.getLength(); index++) {
				Element attributeElem = (Element)attributeNodes.item(index);
				String attributeName = attributeElem.getAttribute("name");


				WorkflowAttributeDefinitionVO attributeDef = getWorkflowAttributeDefinitionVO(attributeName, document);

				NodeList fieldNodes = (NodeList)xpath.evaluate("./field", attributeElem, XPathConstants.NODESET);
				for (int fIndex = 0; fIndex < fieldNodes.getLength(); fIndex++) {
					Element fieldElem = (Element)fieldNodes.item(fIndex);
					String edlField = fieldElem.getAttribute("edlField");
					String attributeField = fieldElem.getAttribute("attributeField");
					PropertyDefinitionVO property = attributeDef.getProperty(attributeField);
					String value = requestParser.getParameterValue(edlField);
					if (property == null) {
						property = new PropertyDefinitionVO(attributeField, value);
						attributeDef.addProperty(property);
					} else {
						property.setValue(value);
					}
				}
				// validate if they are taking an action on the document (i.e. it's annotatable)
				String action = requestParser.getParameterValue(WorkflowDocumentActions.USER_ACTION_REQUEST_KEY);
				if (EDLXmlUtils.isValidatableAction(action)) {
					WorkflowAttributeValidationErrorVO[] errors = document.validateAttributeDefinition(attributeDef);
					if (errors.length > 0) {
						edlContext.setInError(true);
					}
					Map<String, String> fieldErrors = (Map<String, String>)edlContext.getRequestParser().getAttribute(RequestParser.GLOBAL_FIELD_ERRORS_KEY);
					for (int atIndex = 0; atIndex < errors.length; atIndex++) {
						WorkflowAttributeValidationErrorVO error = errors[atIndex];
						fieldErrors.put(error.getKey(), error.getMessage());
					}
				}
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}
			throw new WorkflowRuntimeException("Failed to process attribute.", e);
		}
	}

	private WorkflowAttributeDefinitionVO getWorkflowAttributeDefinitionVO(String attributeName, WorkflowDocument document) {
		for (int i = 0; i < document.getAttributeDefinitions().length; i++) {
			WorkflowAttributeDefinitionVO workflowAttributeDef = (WorkflowAttributeDefinitionVO)document.getAttributeDefinitions()[i];
			if (workflowAttributeDef.getAttributeName().equals(attributeName)) {
				return workflowAttributeDef;
			}
		}
		WorkflowAttributeDefinitionVO workflowAttributeDef = new WorkflowAttributeDefinitionVO(attributeName);
		document.addAttributeDefinition(workflowAttributeDef);
		return workflowAttributeDef;
	}

}
