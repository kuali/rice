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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.KimType;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimGroupTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.springframework.remoting.RemoteAccessException;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		List<KimTypeImpl> searchResults = (List<KimTypeImpl>)super.getSearchResultsHelper(fieldValues, unbounded);
		List<KimTypeImpl> filteredSearchResults = new ArrayList<KimTypeImpl>();
		if(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KNSConstants.DOC_FORM_KEY))) {
			for(KimTypeImpl kimTypeImpl: searchResults){
				if(hasRoleTypeService(kimTypeImpl.toInfo())) {
					filteredSearchResults.add(kimTypeImpl);
				}
			}
			return filteredSearchResults;
		}
		
		if(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KNSConstants.DOC_FORM_KEY))) {
			for(KimTypeImpl kimTypeImpl: searchResults){
				if(hasGroupTypeService(kimTypeImpl.toInfo())) {
					filteredSearchResults.add(kimTypeImpl);
				}
			}
			return filteredSearchResults;
		}
		return searchResults;
	}
	
	/**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
    	KimTypeInfo kimType = KIMServiceLocatorWeb.getTypeInfoService().getKimType(parameters.getProperty(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID));
    	String href = "";
    	//TODO: clean this up.
    	boolean showReturnHref = true;
    	boolean refreshMe = false;
    	String docTypeName = "";
    	String docTypeAction = "";
    	if(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY.equals(lookupForm.getFormKey())){
    		docTypeName = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
    		// show the create link when there is a type service AND
    		//    it has no service (default assignable role)
    		// OR it is not an application role
    		showReturnHref = kimType!=null && 
    			( StringUtils.isBlank( kimType.getKimTypeServiceName() )
    					|| (KIMServiceLocatorWeb.getKimTypeService(kimType) instanceof KimRoleTypeService
    						&&!((KimRoleTypeService) KIMServiceLocatorWeb.getKimTypeService(kimType)).isApplicationRoleType() )
    					);
    	} else{
    		docTypeName = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION;
//    		showReturnHref = kimType!=null;
    		refreshMe = true;
    	}
    	if(showReturnHref){
    		if (!refreshMe){
    			parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
    			parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
    			parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, docTypeName);
    	        if (StringUtils.isNotBlank(getReturnLocation())) {
    	        	parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());	 
    			}
    		}
	    	href = UrlFactory.parameterizeUrl(KimCommonUtilsInternal.getKimBasePath()+docTypeAction, parameters);
    	}
        return href;
    }

	static boolean hasRoleTypeService(KimType kimType){
		return (kimType!=null && kimType.getKimTypeServiceName()==null) || 
					(KIMServiceLocatorWeb.getKimTypeService(kimType) instanceof KimRoleTypeService);
	}

	static boolean hasRoleTypeService(KimType kimType, KimTypeService kimTypeService){
		return (kimType!=null && kimType.getKimTypeServiceName()==null) || 
					(kimTypeService instanceof KimRoleTypeService);
	}
	
    static boolean hasGroupTypeService(KimType kimType){
		return (kimType!=null && kimType.getKimTypeServiceName()==null) || 
					(KIMServiceLocatorWeb.getKimTypeService(kimType) instanceof KimGroupTypeService);
    }

	public static boolean hasDerivedRoleTypeService(KimType kimType){
		boolean hasDerivedRoleTypeService = false;
		KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(kimType);
		//it is possible that the the roleTypeService is coming from a remote application 
	    // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
		try {
		    if(hasRoleTypeService(kimType, kimTypeService))
		        hasDerivedRoleTypeService = (kimType.getKimTypeServiceName()!=null && ((KimRoleTypeService)kimTypeService).isApplicationRoleType());
		} catch (RiceRemoteServiceConnectionException ex) {
			LOG.warn("Not able to retrieve KimTypeService from remote system for KIM Type: " + kimType.getName(), ex);
		    return hasDerivedRoleTypeService;
		}
		// KULRICE-4403: catch org.springframework.remoting.RemoteAccessException
		catch (RemoteAccessException rae) {
			LOG.warn("Not able to retrieve KimTypeService from remote system for KIM Type: " + kimType.getName(), rae);
			return hasDerivedRoleTypeService;
		}
		return hasDerivedRoleTypeService;
	}

}
