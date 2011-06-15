package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = AdHocRevokeFromGroup.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocRevokeFromGroup.Constants.TYPE_NAME, propOrder = {
		AdHocRevokeFromGroup.Elements.GROUP_ID
})
public final class AdHocRevokeFromGroup extends AdHocRevokeCommand {

	private static final long serialVersionUID = 1084623378944347299L;

	@XmlElement(name = Elements.GROUP_ID, required = true)
	private final String groupId;
	
	private AdHocRevokeFromGroup(String actionRequestId, String nodeName, String groupId) {
		super(actionRequestId, nodeName);
		if (StringUtils.isBlank(groupId)) {
			throw new IllegalArgumentException("groupId was null or blank");
		}
		this.groupId = groupId;
	}
	
	public static AdHocRevokeFromGroup createRevokeByActionRequest(String actionRequestId, String groupId) {
		return new AdHocRevokeFromGroup(actionRequestId, null, groupId);
	}
	
	public static AdHocRevokeFromGroup createRevokeByNodeName(String nodeName, String groupId) {
		return new AdHocRevokeFromGroup(null, nodeName, groupId);
	}
		
	public String getGroupId() {
		return groupId;
	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "adHocRevokeFromGroup";
        final static String TYPE_NAME = "AdHocRevokeFromGroupType";
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String GROUP_ID = "groupId";
    }
	
}
