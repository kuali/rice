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
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - bhargavp don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class KimCommonUtilsInternal {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimCommonUtilsInternal.class);

    private static IdentityManagementService identityManagementService;

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
		String kimBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KimConstants.KimUIConstants.KIM_URL_KEY);
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
		return getIdentityManagementService().isAuthorized(
				GlobalVariables.getUserSession().getPrincipalId(),
				KimConstants.NAMESPACE_CODE,
				KimConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
				null,
				new AttributeSet(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId) );
	}

	public static boolean isSuppressName(String entityId) {
	    KimEntityPrivacyPreferences privacy = null;
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
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
        KimEntityPrivacyPreferences privacy = null;
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
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
        KimEntityPrivacyPreferences privacy = null;
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
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
        KimEntityPrivacyPreferences privacy = null;
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
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
        KimEntityPrivacyPreferences privacy = null;
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
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

	private static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KimApiServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}


    public static GroupImpl copyInfoToGroup(GroupInfo info, GroupImpl group) {
        group.setActive(info.isActive());
        group.setGroupDescription(info.getGroupDescription());
        group.setGroupId(info.getGroupId());
        group.setGroupName(info.getGroupName());
        group.setKimTypeId(info.getKimTypeId());
        group.setNamespaceCode(info.getNamespaceCode());

        return group;
    }

    /**
     *
     * @param infoMap Containing the Info Attribute objects.
     * @param groupId for the group of attributes
     * @param kimTypeId for the group of attributes
     * @return a list of group attributes
     */

    public static List<GroupAttributeDataImpl> copyInfoAttributesToGroupAttributes(Map<String, String> infoMap, String groupId, String kimTypeId) {
        List<GroupAttributeDataImpl> attrList = new ArrayList<GroupAttributeDataImpl>(infoMap.size());
        List<KimTypeAttribute> attributeInfoList = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitions();

        for (String key : infoMap.keySet()) {
            KimTypeAttribute typeAttributeInfo = getAttributeInfo(attributeInfoList, key);

            if (typeAttributeInfo != null) {
                GroupAttributeDataImpl groupAttribute = new GroupAttributeDataImpl();
                groupAttribute.setKimAttributeId(typeAttributeInfo.getKimAttribute().getId());
                groupAttribute.setAttributeValue(infoMap.get(typeAttributeInfo.getKimAttribute().getAttributeName()));
                groupAttribute.setGroupId(groupId);
                groupAttribute.setKimTypeId(kimTypeId);
                attrList.add(groupAttribute);
            } else {
                throw new IllegalArgumentException("KimAttribute not found: " + key);
            }
        }
        return attrList;
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
