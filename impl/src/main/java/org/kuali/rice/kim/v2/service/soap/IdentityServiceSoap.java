package org.kuali.rice.kim.v2.service.soap;

import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.v2.service.IdentityServiceBase;

public interface IdentityServiceSoap extends IdentityServiceBase {
	public EntityDTO getWebServiceSafeEntityByPrincipalName(String principalName);
}
