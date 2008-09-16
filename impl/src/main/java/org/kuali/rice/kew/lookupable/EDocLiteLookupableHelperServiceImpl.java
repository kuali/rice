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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.*;
import org.kuali.rice.kew.edl.UserAction;
import org.kuali.rice.kew.edl.bo.EDocLiteAssociation;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;

/**
 * This is a description of what this class does - sp20369 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EDocLiteLookupableHelperServiceImpl  extends KualiLookupableHelperServiceImpl {

    	private static final String EDOC_LITE_NAME = "eDocLiteName_";
    	private static final String EDOC_LITE_STYLE_NAME = "eDocLiteStyleName_";
    	private static final String EDOC_LITE_DOC_TYPE_NAME = "eDocLiteDocName_";
    	private static final String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
    	private static final String ACTIVE_IND_PROPERTY_NAME = "activeIndicator";

    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     *
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
  
    public List<String> getCustomActionUrls(BusinessObject businessObject, Map fieldValues) {
        EDocLiteAssociation edocLite = (EDocLiteAssociation) businessObject;
        String activeInd = (String)fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
		if (!Utilities.isEmpty(activeInd) && !activeInd.equals("all")) {
			edocLite.setActiveInd(new Boolean(activeInd.trim()));
		}
		String definition = (String)fieldValues.get(EDOC_LITE_NAME);
		if (!Utilities.isEmpty(definition)) {
			edocLite.setDefinition(definition.trim());
		}
		String style = (String)fieldValues.get(EDOC_LITE_STYLE_NAME);
		if (!Utilities.isEmpty(style)) {
			edocLite.setStyle(style.trim());
		}
		String documentType = (String)fieldValues.get(EDOC_LITE_DOC_TYPE_NAME);
		if (!Utilities.isEmpty(documentType)) {
			edocLite.setEdlName(documentType.trim());
		}
		List results = KEWServiceLocator.getEDocLiteService().search(edocLite);;
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			EDocLiteAssociation result = (EDocLiteAssociation) iter.next();
			String actionsUrl = "<a href=\"EDocLite?userAction=" + UserAction.ACTION_CREATE + "&edlName=" + result.getEdlName() + "\">Create Document</a>";
			result.setActionsUrl(actionsUrl);
		}		
        return results;
    }

}
