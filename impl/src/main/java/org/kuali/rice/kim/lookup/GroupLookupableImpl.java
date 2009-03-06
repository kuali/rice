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

import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupLookupableImpl extends KualiLookupableImpl {

	@Override
	public String getCreateNewUrl() {
		//lookup.do?businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType&amp;conversionFields=acquisitionTypeCode%3AacquisitionTypeCode&amp;returnLocation=portal.do&amp;docFormKey=88888888"
		String url = "lookup.do?businessObjectClassName=org.kuali.rice.kim.bo.types.impl.KimTypeImpl&returnLocation=portal.do&docFormKey="+KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_SHORT_KEY;
		//String url = "../kim/identityManagementRoleDocument.do?methodToCall=docHandler&command=initiate&docTypeName=IdentityManagementRoleDocument";
        //url = "kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.types.impl.KimTypeImpl";
        return "<a href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
	}

}
