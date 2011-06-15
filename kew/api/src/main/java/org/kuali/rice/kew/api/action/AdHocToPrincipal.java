package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = AdHocToPrincipal.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AdHocToPrincipal.Constants.TYPE_NAME, propOrder = {
		AdHocToPrincipal.Elements.TARGET_PRINCIPAL_ID
})
public final class AdHocToPrincipal extends AdHocCommand {

	private static final long serialVersionUID = -4512561589558793736L;

	private final String targetPrincipalId;

	private AdHocToPrincipal() {
		this.targetPrincipalId = null;
	}
	
	private AdHocToPrincipal(Builder builder) {
		super(builder);
		this.targetPrincipalId = builder.getTargetPrincipalId();
	}

	public String getTargetPrincipalId() {
		return targetPrincipalId;
	}
	
	public static final class Builder extends AdHocCommand.Builder<AdHocToPrincipal> {
		
		private static final long serialVersionUID = 5288681963619747957L;

		private String targetPrincipalId;
		
		private Builder(ActionRequestType actionRequested, String nodeName, String targetPrincipalId) {
			super(actionRequested, nodeName);
			setTargetPrincipalId(targetPrincipalId);
		}
		
		public static Builder create(ActionRequestType actionRequested, String nodeName, String targetPrincipalId) {
			return new Builder(actionRequested, nodeName, targetPrincipalId);
		}
		
		public String getTargetPrincipalId() {
			return targetPrincipalId;
		}
		
		public void setTargetPrincipalId(String targetPrincipalId) {
			if (StringUtils.isBlank(targetPrincipalId)) {
				throw new IllegalArgumentException("targetPrincipalId was null or blank");
			}
			this.targetPrincipalId = targetPrincipalId;
		}
		
		@Override
		public AdHocToPrincipal build() {
			return new AdHocToPrincipal(this);
		}

	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "adHocToPrincipal";
        final static String TYPE_NAME = "AdHocToPrincipalType";
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String TARGET_PRINCIPAL_ID = "targetPrincipalId";
    }

	
}
