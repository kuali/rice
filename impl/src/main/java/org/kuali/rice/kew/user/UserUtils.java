/*
 * Copyright 2007-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.user;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Provides some utility methods for translating user ID types.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UserUtils.class);

	private static PersonService<Person> personService;
	private static IdentityManagementService identityManagementService;
	
	private static final String RESTRICTED_DATA_MASK = "xxxxxx";
	
	public static String getIdValue(String idType, Person user) {
	    if ("workflowId".equalsIgnoreCase(idType) || "w".equalsIgnoreCase(idType) || "principalId".equalsIgnoreCase(idType)) {
	      return user.getPrincipalId();
	    } else if ("authenticationId".equalsIgnoreCase(idType) || "a".equalsIgnoreCase(idType) || "principalName".equalsIgnoreCase(idType)) {
	      return user.getPrincipalName();
	    } else if ("emplId".equalsIgnoreCase(idType) || "e".equalsIgnoreCase(idType)) {
	      return user.getEmployeeId();
	    } else {
	      LOG.error("Could not determine ID Value for given id type!" + idType);
	    }
	    return null;
	  }

	//public static String getDisplayableName(UserSession userSession, String principalId) {
	//	return getDisplayableName(userSession, KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId));
	//}
	
	/*
     * @deprecated  Person getEmailName method accounts for privacy.
     */
    //@Deprecated
	//public static String getDisplayableName(UserSession userSession, KimPrincipal principal) {
	//	if (userSession != null && !userSession.getPrincipalId().equals(principal.getPrincipalId()) && isEntityNameRestricted(principal.getEntityId())) {
	//		return RESTRICTED_DATA_MASK;
	//	}
	//	Person person = getPersonService().getPerson(principal.getPrincipalId());
	//	return person.getName();
	//}
	
	public static String getTransposedName(UserSession userSession, KimPrincipal principal) {
		Person person = getPersonService().getPerson(principal.getPrincipalId());
		return person.getName(); //contructTransposedName(person);
	}
	
	//private static String contructTransposedName(Person person) {
	//	return person.getLastName() + (StringUtils.isNotBlank(person.getFirstName())?", " + person.getFirstName():"");
	//}

	/*
	 * @deprecated  Person getEmailAddress method accounts for privacy.
	 */
	//@Deprecated
	//public static String getDisplayableEmailAddress(UserSession userSession, KimPrincipal principal) {
	//	if (userSession != null && !userSession.getPrincipalId().equals(principal.getPrincipalId()) && isEntityEmailRestricted(principal.getEntityId())) {
	//		return RESTRICTED_DATA_MASK;
	//	}
	//	Person person = getPersonService().getPerson(principal.getPrincipalId());
	//	return person.getEmailAddress();
	//}
	
	//public static boolean isEntityNameRestricted(String entityId) {
	//	KimEntityPrivacyPreferences privacy = getIdentityManagementService().getEntityPrivacyPreferences( entityId );
	//	if ( ObjectUtils.isNotNull(privacy) ) {
	//		return privacy.isSuppressName();
	//	}
	//	return false;
	//}

	//public static boolean isEntityEmailRestricted(String entityId) {
	//	KimEntityPrivacyPreferences privacy = getIdentityManagementService().getEntityPrivacyPreferences( entityId );
	//	if (ObjectUtils.isNotNull(privacy) ) {
	//		return privacy.isSuppressEmail();
	//	}
	//	return false;
	//}

	/**
	 * @return the personService
	 */
	public static PersonService<Person> getPersonService() {
		if ( personService == null ) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

	/**
	 * @return the identityManagementService
	 */
	public static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

}
