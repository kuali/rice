package org.kuali.rice.krms.impl.authorization;

/**
 * This class contains the authorization for KRMS Agendas
 */
public interface AgendaAuthorizationService {
    /**
     * Checks if the current user is authorized to perform the action on an agenda in the specified context.
     *
     * When the contextId is unknown (i.e. at initiation of a new Agenda document) then null should be passed as
     * the contextId. On save the authorization needs to be checked again with the contextId.  The business rules
     * should have taken care of this.
     *
     * @param permissionName
     * @param contextId
     * @return true if current user is authorized, false otherwise
     */
    public boolean isAuthorized(String permissionName, String contextId);
}
