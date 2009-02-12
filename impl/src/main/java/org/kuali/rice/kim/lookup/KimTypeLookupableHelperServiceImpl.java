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

import java.util.List;
import java.util.Properties;

import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
    	//conversionFields=kimTypeId:document.kimType.kimTypeId,name:document.kimType.name&

    	parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
    	parameters.put(KNSConstants.PARAMETER_COMMAND, "initiate");
    	parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, "IdentityManagementRoleDocument");
    	String href = UrlFactory.parameterizeUrl("../kim/identityManagementRoleDocument.do", parameters);
        return href;
        //"../kim/identityManagementRoleDocument.do?methodToCall=docHandler&amp;command=initiate&amp;docTypeName=IdentityManagementRoleDocument";
    	//return addToReturnHref(href, lookupForm);
    }

}