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
package org.kuali.rice.kim.lookup;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;

import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PersonLookupableImpl extends KualiLookupableImpl {

	private UiDocumentService uiDocumentService;
	private static final long serialVersionUID = 1707861010746829601L;

	@Override
	public String getCreateNewUrl() {
		String url = "";
		if((getLookupableHelperService()).allowsNewOrCopyAction(KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_TYPE_NAME)
				&& getUiDocumentService().canModifyEntity(GlobalVariables.getUserSession().getPrincipalId(), null)){
	        Properties parameters = new Properties();
	        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
	        parameters.put(KRADConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
	        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_TYPE_NAME);
	        if (StringUtils.isNotBlank(getReturnLocation())) {
	        	parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
	        	}
	        url = getCreateNewUrl(UrlFactory.parameterizeUrl(
	        		KimCommonUtilsInternal.getKimBasePath()+KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_ACTION, parameters));
	        //String url = "lookup.do?businessObjectClassName=org.kuali.rice.kim.bo.types.impl.KimTypeImpl&returnLocation=portal.do&docFormKey="+KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY;
	        //url = "../kim/identityManagementPersonDocument.do?methodToCall=docHandler&command=initiate&docTypeName=IdentityManagementPersonDocument";
	        //url = "<a href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
		}
		return url;
	}

	public UiDocumentService getUiDocumentService() {
		if ( uiDocumentService == null ) {
			uiDocumentService = KIMServiceLocatorInternal.getUiDocumentService();
		}
		return uiDocumentService;
	}

}
