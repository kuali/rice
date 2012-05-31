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

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleLookupableImpl extends KualiLookupableImpl {

    private static final long serialVersionUID = -543816772634893348L;

    @Override
    public String getCreateNewUrl() {
        String url = "";
        if((getLookupableHelperService()).allowsNewOrCopyAction(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_TYPE_NAME)){
            Properties parameters = new Properties();
            parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KimTypeImpl.class.getName());
            //parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, KNSConstants.PORTAL_ACTION);
            parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
            parameters.put(KNSConstants.DOC_FORM_KEY, KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY);
	        if (StringUtils.isNotBlank(getReturnLocation())) {
	        	parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
	        	}
            url = getCreateNewUrl(UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, parameters));
            //String url = "lookup.do?businessObjectClassName=org.kuali.rice.kim.bo.types.impl.KimTypeImpl&returnLocation=portal.do&docFormKey="+KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY;
        }
        return url;
    }

}
