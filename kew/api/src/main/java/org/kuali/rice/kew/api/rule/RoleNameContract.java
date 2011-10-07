package org.kuali.rice.kew.api.rule;


/**
 * This is an interface to define a Role Name for a role assigned to a RoleAttribute.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RoleNameContract {
    /**
     * This is the composite name value for the Role on an attribute.  It consists of
     * of the roleAttribute's class name + '!' + roleBaseName
     *
     * @return name
     */
    String getName();

    /**
     * This is the base name value for the Role on an attribute.  It consists of
     * of the name of the Role
     *
     * @return baseName
     */
    String getBaseName();

    /**
     * This is the return URL for the given Role for a role attribute
     *
     * @return returnUrl
     */
    String getReturnUrl();

    /**
     * A label for the Role on an attribute.
     *
     * @return label
     */
    String getLabel();
}
