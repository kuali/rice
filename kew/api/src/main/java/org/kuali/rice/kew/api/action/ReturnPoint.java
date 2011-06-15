package org.kuali.rice.kew.api.action;

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

@XmlRootElement(name = ReturnPoint.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ReturnPoint.Constants.TYPE_NAME, propOrder = {
		ReturnPoint.Elements.NODE_NAME,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ReturnPoint {
    
	@XmlElement(name = Elements.NODE_NAME, required = true)
    private final String nodeName;
	    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private ReturnPoint() {
    	this.nodeName = null;
    }
    
    private ReturnPoint(String nodeName) {
    	if (StringUtils.isBlank(nodeName)) {
    		throw new IllegalArgumentException("nodeName was null or blank");
    	}
        this.nodeName = nodeName;
    }
    
    public static ReturnPoint create(String nodeName) {
    	return new ReturnPoint(nodeName);
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
        final static String ROOT_ELEMENT_NAME = "returnPoint";
        final static String TYPE_NAME = "ReturnPointType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String NODE_NAME = "nodeName";
    }

}
