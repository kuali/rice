package org.kuali.rice.kew.api.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

/**
 * Defines an update to document content on a particular workflow document.
 * Contains general application content as well as a list of attribute
 * definitions and searchable definitions.  When passed to the appropriate
 * workflow services to perform an update on document content, if any of the
 * internal content or definitions on this object have not been set then they
 * will not be updated.  This allows for this data structure to be used to only
 * update the portion of the document content that is desired to be updated.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@XmlRootElement(name = DocumentContentUpdate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentContentUpdate.Constants.TYPE_NAME, propOrder = {
    DocumentContent.Elements.APPLICATION_CONTENT,
    DocumentContent.Elements.ATTRIBUTE_CONTENT,
    DocumentContent.Elements.SEARCHABLE_CONTENT,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentContentUpdate implements Serializable {

	private static final long serialVersionUID = -7386661044232391889L;

	@XmlElement(name = Elements.APPLICATION_CONTENT, required = false)
	private String applicationContent;
	
	@XmlElementWrapper(name = Elements.ATTRIBUTE_DEFINITIONS, required = false)
	@XmlElement(name = Elements.ATTRIBUTE_DEFINITION, required = false)
    private List<WorkflowAttributeDefinition> attributeDefinitions;

	@XmlElementWrapper(name = Elements.SEARCHABLE_DEFINITIONS, required = false)
	@XmlElement(name = Elements.SEARCHABLE_DEFINITION, required = false)
	private List<WorkflowAttributeDefinition> searchableDefinitions;
    
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
    
    private DocumentContentUpdate(String applicationContent,
    		List<WorkflowAttributeDefinition> attributeDefinitions,
    		List<WorkflowAttributeDefinition> searchableDefinitions) {
    	this.applicationContent = applicationContent;
    	if (attributeDefinitions != null) {
    		this.attributeDefinitions = new ArrayList<WorkflowAttributeDefinition>(attributeDefinitions);
    	}
    	if (searchableDefinitions != null) {
    		this.searchableDefinitions = new ArrayList<WorkflowAttributeDefinition>(searchableDefinitions);
    	}
    }
    
    public static DocumentContentUpdate createApplicationContentUpdate(String applicationContent) {
    	if (applicationContent == null) {
    		throw new IllegalArgumentException("applicationContent was null");
    	}
    	return new DocumentContentUpdate(applicationContent, null, null);
    }
    
    public static DocumentContentUpdate createAttributeUpdate(List<WorkflowAttributeDefinition> attributeDefinitions) {
    	if (attributeDefinitions == null) {
    		throw new IllegalArgumentException("attributeDefinitions was null");
    	}
    	return new DocumentContentUpdate(null, attributeDefinitions, null);
    }
    
    public static DocumentContentUpdate createSearchAttributeUpdate(List<WorkflowAttributeDefinition> searchableDefinitions) {
    	if (searchableDefinitions == null) {
    		throw new IllegalArgumentException("searchableDefinitions was null");
    	}
    	return new DocumentContentUpdate(null, null, searchableDefinitions);
    }
    
    public static DocumentContentUpdate createUpdate(String applicationContent,
    		List<WorkflowAttributeDefinition> attributeDefinitions,
    		List<WorkflowAttributeDefinition> searchableDefinitions) {
    	return new DocumentContentUpdate(applicationContent, attributeDefinitions, searchableDefinitions);
    }
    
	public String getApplicationContent() {
		return applicationContent;
	}

	public List<WorkflowAttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions == null ? null : new ArrayList<WorkflowAttributeDefinition>(attributeDefinitions);
	}

	public List<WorkflowAttributeDefinition> getSearchableDefinitions() {
		return searchableDefinitions == null ? null : new ArrayList<WorkflowAttributeDefinition>(searchableDefinitions);
	}
	
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentContentUpdate";
        final static String TYPE_NAME = "DocumentContentUpdateType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String APPLICATION_CONTENT = "applicationContent";
        final static String ATTRIBUTE_DEFINITION = "attributeDefinition";
        final static String ATTRIBUTE_DEFINITIONS = "attributeDefinitions";
        final static String SEARCHABLE_DEFINITION = "searchableDefinition";
        final static String SEARCHABLE_DEFINITIONS = "searchableDefinitions";
    }

}
