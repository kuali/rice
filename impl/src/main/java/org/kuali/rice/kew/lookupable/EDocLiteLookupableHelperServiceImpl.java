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

import java.util.Iterator;
import java.util.*;
import java.util.Map;

import org.kuali.rice.kew.edl.bo.EDocLiteAssociation;
import org.kuali.rice.kew.edl.UserAction;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

/**
 * This is a description of what this class does - sp20369 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

public class EDocLiteLookupableHelperServiceImpl  extends KualiLookupableHelperServiceImpl {
    	
    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     *
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
  
    public List<String> getCustomActionUrls(BusinessObject businessObject, Map fieldValues) {
    	System.out.println("Inside EDocLiteLookupableHelperServiceImpl++++++++");
        EDocLiteAssociation edocLite = (EDocLiteAssociation) businessObject;
        String actionsUrl = "<a href=\"EDocLite?userAction=" + UserAction.ACTION_CREATE + "&edlName=" + edocLite.getEdlName() + "\">Create Document</a>";
        List<String> actionUrls = new ArrayList<String>(); 
        actionUrls.add(actionsUrl); 
        return actionUrls;
    }
}
