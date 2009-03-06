/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.util;

import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;

/**
 * This is a description of what this class does - bhargavp don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class KimCommonUtils {
	private static KualiModuleService kualiModuleService;

	private static KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KNSServiceLocator.getKualiModuleService();
		}
		return kualiModuleService;
	}

	public static String getClosestParentDocumentTypeName(
			DocumentType documentType,
			Set<String> potentialParentDocumentTypeNames) {
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
			return "KUALI";
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

	public static final KimTypeService getKimTypeService(KimTypeImpl kimTypeImpl){
		String serviceName = KimCommonUtils.getKimTypeServiceName(kimTypeImpl.getKimTypeServiceName());
		return (KimTypeService)KIMServiceLocator.getService(serviceName);
	}

	public static void copyProperties(Object targetToCopyTo, Object sourceToCopyFrom){
		try{
			PropertyUtils.copyProperties(targetToCopyTo, sourceToCopyFrom);
		} catch(Exception ex){
			throw new RuntimeException("Failed to copy from source object: "+sourceToCopyFrom.getClass()+" to target object: "+targetToCopyTo);
		}
	}

	public static String getKimBasePath(){
		String kimBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KimConstants.KimUIConstants.KIM_URL_KEY);
		if (!kimBaseUrl.endsWith("/")) {
			kimBaseUrl = kimBaseUrl + "/";
		}
		return kimBaseUrl;
	}

}
