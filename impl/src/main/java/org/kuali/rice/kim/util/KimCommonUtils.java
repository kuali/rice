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

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kim.bo.KimType;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.reference.ExternalIdentifierType;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This is a description of what this class does - bhargavp don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KimCommonUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimCommonUtils.class);

    private static KualiModuleService kualiModuleService;
    private static Map<String,KimTypeService> kimTypeServiceCache = new HashMap<String,KimTypeService>();
    private static IdentityManagementService identityManagementService;

	private KimCommonUtils() {}
    
	private static KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KNSServiceLocator.getKualiModuleService();
		}
		return kualiModuleService;
	}

	public static String getClosestParentDocumentTypeName(
			DocumentType documentType,
			Set<String> potentialParentDocumentTypeNames) {
		if ( potentialParentDocumentTypeNames == null || documentType == null ) {
			return null;
		}
		if (potentialParentDocumentTypeNames.contains(documentType.getName())) {
			return documentType.getName();
		} else {
			if ((documentType.getDocTypeParentId() == null)
					|| documentType.getDocTypeParentId().equals(
							documentType.getDocumentTypeId())) {
				return null;
			} else {
				return getClosestParentDocumentTypeName(documentType
						.getParentDocType(), potentialParentDocumentTypeNames);
			}
		}
	}
	
	public static boolean storedValueNotSpecifiedOrInputValueMatches(AttributeSet storedValues, AttributeSet inputValues, String attributeName) {
		return ((storedValues == null) || (inputValues == null)) || !storedValues.containsKey(attributeName) || storedValues.get(attributeName).equals(inputValues.get(attributeName));
	}

	public static boolean doesPropertyNameMatch(
			String requestedDetailsPropertyName,
			String permissionDetailsPropertyName) {
		if (StringUtils.isBlank(permissionDetailsPropertyName)) {
			return true;
		}
		if ( requestedDetailsPropertyName == null ) {
		    requestedDetailsPropertyName = ""; // prevent NPE
		}
		return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
				|| (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName+".")); 
	}

	public static AttributeSet getNamespaceAndComponentSimpleName( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KimAttributes.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KimAttributes.COMPONENT_NAME, getComponentSimpleName(clazz));
		return attributeSet;
	}

	public static AttributeSet getNamespaceAndComponentFullName( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KimAttributes.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KimAttributes.COMPONENT_NAME, getComponentFullName(clazz));
		return attributeSet;
	}
	
	public static AttributeSet getNamespaceAndActionClass( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KimAttributes.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KimAttributes.ACTION_CLASS, clazz.getName());
		return attributeSet;
	}

	public static String getNamespaceCode(Class<? extends Object> clazz) {
		ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
		if (moduleService == null) {
			return KimConstants.KIM_TYPE_DEFAULT_NAMESPACE;
		}
		return moduleService.getModuleConfiguration().getNamespaceCode();
	}

	public static String getComponentSimpleName(Class<? extends Object> clazz) {
		return clazz.getSimpleName();
	}

	public static String getComponentFullName(Class<? extends Object> clazz) {
		return clazz.getName();
	}
	
	public static boolean isAttributeSetEntryEquals( AttributeSet map1, AttributeSet map2, String key ) {
		return StringUtils.equals( map1.get( key ), map2.get( key ) );
	}
	
	public static final String DEFAULT_KIM_SERVICE_NAME = "kimTypeService";
	
	public static String getKimTypeServiceName(String kimTypeServiceName){
    	if (StringUtils.isBlank(kimTypeServiceName)) {
    		kimTypeServiceName = DEFAULT_KIM_SERVICE_NAME;
    	}
    	return kimTypeServiceName;
	}

	public static KimTypeService getKimTypeService(KimType kimType){
		if( kimType == null ) {
			LOG.warn( "null KimType passed into getKimTypeService" );
			return null;
		}
		return getKimTypeService( KimCommonUtils.getKimTypeServiceName(kimType.getKimTypeServiceName() ) );
	}
	
	public static KimTypeService getKimTypeService( String serviceName ) {
		KimTypeService service = null;
		if ( StringUtils.isNotBlank(serviceName) ) {
	    	service = kimTypeServiceCache.get( serviceName );
	    	if ( service == null && !kimTypeServiceCache.containsKey( serviceName ) ) {
    			try {
    				service = (KimTypeService)KIMServiceLocator.getService( serviceName );
    			} catch ( Exception ex ) {
    				LOG.error( "Unable to find KIM type service with name: " + serviceName, ex );
    				service = null;
    			}
    		}
	    	if (service != null) {
		    	synchronized (kimTypeServiceCache) {
					kimTypeServiceCache.put(serviceName, service);
				}
	    	}
    	} else {
    		LOG.warn( "Blank service name passed into getKimTypeService" );
    	}
    	return service;
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
    	if(path.indexOf(kimActionName)!=-1 && path.indexOf(kimContext+kimActionName)==-1
    			&& path.indexOf(kimContextParameterized+kimActionName)==-1)
    		path = path.replace(kimActionName, kimContext+kimActionName);
    	return path;
	}
	
	public static String stripEnd(String toStripFrom, String toStrip){
		String stripped;
		if(toStripFrom==null) stripped = null;
		if(toStrip==null) stripped = toStripFrom;
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
				new AttributeSet(KimAttributes.PRINCIPAL_ID, principalId) );
	}
	
	public static boolean isSuppressName(String entityId) {
	    KimEntityPrivacyPreferences privacy = null; 
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        }
	    UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressName = false;
        if (privacy != null) {
            suppressName = privacy.isSuppressName();
        } 
        if (	   suppressName
        		&& userSession != null 
                && !StringUtils.equals(userSession.getPerson().getEntityId(),entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId())) {
            return true;
        }
        return false;
	}
  
    public static boolean isSuppressEmail(String entityId) {
        KimEntityPrivacyPreferences privacy = null; 
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressEmail = false;
        if (privacy != null) {
            suppressEmail = privacy.isSuppressEmail();
        } 
        if (	   suppressEmail
        		&& userSession != null 
                && !StringUtils.equals(userSession.getPerson().getEntityId(),entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId())) {
            return true;
        }
        return false;
    }
   
    public static boolean isSuppressAddress(String entityId) {
        KimEntityPrivacyPreferences privacy = null; 
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressAddress = false;
        if (privacy != null) {
            suppressAddress = privacy.isSuppressAddress();
        } 
        if (	   suppressAddress
    			&& userSession != null 
                && !StringUtils.equals(userSession.getPerson().getEntityId(),entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId())) {
            return true;
        }
        return false;
    }
   
    public static boolean isSuppressPhone(String entityId) {
        KimEntityPrivacyPreferences privacy = null; 
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressPhone = false;
        if (privacy != null) {
            suppressPhone = privacy.isSuppressPhone();
        } 
        if (	   suppressPhone
        		&& userSession != null 
                && !StringUtils.equals(userSession.getPerson().getEntityId(),entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId())) {
            return true;
        }
        return false;
    }
    
    public static boolean isSuppressPersonal(String entityId) {
        KimEntityPrivacyPreferences privacy = null; 
        KimEntityDefaultInfo entityInfo = getIdentityManagementService().getEntityDefaultInfo(entityId);
        if (entityInfo != null) {
            privacy = entityInfo.getPrivacyPreferences();
        }
        UserSession userSession = GlobalVariables.getUserSession();

        boolean suppressPersonal = false;
        if (privacy != null) {
            suppressPersonal = privacy.isSuppressPersonal();
        } 
        if (	   suppressPersonal
        		&& userSession != null 
                && !StringUtils.equals(userSession.getPerson().getEntityId(),entityId)
                && !canOverrideEntityPrivacyPreferences(entityInfo.getPrincipals().get(0).getPrincipalId())) {
            return true;
        }
        return false;
    }

	public static String encryptExternalIdentifier(String externalIdentifier, String externalIdentifierType){
		Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE, externalIdentifierType);
	    ExternalIdentifierType externalIdentifierTypeObject = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierTypeObject!= null && externalIdentifierTypeObject.isEncryptionRequired()){
			if(StringUtils.isNotEmpty(externalIdentifier)){
				try{
					return KNSServiceLocator.getEncryptionService().encrypt(externalIdentifier);
				}catch (GeneralSecurityException e) {
		            LOG.info("Unable to encrypt value : " + e.getMessage() + " or it is already encrypted");
		        }
			}
		}
		return externalIdentifier;
    }
    
    public static String decryptExternalIdentifier(String externalIdentifier, String externalIdentifierType){
        Map<String, String> criteria = new HashMap<String, String>();
	    criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE, externalIdentifierType);
	    ExternalIdentifierType externalIdentifierTypeObject = (ExternalIdentifierType) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, criteria);
		if( externalIdentifierTypeObject!= null && externalIdentifierTypeObject.isEncryptionRequired()){
			if(StringUtils.isNotEmpty(externalIdentifier)){
				try{
					return KNSServiceLocator.getEncryptionService().decrypt(externalIdentifier);
				}catch (GeneralSecurityException e) {
		            LOG.info("Unable to decrypt value : " + e.getMessage() + " or it is already decrypted");
		        }
			}
		}
		return externalIdentifier;
    }

	public static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}
    
}
