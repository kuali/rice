package org.kuali.rice.kew.edl.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.edl.EDLContext;
import org.kuali.rice.kew.edl.EDLModelComponent;
import org.kuali.rice.kew.edl.RequestParser;
import org.kuali.rice.kew.edl.UserAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RefreshFromLookupComponent implements EDLModelComponent {
	
	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		String command = edlContext.getUserAction().getAction();
		if (UserAction.ACTION_REFRESH_FROM_LOOKUP.equals(command)) {
			RequestParser requestParser = edlContext.getRequestParser();
			
			Element currentVersion = VersioningPreprocessor.findCurrentVersion(dom);
			// First, create a list of all of the <field> tags that match the parameter names returned from the lookup
			List<Element> fieldsToDelete = new ArrayList<Element>();
			NodeList fieldNodes = currentVersion.getElementsByTagName("field");
			
			// get the list of input parameters returned from the lookup so we can clear empty ones as well
			List<String> requestParameterNames = requestParser.getParameterNames();
			
			for (int i = 0; i < fieldNodes.getLength(); i++) {
				Element fieldNode = (Element) fieldNodes.item(i);
				String fieldName = fieldNode.getAttribute("name");
				if (requestParameterNames.contains(fieldName)) {
					fieldsToDelete.add(fieldNode);
				}
			}
			
			// Second, delete those nodes; we will rely on normal population to recreate those nodes
			// if the nodes weren't deleted, EDL would continue to display the old value on the generated output
			for (Element fieldToDelete : fieldsToDelete) {
				currentVersion.removeChild(fieldToDelete);
			}
		}
	}
}
