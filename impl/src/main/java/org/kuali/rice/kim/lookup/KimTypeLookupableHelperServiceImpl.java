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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.support.KimGroupTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		List<KimTypeImpl> searchResults = (List<KimTypeImpl>)super.getSearchResultsHelper(fieldValues, unbounded);
		List<KimTypeImpl> filteredSearchResults = new ArrayList<KimTypeImpl>();
		if(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KNSConstants.DOC_FORM_KEY)))
			for(KimTypeImpl kimTypeImpl: searchResults){
				if(hasRoleTypeService(kimTypeImpl)) {
					filteredSearchResults.add(kimTypeImpl);
				}
			}
		
		if(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_SHORT_KEY.equals(fieldValues.get(KNSConstants.DOC_FORM_KEY)))
			for(KimTypeImpl kimTypeImpl: searchResults){
				if(hasGroupTypeService(kimTypeImpl)) {
					filteredSearchResults.add(kimTypeImpl);
				}
			}

		return filteredSearchResults;
	}
	
	/**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, parameters.getProperty(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID));
    	KimTypeImpl kimTypeImpl = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
    	String href = "";
    	//TODO: clean this up.
    	boolean showReturnHref = true;
    	String docTypeName = "";
    	String docTypeAction = "";
    	if(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY.equals(lookupForm.getFormKey())){
    		docTypeName = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
    		// show the create link when there is a type service AND
    		//    it has no service (default assignable role)
    		// OR it is not an application role
    		showReturnHref = kimTypeImpl!=null && 
    			( StringUtils.isBlank( kimTypeImpl.getKimTypeServiceName() )
    					|| (KimCommonUtils.getKimTypeService(kimTypeImpl) instanceof KimRoleTypeService
    						&&!((KimRoleTypeService)KimCommonUtils.getKimTypeService(kimTypeImpl)).isApplicationRoleType() )
    					);
    	} else{
    		docTypeName = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_TYPE_NAME;
    		docTypeAction = KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION;
    		showReturnHref = kimTypeImpl!=null;
    	}
    	if(showReturnHref){
	    	parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
	    	parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
	    	parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, docTypeName);
	    	href = UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+docTypeAction, parameters);
    	}
        return href;
    }

	static boolean hasRoleTypeService(KimTypeImpl kimTypeImpl){
		return (kimTypeImpl!=null && kimTypeImpl.getKimTypeServiceName()==null) || 
					(KimCommonUtils.getKimTypeService(kimTypeImpl) instanceof KimRoleTypeService);
	}

	static boolean hasRoleTypeService(KimTypeImpl kimTypeImpl, KimTypeService kimTypeService){
		return (kimTypeImpl!=null && kimTypeImpl.getKimTypeServiceName()==null) || 
					(kimTypeService instanceof KimRoleTypeService);
	}
	
    static boolean hasGroupTypeService(KimTypeImpl kimTypeImpl){
		return (kimTypeImpl!=null && kimTypeImpl.getKimTypeServiceName()==null) || 
					(KimCommonUtils.getKimTypeService(kimTypeImpl) instanceof KimGroupTypeService);
    }

	public static boolean hasDerivedRoleTypeService(KimTypeImpl kimTypeImpl){
		boolean hasDerivedRoleTypeService = false;
		KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimTypeImpl);
		if(hasRoleTypeService(kimTypeImpl, kimTypeService))
			hasDerivedRoleTypeService = (kimTypeImpl.getKimTypeServiceName()!=null && ((KimRoleTypeService)kimTypeService).isApplicationRoleType());
		return hasDerivedRoleTypeService;
	}

}