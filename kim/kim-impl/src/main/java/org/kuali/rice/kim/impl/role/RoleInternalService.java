package org.kuali.rice.kim.impl.role;

public interface RoleInternalService {
    /**
	 * Notifies all of a principal's roles and role types that the principal has been inactivated.
	 */
	void principalInactivated(String principalId ) throws IllegalArgumentException;

	/**
	 * Notifies the role service that the role with the given id has been inactivated.
	 */
	void roleInactivated(String roleId) throws IllegalArgumentException;

	/**
	 * Notifies the role service that the group with the given id has been inactivated.
	 */
    void groupInactivated(String groupId) throws IllegalArgumentException;
}
