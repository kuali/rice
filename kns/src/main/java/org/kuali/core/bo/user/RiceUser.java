package org.kuali.core.bo.user;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class RiceUser extends PersistableBusinessObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2912023481237314564L;

	private String personUniversalIdentifier;
	private String universityId;
	private String personUserIdentifier;
	private String emailAddress;
	private String personName;
	
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap map = new LinkedHashMap();
		map.put("RiceUser", "RiceUser");
		return map;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String name) {
		this.personName = name;
	}

	public String getPersonUserIdentifier() {
		return personUserIdentifier;
	}

	public void setPersonUserIdentifier(String networkId) {
		this.personUserIdentifier = networkId;
	}

	public String getUniversityId() {
		return universityId;
	}

	public void setUniversityId(String universityId) {
		this.universityId = universityId;
	}

	public String getPersonUniversalIdentifier() {
		return personUniversalIdentifier;
	}

	public void setPersonUniversalIdentifier(String userId) {
		this.personUniversalIdentifier = userId;
	}

}
