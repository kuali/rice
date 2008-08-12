package org.kuali.rice.kim.v2.service;

import org.kuali.rice.kim.dto.EntityDTO;

public interface IdentityServiceSoap extends IdentityServiceBase {
	public EntityDTO getWebServiceSafeEntityByPrincipalName(String principalName);
}
