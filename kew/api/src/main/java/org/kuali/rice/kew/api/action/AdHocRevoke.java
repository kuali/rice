package org.kuali.rice.kew.api.action;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

@XmlRootElement(name = AdHocRevoke.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocRevoke.Constants.TYPE_NAME, propOrder = {
		AdHocRevoke.Elements.NODE_NAMES,
		AdHocRevoke.Elements.PRINCIPAL_IDS,
		AdHocRevoke.Elements.GROUP_IDS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AdHocRevoke implements Serializable {

	private static final long serialVersionUID = 5848714514445793355L;
	
	@XmlElementWrapper(name = Elements.NODE_NAMES, required = false)
	@XmlElement(name = Elements.NODE_NAME, required = false)
	private final Set<String> nodeNames;
	
	@XmlElementWrapper(name = Elements.PRINCIPAL_IDS, required = false)
	@XmlElement(name = Elements.PRINCIPAL_ID, required = false)
	private final Set<String> principalIds;
	
	@XmlElementWrapper(name = Elements.GROUP_IDS, required = false)
	@XmlElement(name = Elements.GROUP_ID, required = false)
	private final Set<String> groupIds;
		
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
    private AdHocRevoke(Set<String> nodeNames, Set<String> principalIds, Set<String> groupIds) {
    	this.nodeNames = nodeNames;
    	this.principalIds = principalIds;
    	this.groupIds = groupIds;
    }
    
    public static AdHocRevoke create(Set<String> nodeNames, Set<String> principalIds, Set<String> groupIds) {
    	return new AdHocRevoke(nodeNames, principalIds, groupIds);
    }
    
    public static AdHocRevoke createRevokeFromPrincipal(String principalId) {
    	if (StringUtils.isBlank(principalId)) {
    		throw new IllegalArgumentException("principalId was null or blank");
    	}
    	return create(null, Collections.singleton(principalId), null);
    }
    
    public static AdHocRevoke createRevokeFromGroup(String groupId) {
    	if (StringUtils.isBlank(groupId)) {
    		throw new IllegalArgumentException("groupId was null or blank");
    	}
    	return create(null, null, Collections.singleton(groupId));
    }
    
    public static AdHocRevoke createRevokeAtNode(String nodeName) {
    	if (StringUtils.isBlank(nodeName)) {
    		throw new IllegalArgumentException("nodeName was null or blank");
    	}
    	return create(Collections.singleton(nodeName), null, null);
    }
    
	public Set<String> getNodeNames() {
		if (nodeNames == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(nodeNames);
	}
	
	public Set<String> getPrincipalIds() {
		if (principalIds == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(principalIds);
	}
	
	public Set<String> getGroupIds() {
		if (groupIds == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(groupIds);
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
        final static String ROOT_ELEMENT_NAME = "adHocRevoke";
        final static String TYPE_NAME = "AdHocRevokeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
    	final static String NODE_NAMES = "nodeNames";
        final static String NODE_NAME = "nodeName";
        final static String PRINCIPAL_IDS = "principalIds";
        final static String PRINCIPAL_ID = "principalId";
        final static String GROUP_IDS = "groupIds";
        final static String GROUP_ID = "groupId";
    }

}
