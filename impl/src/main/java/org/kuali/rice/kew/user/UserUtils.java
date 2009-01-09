package org.kuali.rice.kew.user;

import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.EntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * Provides some utility methods for translating user ID types.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UserUtils.class);

	private static final String RESTRICTED_DATA_MASK = "xxxxxx";
	
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

	public static String getIdValue(String idType, Person user) {
	    if ("workflowId".equalsIgnoreCase(idType) || "w".equalsIgnoreCase(idType)) {
	      return user.getPrincipalId();
	    } else if ("authenticationId".equalsIgnoreCase(idType) || "a".equalsIgnoreCase(idType)) {
	      return user.getPrincipalName();
	    } else {
	      LOG.error("Could not determine ID Value for given id type!" + idType);
	    }
	    return null;
	  }

	public static String getDisplayableName(UserSession userSession, String principalId) {
		return getDisplayableName(userSession, KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId));
	}
	
	public static String getDisplayableName(UserSession userSession, KimPrincipal principal) {
		if (userSession != null && userSession.getPrincipalId().equals(principal.getPrincipalId()) && isEntityNameRestricted(principal.getEntityId())) {
			return RESTRICTED_DATA_MASK;
		}
		Person person = KIMServiceLocator.getPersonService().getPerson(principal.getPrincipalId());
		return person.getName();
	}
	
	public static String getTransposedName(UserSession userSession, KimPrincipal principal) {
		if (userSession != null && userSession.getPrincipalId().equals(principal.getPrincipalId()) && isEntityNameRestricted(principal.getEntityId())) {
			return RESTRICTED_DATA_MASK;
		}
		Person person = KIMServiceLocator.getPersonService().getPerson(principal.getPrincipalId());
		return contructTransposedName(person);
	}
	
	private static String contructTransposedName(Person person) {
		return person.getLastName() + ", " + person.getFirstName();
	}

	public static String getDisplayableEmailAddress(UserSession userSession, KimPrincipal principal) {
		if (userSession != null && userSession.getPrincipalId().equals(principal.getPrincipalId()) && isEntityEmailRestricted(principal.getEntityId())) {
			return RESTRICTED_DATA_MASK;
		}
		Person person = KIMServiceLocator.getPersonService().getPerson(principal.getPrincipalId());
		return person.getEmailAddress();
	}
	
	public static boolean isEntityNameRestricted(String entityId) {
		KimEntity entity = KIMServiceLocator.getIdentityManagementService().getEntity(entityId);
		EntityPrivacyPreferences privacy = entity.getPrivacyPreferences();
		if (privacy != null) {
			return entity.getPrivacyPreferences().isSuppressName();
		}
		return false;
	}

	public static boolean isEntityEmailRestricted(String entityId) {
		KimEntity entity = KIMServiceLocator.getIdentityManagementService().getEntity(entityId);
		EntityPrivacyPreferences privacy = entity.getPrivacyPreferences();
		if (privacy != null) {
			return entity.getPrivacyPreferences().isSuppressEmail();
		}
		return false;
	}

}
