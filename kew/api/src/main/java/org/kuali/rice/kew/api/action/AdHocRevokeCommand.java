package org.kuali.rice.kew.api.action;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

@XmlRootElement(name = AdHocRevokeCommand.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocRevokeCommand.Constants.TYPE_NAME, propOrder = {
		AdHocRevokeCommand.Elements.ACTION_REQUEST_ID,
		AdHocRevokeCommand.Elements.NODE_NAME,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public abstract class AdHocRevokeCommand implements Serializable {

	private static final long serialVersionUID = 5848714514445793355L;

	@XmlElement(name = Elements.ACTION_REQUEST_ID, required = false)
	private final String actionRequestId;
	
	@XmlElement(name = Elements.NODE_NAME, required = false)
	private final String nodeName;
		
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
    protected AdHocRevokeCommand(String actionRequestId, String nodeName) {
    	boolean actionRequestIdNull = StringUtils.isBlank(actionRequestId);
    	boolean nodeNameNull = StringUtils.isBlank(nodeName);
    	if (actionRequestIdNull && nodeNameNull) {
    		throw new IllegalArgumentException("One of actionRequestId or nodeName must not be null or blank");
    	}
    	if (!actionRequestIdNull && !nodeNameNull) {
    		throw new IllegalArgumentException("Only one of actionRequestId or nodeName must be defined on AdHocRevokeCommand");
    	}
    	this.actionRequestId = actionRequestId;
    	this.nodeName = nodeName;
    }
    
    public String getActionRequestId() {
		return actionRequestId;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "adHocRevokeCommand";
        final static String TYPE_NAME = "AdHocRevokeCommandType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String ACTION_REQUEST_ID = "actionRequestedId";
        final static String NODE_NAME = "nodeName";
    }

}
