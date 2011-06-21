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
		AdHocRevokeFromPrincipal.Elements.TARGET_PRINCIPAL_ID
})
public final class AdHocRevokeFromPrincipal extends AdHocRevokeCommand {

	private static final long serialVersionUID = 1084623378944347299L;

	@XmlElement(name = Elements.TARGET_PRINCIPAL_ID, required = true)
	private final String targetPrincipalId;
	
	private AdHocRevokeFromPrincipal(String actionRequestId, String nodeName, String targetPrincipalId) {
		super(actionRequestId, nodeName);
		if (StringUtils.isBlank(targetPrincipalId)) {
			throw new IllegalArgumentException("targetPrincipalId was null or blank");
		}
		this.targetPrincipalId = targetPrincipalId;
	}
	
	public static AdHocRevokeFromPrincipal createRevokeByActionRequest(String actionRequestId, String targetPrincipalId) {
		return new AdHocRevokeFromPrincipal(actionRequestId, null, targetPrincipalId);
	}
	
	public static AdHocRevokeFromPrincipal createRevokeByNodeName(String nodeName, String targetPrincipalId) {
		return new AdHocRevokeFromPrincipal(null, nodeName, targetPrincipalId);
	}
		
	public String getTargetPrincipalId() {
		return targetPrincipalId;
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
        final static String TARGET_PRINCIPAL_ID = "targetPrincipalId";
    }
	
}
