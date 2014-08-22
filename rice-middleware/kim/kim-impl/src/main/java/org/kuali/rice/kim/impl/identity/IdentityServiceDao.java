package org.kuali.rice.kim.impl.identity;

import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;

import java.util.List;
import java.util.Map;

public interface IdentityServiceDao {

    Map<String, EntityNamePrincipalName> getDefaultNamesByPrincipalIds(List<String> principalIds);
}
