package org.kuali.rice.krms.impl.authorization;

import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krms.api.KrmsConstants;

import java.util.HashMap;

public class AgendaAuthorizationServiceImpl implements AgendaAuthorizationService {

    @Override
    public boolean hasPermission(String permissionName) {
        boolean hasPermission = getPermissionService().isAuthorized(
                GlobalVariables.getUserSession().getPrincipalId(),
                KrmsConstants.KRMS_NAMESPACE,
                permissionName,
                new HashMap<String, String>(),
                new HashMap<String, String>());
        return hasPermission;
    }

    private PermissionService getPermissionService() {
        return KimApiServiceLocator.getPermissionService();
    }
}