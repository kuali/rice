package org.kuali.rice.krms.impl.authorization;

import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;

import java.util.HashMap;
import java.util.Map;

public class AgendaAuthorizationServiceImpl implements AgendaAuthorizationService {

    static final String NAMESPACE_CODE = "namespaceCode";

    @Override
    public boolean isAuthorized(String permissionName, String contextId) {
        ContextDefinition context = getContextBoService().getContextByContextId(contextId);

        Map qualification = new HashMap<String, String>();
        if (contextId != null) {
            qualification.put(NAMESPACE_CODE, context.getNamespace());
        }
        boolean isAuthorized = getPermissionService().isAuthorized(
                GlobalVariables.getUserSession().getPrincipalId(),
                KrmsConstants.KRMS_NAMESPACE,
                permissionName,
                qualification,
                new HashMap<String, String>());
        return isAuthorized;
    }

    /**
     * return the contextBoService
     */
    private ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }

    /**
     * returns the permissionService
     */
    private PermissionService getPermissionService() {
        return KimApiServiceLocator.getPermissionService();
    }
}