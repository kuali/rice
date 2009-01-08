package org.kuali.rice.kew.user;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.service.KIMServiceLocator;

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

	public static String getTransposedName(UserSession userSession, Person user) {
		if (userSession != null && !userSession.getPrincipalId().equals(user.getPrincipalId())) {
			if(isPersonNameRestricted(user)){
				return "xxxxxx";
			}
		}
		return getTransposedName(user);
	}

	public static boolean isPersonNameRestricted(Person user){
		boolean bRet = false;

		KimEntity entity = KIMServiceLocator.getIdentityService().getEntity(user.getEntityId());

		bRet = entity.getPrivacyPreferences().isSuppressName();

		return bRet;
	}

	public static String getTransposedName(Person user) {
        return user.getLastName() + ", " + user.getFirstName();
    }

	public static String getDisplayableEmailAddress(UserSession userSession, WorkflowUser user) {
		if (!userSession.getWorkflowUser().getWorkflowId().equals(user.getWorkflowId())) {
			return user.getEmailAddressSafe();
		}
		return user.getEmailAddress();
	}

}
