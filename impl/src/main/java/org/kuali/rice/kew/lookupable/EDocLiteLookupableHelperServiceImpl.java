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
package org.kuali.rice.kew.lookupable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.edl.bo.EDocLiteAssociation;
import org.kuali.rice.kew.edl.UserAction;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * This is a description of what this class does - sp20369 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

public class EDocLiteLookupableHelperServiceImpl  extends AbstractLookupableHelperServiceImpl{ //KualiLookupableHelperServiceImpl {
    	
    /**
     * 
     * @returns links to action for the current edoclite
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
  
    public String getActionUrls(BusinessObject businessObject) {//    	System.out.println("Inside EDocLiteLookupableHelperServiceImpl++++++++");
        EDocLiteAssociation edocLite = (EDocLiteAssociation) businessObject;
        String actionsUrl = "<a href=\"../kr-dev/en/EDocLite?userAction=" + UserAction.ACTION_CREATE + "&edlName=" + edocLite.getEdlName() + "\">Create Document</a>";
        return actionsUrl;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	public List<? extends BusinessObject> getSearchResults(
			Map<String, String> fieldValues) {
		boolean unbounded=true;	//System.out.println("BackLocation==="+KNSConstants.BACK_LOCATION+"=="+fieldValues.get(KNSConstants.BACK_LOCATION)+"=="+fieldValues.values());
		setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
        List searchResults;
        if (UniversalUser.class.equals(getBusinessObjectClass())) {
            searchResults = (List) getUniversalUserService().findUniversalUsers(fieldValues);
        }
        else if (getUniversalUserService().hasUniversalUserProperty(getBusinessObjectClass(), fieldValues)) {
            // TODO WARNING: this does not support nested joins, because i don't have a test case
            searchResults = (List) getUniversalUserService().findWithUniversalUserJoin(getBusinessObjectClass(), fieldValues, unbounded);
        }
        else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }	//  System.out.println("Inside getSearchResults--------------------");
        return searchResults;

		
	}

	/**
	 * Since we don't have a maintenance document for EDocLiteAssociations, we need to
	 * set showMaintenanceLinks to true manually.  Otherwise our "Create Document" link
	 * won't show up.
	 */
	@Override
	public Collection performLookup(LookupForm lookupForm,
			Collection resultTable, boolean bounded) {
		lookupForm.setShowMaintenanceLinks(true);
		return super.performLookup(lookupForm, resultTable, bounded);
	}
	
	
}
