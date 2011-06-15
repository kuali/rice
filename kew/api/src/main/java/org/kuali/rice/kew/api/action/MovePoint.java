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

@XmlRootElement(name = MovePoint.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = MovePoint.Constants.TYPE_NAME, propOrder = {
		MovePoint.Elements.START_NODE_NAME,
		MovePoint.Elements.STEPS_TO_MOVE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class MovePoint {
    
	@XmlElement(name = Elements.START_NODE_NAME, required = true)
    private final String startNodeName;
	
	@XmlElement(name = Elements.STEPS_TO_MOVE, required = true)
    private final int stepsToMove;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private MovePoint() {
    	this.startNodeName = null;
    	this.stepsToMove = 0;
    }
    
    private MovePoint(String startNodeName, int stepsToMove) {
    	if (StringUtils.isBlank(startNodeName)) {
    		throw new IllegalArgumentException("startNodeName was null or blank");
    	}
        this.startNodeName = startNodeName;
        this.stepsToMove = stepsToMove;
    }
    
    public static MovePoint create(String startNodeName, int stepsToMove) {
    	return new MovePoint(startNodeName, stepsToMove);
    }
    
    public String getStartNodeName() {
        return startNodeName;
    }

    public int getStepsToMove() {
        return stepsToMove;
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
        final static String ROOT_ELEMENT_NAME = "movePoint";
        final static String TYPE_NAME = "MovePointType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String START_NODE_NAME = "startNodeName";
        final static String STEPS_TO_MOVE = "stepsToMove";
    }

}
