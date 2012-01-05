package org.kuali.rice.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kns.kim.permission.PermissionTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Type service for the 'View Widget' KIM type which matches on the id for a UIF view and widget id
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewWidgetPermissionTypeServiceImpl extends PermissionTypeServiceBase {

    @Override
    protected List<String> getRequiredAttributes() {
        List<String> attributes = new ArrayList<String>(super.getRequiredAttributes());
        attributes.add(KimConstants.AttributeConstants.VIEW_ID);
        attributes.add(KimConstants.AttributeConstants.WIDGET_ID);

        return Collections.unmodifiableList(attributes);
    }

    /**
     * Filters the given permission list to return those that match on widget id, then calls super
     * to filter based on view id
     *
     * @param requestedDetails - map of details requested with permission (used for matching)
     * @param permissionsList - list of permissions to process for matches
     * @return List<Permission> list of permissions that match the requested details
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
            List<Permission> permissionsList) {

        String requestedWidgetId = requestedDetails.get(KimConstants.AttributeConstants.WIDGET_ID);

        List<Permission> matchingPermissions = new ArrayList<Permission>();
        for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);

            String permissionWidgetId = bo.getDetails().get(KimConstants.AttributeConstants.WIDGET_ID);
            if (StringUtils.equals(requestedWidgetId, permissionWidgetId)) {
                matchingPermissions.add(permission);
            }
        }

        return super.performPermissionMatches(requestedDetails, matchingPermissions);
    }

}
