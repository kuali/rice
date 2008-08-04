package edu.iu.uis.eden.user;

import edu.iu.uis.eden.web.session.UserSession;

/**
 * Provides some utility methods for translating user ID types.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UserUtils.class);

	public static String getIdValue(String idType, WorkflowUser user) {
	    if ("emplId".equalsIgnoreCase(idType) || "e".equalsIgnoreCase(idType)) {
	      return user.getEmplId().getId();
	    } else if ("workflowId".equalsIgnoreCase(idType) || "w".equalsIgnoreCase(idType)) {
	      return user.getWorkflowUserId().getId();
	    } else if ("uuId".equalsIgnoreCase(idType) || "u".equalsIgnoreCase(idType)) {
	      return user.getUuId().getId();
	    } else if ("authenticationId".equalsIgnoreCase(idType) || "a".equalsIgnoreCase(idType)) {
	      return user.getAuthenticationUserId().getId();
	    } else {
	      LOG.error("Could not determine ID Value for given id type!" + idType);
	    }
	    return null;
	  }

	public static String getDisplayableName(UserSession userSession, WorkflowUser user) {
		if (!userSession.getWorkflowUser().getWorkflowId().equals(user.getWorkflowId())) {
			return user.getDisplayNameSafe();
		}
		return user.getDisplayName();
	}

	public static String getTransposedName(UserSession userSession, WorkflowUser user) {
		if (userSession != null && !userSession.getWorkflowUser().getWorkflowId().equals(user.getWorkflowId())) {
			return user.getTransposedNameSafe();
		}
		return user.getTransposedName();
	}

	public static String getDisplayableEmailAddress(UserSession userSession, WorkflowUser user) {
		if (!userSession.getWorkflowUser().getWorkflowId().equals(user.getWorkflowId())) {
			return user.getEmailAddressSafe();
		}
		return user.getEmailAddress();
	}

}
