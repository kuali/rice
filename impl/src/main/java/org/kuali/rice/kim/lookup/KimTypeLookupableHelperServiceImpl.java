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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimGroupTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.struts.form.LookupForm;
import org.springframework.remoting.RemoteAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		List<KimTypeBo> searchResults = (List<KimTypeBo>)super.getSearchResultsHelper(fieldValues, unbounded);
		List<KimTypeBo> filteredSearchResults = new ArrayList<KimTypeBo>();
		if(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KRADConstants.DOC_FORM_KEY))) {
			for(KimTypeBo kimTypeBo: searchResults){
				if(hasRoleTypeService(KimTypeBo.to(kimTypeBo))) {
					filteredSearchResults.add(kimTypeBo);
				}
			}
			return filteredSearchResults;
		}
		
		if(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KRADConstants.DOC_FORM_KEY))) {
			for(KimTypeBo kimTypeBo: searchResults){
				if(hasGroupTypeService(KimTypeBo.to(kimTypeBo))) {
					filteredSearchResults.add(kimTypeBo);
				}
			}
			return filteredSearchResults;
		}
		return searchResults;
	}
	
	/**
     * @see org.kuali.rice.krad.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.krad.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
    	KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(parameters.getProperty(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID));
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
    			( StringUtils.isBlank( kimType.getServiceName() )
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
    			parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
    			parameters.put(KRADConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
    			parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, docTypeName);
    	        if (StringUtils.isNotBlank(getReturnLocation())) {
    	        	parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
    			}
    		}
	    	href = UrlFactory.parameterizeUrl(KimCommonUtilsInternal.getKimBasePath()+docTypeAction, parameters);
    	}
        return href;
    }

	static boolean hasRoleTypeService(KimType kimType){
		return (kimType!=null && kimType.getServiceName()==null) ||
					(KIMServiceLocatorWeb.getKimTypeService(kimType) instanceof KimRoleTypeService);
	}

	static boolean hasRoleTypeService(KimType kimType, KimTypeService kimTypeService){
		return (kimType!=null && kimType.getServiceName()==null) ||
					(kimTypeService instanceof KimRoleTypeService);
	}
	
    static boolean hasGroupTypeService(KimType kimType){
		return (kimType!=null && kimType.getServiceName()==null) ||
					(KIMServiceLocatorWeb.getKimTypeService(kimType) instanceof KimGroupTypeService);
    }

	public static boolean hasDerivedRoleTypeService(KimType kimType){
		boolean hasDerivedRoleTypeService = false;
		KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(kimType);
		//it is possible that the the roleTypeService is coming from a remote application 
	    // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
		try {
		    if(hasRoleTypeService(kimType, kimTypeService))
		        hasDerivedRoleTypeService = (kimType.getServiceName()!=null && ((KimRoleTypeService)kimTypeService).isApplicationRoleType());
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
