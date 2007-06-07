package edu.sampleu.travel.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.GenericWorkflowAttribute;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;

public class DestinationRuleAttribute extends GenericWorkflowAttribute {

	private static final String DEST_LABEL = "Destination";
	private static final String DEST_FIELD_KEY = "destination";
	
    private static final List<Row> rows = new ArrayList<Row>();
    static {
        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field(DEST_LABEL, "", Field.TEXT, true, DEST_FIELD_KEY, "", null, null, DEST_FIELD_KEY));
        rows.add(new Row(fields));
    }

	private String destination;

    public DestinationRuleAttribute() {
        super("destination");
    }
    
    public DestinationRuleAttribute(String destination) {
        super("destination");
        this.destination = destination;
    }

    /*
	public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
		try {
			boolean foundDestRule = false;
			for (Iterator extensionsIterator = ruleExtensions.iterator(); extensionsIterator.hasNext();) {
	            RuleExtension extension = (RuleExtension) extensionsIterator.next();
	            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(getClass().getName())) {
	                for (Iterator valuesIterator = extension.getExtensionValues().iterator(); valuesIterator.hasNext();) {
	                    RuleExtensionValue extensionValue = (RuleExtensionValue) valuesIterator.next();
	                    String key = extensionValue.getKey();
	                    String value = extensionValue.getValue();
	                    if (key.equals(DEST_FIELD_KEY)) {
	                    	destination = value;
	                    	foundDestRule = true;
	                    }
	                }
	            }
	        }
			if (! foundDestRule) {
				return false;
			}
			
			Element element = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new BufferedReader(new StringReader(docContent.getDocContent())))).getDocumentElement();
			XPath xpath = XPathFactory.newInstance().newXPath();
			String docContentDest = xpath.evaluate("//destination", element);
			return destination.equals(docContentDest);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}*/

	public List<Row> getRuleRows() {
		return getRows();
	}

	public List<Row> getRoutingDataRows() {
		return getRows();
	}

    private List<Row> getRows() {
        log.info("Returning rows: " + rows);
        return rows;
    }

    /* setter for edoclite field */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<String, String>();
        props.put("destination", destination);
        return props;
    }

	public List validateRoutingData(Map paramMap) {
		return validateInputMap(paramMap);
	}

	public List validateRuleData(Map paramMap) {
		return validateInputMap(paramMap);
	}
    
    private List validateInputMap(Map paramMap) {
    	List errors = new ArrayList();
    	this.destination = (String) paramMap.get(DEST_FIELD_KEY);
    	if (this.destination == null  && required) {
    		errors.add(new WorkflowAttributeValidationError(DEST_FIELD_KEY, "Destination is required."));
    	}
    	return errors;
    }	
}