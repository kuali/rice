package org.kuali.rice.krad.uif.authorization;

import java.util.Map;

/**
 * Invoked to authorize actions requested on data objects (such as edit or view)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectAuthorizer {

    /**
     * Determines whether the user identified by the given principal ID has the given permission in the context
     * of the data object
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionName
     * @param principalId
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId);

    /**
     * Determines whether the user identified by the given principal ID has been granted a permission of the given
     * template in the context of the data object
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionTemplateName
     * @param principalId
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
            String principalId);

    /**
     * Determines whether the user identified by the given principal ID has the given permission in the context
     * of the data object, the additional permission details and role qualifiers are used for the check
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionName
     * @param principalId
     * @param additionalPermissionDetails
     * @param additionalRoleQualifiers
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId,
            Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers);

    /**
     * Determines whether the user identified by the given principal ID has been granted a permission of the given
     * template in the context of the data object, the additional permission details and role qualifiers are used for
     * the check
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionTemplateName
     * @param principalId
     * @param additionalPermissionDetails
     * @param additionalRoleQualifiers
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
            String principalId, Map<String, String> additionalPermissionDetails,
            Map<String, String> additionalRoleQualifiers);

}
