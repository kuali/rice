package org.kuali.rice.kew.api.document.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = AdHocToGroup.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocToGroup.Constants.TYPE_NAME, propOrder = {
		AdHocToGroup.Elements.TARGET_GROUP_ID
})
public final class AdHocToGroup extends AdHocCommand {

	private static final long serialVersionUID = 1543126020560887187L;

	private final String targetGroupId;

	private AdHocToGroup() {
		this.targetGroupId = null;
	}
	
	private AdHocToGroup(Builder builder) {
		super(builder);
		this.targetGroupId = builder.getTargetGroupId();
	}

	public String getTargetGroupId() {
		return targetGroupId;
	}
	
	public static final class Builder extends AdHocCommand.Builder<AdHocToGroup> {
		
		private static final long serialVersionUID = 3062630774766721773L;

		private String targetGroupId;
		
		private Builder(ActionRequestType actionRequested, String nodeName, String targetGroupId) {
			super(actionRequested, nodeName);
			setTargetGroupId(targetGroupId);
		}
		
		public static Builder create(ActionRequestType actionRequested, String nodeName, String targetGroupId) {
			return new Builder(actionRequested, nodeName, targetGroupId);
		}
		
		public String getTargetGroupId() {
			return targetGroupId;
		}
		
		public void setTargetGroupId(String targetGroupId) {
			if (StringUtils.isBlank(targetGroupId)) {
				throw new IllegalArgumentException("targetGroupId was null or blank");
			}
			this.targetGroupId = targetGroupId;
		}
		
		@Override
		public AdHocToGroup build() {
			return new AdHocToGroup(this);
		}

	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "adHocToGroup";
        final static String TYPE_NAME = "AdHocToGroupType";
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String TARGET_GROUP_ID = "targetGroupId";
    }

	
}
