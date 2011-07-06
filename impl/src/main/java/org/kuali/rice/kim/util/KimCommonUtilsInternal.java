/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.IdentityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.List;

/**
 * This is a description of what this class does - bhargavp don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class KimCommonUtilsInternal {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimCommonUtilsInternal.class);

    private static IdentityService identityService;
    private static PermissionService permissionService;

	private KimCommonUtilsInternal() {
		throw new UnsupportedOperationException("do not call");
	}

    public static void copyProperties(Object targetToCopyTo, Object sourceToCopyFrom){
		if(targetToCopyTo!=null && sourceToCopyFrom!=null)
		try{
			PropertyUtils.copyProperties(targetToCopyTo, sourceToCopyFrom);
		} catch(Exception ex){
			throw new RuntimeException("Failed to copy from source object: "+sourceToCopyFrom.getClass()+" to target object: "+targetToCopyTo,ex);
		}
	}

	public static String getKimBasePath(){
		String kimBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyString(KimConstants.KimUIConstants.KIM_URL_KEY);
		if (!kimBaseUrl.endsWith(KimConstants.KimUIConstants.URL_SEPARATOR)) {
			kimBaseUrl = kimBaseUrl + KimConstants.KimUIConstants.URL_SEPARATOR;
		}
		return kimBaseUrl;
	}

	public static String getPathWithKimContext(String path, String kimActionName){
		String kimContext = KimConstants.KimUIConstants.KIM_APPLICATION+KimConstants.KimUIConstants.URL_SEPARATOR;
		String kimContextParameterized = KimConstants.KimUIConstants.KIM_APPLICATION+KimConstants.KimUIConstants.PARAMETERIZED_URL_SEPARATOR;
    	if(path.contains(kimActionName) && !path.contains(kimContext + kimActionName)
    			&& !path.contains(kimContextParameterized + kimActionName))
    		path = path.replace(kimActionName, kimContext+kimActionName);
    	return path;
	}

	public static String stripEnd(String toStripFrom, String toStrip){
		String stripped;
		if(toStripFrom==null) return null;
		if(toStrip==null) return toStripFrom;
        if(toStrip.length() > toStripFrom.length()) return toStripFrom;
		if(toStripFrom.endsWith(toStrip)){
			StringBuffer buffer = new StringBuffer(toStripFrom);
			buffer.delete(buffer.length()-toStrip.length(), buffer.length());
			stripped = buffer.toString();
		} else stripped = toStripFrom;
		return stripped;
	}

	protected static boolean canOverrideEntityPrivacyPreferences( String principalId ){
		return getPermissionService().isAuthorized(
				GlobalVariables.getUserSession().getPrincipalId(),
				KimConstants.NAMESPACE_CODE,
				KimConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
				null,
				new AttributeSet(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId) );
	}

	public static boolean isSuppressName(String entityId) {
	    EntityPrivacyPreferences privacy = null;
        EntityDefault entityInfo = getIdentityService().getEntityDefault(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        } else {
        	return true;
        }
	    UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressName = false;
        if (privacy != null) {
            suppressName = privacy.isSuppressName();
        }
        
        return suppressName
                && userSession != null
                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
    }

    public static boolean isSuppressEmail(String entityId) {
        EntityPrivacyPreferences privacy = null;
        EntityDefault entityInfo = getIdentityService().getEntityDefault(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        } else {
        	return true;
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressEmail = false;
        if (privacy != null) {
            suppressEmail = privacy.isSuppressEmail();
        }
        return suppressEmail
                && userSession != null
                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
    }

    public static boolean isSuppressAddress(String entityId) {
        EntityPrivacyPreferences privacy = null;
        EntityDefault entityInfo = getIdentityService().getEntityDefault(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        } else {
        	return false;
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressAddress = false;
        if (privacy != null) {
            suppressAddress = privacy.isSuppressAddress();
        }
        return suppressAddress
                && userSession != null
                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
    }

    public static boolean isSuppressPhone(String entityId) {
        EntityPrivacyPreferences privacy = null;
        EntityDefault entityInfo = getIdentityService().getEntityDefault(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        } else { 
        	return true;
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressPhone = false;
        if (privacy != null) {
            suppressPhone = privacy.isSuppressPhone();
        }
        return suppressPhone
                && userSession != null
                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
    }

    public static boolean isSuppressPersonal(String entityId) {
        EntityPrivacyPreferences privacy = null;
        EntityDefault entityInfo = getIdentityService().getEntityDefault(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        } else { 
        	return true;
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressPersonal = false;
        if (privacy != null) {
            suppressPersonal = privacy.isSuppressPersonal();
        }
        return suppressPersonal
                && userSession != null
                && !StringUtils.equals(userSession.getPerson().getEntityId(), entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId());
    }

	private static IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

    private static PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KimApiServiceLocator.getPermissionService();
		}
		return permissionService;
	}

    private static KimTypeAttribute getAttributeInfo(List<KimTypeAttribute> attributeInfoList, String attributeName) {
        KimTypeAttribute kRet = null;
        for (KimTypeAttribute attributeInfo : attributeInfoList) {
            if (attributeInfo.getKimAttribute().getAttributeName().equals(attributeName)) {
                kRet = attributeInfo;
                break;
            }
        }
        return kRet;
    }

}
