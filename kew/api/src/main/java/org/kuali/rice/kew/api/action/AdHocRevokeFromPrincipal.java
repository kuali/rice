package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = AdHocRevokeFromPrincipal.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocRevokeFromPrincipal.Constants.TYPE_NAME, propOrder = {
		AdHocRevokeFromPrincipal.Elements.PRINCIPAL_ID
})
public final class AdHocRevokeFromPrincipal extends AdHocRevokeCommand {

	private static final long serialVersionUID = 1084623378944347299L;

	@XmlElement(name = Elements.PRINCIPAL_ID, required = true)
	private final String principalId;
	
	private AdHocRevokeFromPrincipal(String actionRequestId, String nodeName, String principalId) {
		super(actionRequestId, nodeName);
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		this.principalId = principalId;
	}
	
	public static AdHocRevokeFromPrincipal createRevokeByActionRequest(String actionRequestId, String principalId) {
		return new AdHocRevokeFromPrincipal(actionRequestId, null, principalId);
	}
	
	public static AdHocRevokeFromPrincipal createRevokeByNodeName(String nodeName, String principalId) {
		return new AdHocRevokeFromPrincipal(null, nodeName, principalId);
	}
		
	public String getPrincipalId() {
		return principalId;
	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "adHocRevokeFromPrincipal";
        final static String TYPE_NAME = "AdHocRevokeFromPrincipalType";
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String PRINCIPAL_ID = "principalId";
    }
	
}
