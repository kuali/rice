package org.kuali.rice.kew.api.document.actions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * TODO...
 * 
 * @author ewestfal
 *
 */
@XmlRootElement(name = "actionType")
@XmlType(name = "ActionTypeType")
@XmlEnum
public enum ActionType implements Coded {

    @XmlEnumValue("k") SU_ACKNOWLEDGE("k", "SUPER USER ACKNOWLEDGE"),
    
    @XmlEnumValue("f") SU_FYI("f", "SUPER USER FYI"),
    
    @XmlEnumValue("m") SU_COMPLETE("m", "SUPER USER COMPLETE"),
    
    @XmlEnumValue("v") SU_APPROVE("v", "SUPER USER APPROVE"),
    
    @XmlEnumValue("r") SU_ROUTE_NODE_APPROVE("r", "SUPER USER ROUTE NODE APPROVE"),
    
    @XmlEnumValue("z") SU_RETURN_TO_PREVIOUS("z", "SUPER USER RETURN TO PREVIOUS"),
    
    @XmlEnumValue("d") SU_DISAPPROVE("d", "SUPER USER DISAPPROVE"),
    
    @XmlEnumValue("c") SU_CANCEL("c", "SUPER USER CANCEL"),
    
    @XmlEnumValue("a") SU_BLANKET_APPROVE("a", "SUPER USER BLANKET APPROVE"),
    
    @XmlEnumValue("B") BLANKET_APPROVE("B", "BLANKET APPROVE"),
    
    @XmlEnumValue("F") FYI("F", "FYI"),
    
    /**
     * User has generated an action request to another user
     */
    @XmlEnumValue("H") ADHOC_REQUEST("H", "ADHOC REQUEST"),
    
    /**
     * AdHoc Request has been revoked
     */
    @XmlEnumValue("V") ADHOC_REQUEST_REVOKE("V", "ADHOC REQUEST_REVOKE"),
    
    /**
     * Document has been saved by the user for later work
     */
    @XmlEnumValue("S") SAVE("S", "SAVED"),
    
    /**
     * Document has been canceled.
     */
    @XmlEnumValue("X") CANCEL("X", "CANCEL"),
    
    /**
     * Document has been disapproved.
     */
    @XmlEnumValue("D") DISAPPROVE("D", "DISAPPROVE"),
    
    /**
     * Document has been opened by the designated recipient.
     */
    @XmlEnumValue("K") ACKNOWLEDGE("K", "ACKNOWLEDGE"),
    
    /**
     * Document has been completed as requested.
     */
    @XmlEnumValue("C") COMPLETE("C", "COMPLETE"),
    
    /**
     * Document has been submitted to the engine for processing.
     */
    @XmlEnumValue("O") ROUTE("O", "ROUTE"),
    
    /**
     * The document has been approved.
     */
    @XmlEnumValue("A") APPROVE("A", "APPROVE"),
    
    /**
     * The document is being returned to a previous routelevel
     */
    @XmlEnumValue("Z") RETURN_TO_PREVIOUS("Z", "RETURN TO PREVIOUS"),
    
    /**
     * The document has non-routed activity against it that is recorded in the route log
     */
    @XmlEnumValue("R") LOG_MESSAGE("R", "LOG MESSAGE"),
    
    /**
     * The document is routed to a workgroup and a user in the workgroup wants to take authority from the workgroup
     */
    @XmlEnumValue("w") TAKE_WORKGROUP_AUTHORITY("w", "TAKE WORKGROUP AUTHORITY"),
    		
    /**
     * The person who took workgroup authority is releasing it
     */
    @XmlEnumValue("y") ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD("y", "RELEASE WORKGROUP AUTHORITY"),
    
    /**
     * The document is moved
     */
    @XmlEnumValue("M") MOVE("M", "MOVED");

	private final String code;
	private final String label;
	
	private ActionType(String code, String label) {
		this.code = code;
		this.label = label;
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static ActionType fromCode(String code) {
		return fromCode(code, false);
	}
	
	public static ActionType fromCode(String code, boolean allowMissing) {
		if (code == null) {
			return null;
		}
		for (ActionType status : values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		if (allowMissing) {
			return null;
		}
		throw new IllegalArgumentException("Failed to locate the ActionType with the given code: " + code);
	}
	
}
