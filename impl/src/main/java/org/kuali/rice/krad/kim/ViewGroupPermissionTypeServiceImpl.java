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
 * Type service for the 'View Group' KIM type which matches on the id for a UIF view, group id or collection
 * property name
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewGroupPermissionTypeServiceImpl extends PermissionTypeServiceBase {

    @Override
    protected List<String> getRequiredAttributes() {
        List<String> attributes = new ArrayList<String>(super.getRequiredAttributes());
        attributes.add(KimConstants.AttributeConstants.VIEW_ID);

        return Collections.unmodifiableList(attributes);
    }

    /**
     * Filters the given permission list to return those that match on group id or collection property name, then
     * calls super to filter based on view id
     *
     * @param requestedDetails - map of details requested with permission (used for matching)
     * @param permissionsList - list of permissions to process for matches
     * @return List<Permission> list of permissions that match the requested details
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
            List<Permission> permissionsList) {

        String requestedGroupId = null;
        if (requestedDetails.containsKey(KimConstants.AttributeConstants.GROUP_ID)) {
            requestedGroupId = requestedDetails.get(KimConstants.AttributeConstants.GROUP_ID);
        }

        String requestedCollectionPropertyName = null;
        if (requestedDetails.containsKey(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME)) {
            requestedCollectionPropertyName = requestedDetails.get(
                    KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME);
        }

        List<Permission> matchingPermissions = new ArrayList<Permission>();
        for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);

            String permissionGroupId = null;
            if (bo.getDetails().containsKey(KimConstants.AttributeConstants.GROUP_ID)) {
                permissionGroupId = bo.getDetails().get(KimConstants.AttributeConstants.GROUP_ID);
            }

            String permissionCollectionPropertyName = null;
            if (bo.getDetails().containsKey(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME)) {
                permissionCollectionPropertyName = bo.getDetails().get(
                        KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME);
            }

            if ((requestedGroupId != null) && (permissionGroupId != null) && StringUtils.equals(requestedGroupId,
                    permissionGroupId)) {
                matchingPermissions.add(permission);
            } else if ((requestedCollectionPropertyName != null) && (permissionCollectionPropertyName != null)
                    && StringUtils.equals(requestedCollectionPropertyName, permissionCollectionPropertyName)) {
                matchingPermissions.add(permission);
            }
        }

        return super.performPermissionMatches(requestedDetails, matchingPermissions);
    }

}
