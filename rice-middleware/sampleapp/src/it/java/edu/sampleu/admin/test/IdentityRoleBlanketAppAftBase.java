/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.sampleu.admin.test;

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class IdentityRoleBlanketAppAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Role&channelUrl=" 
     * + WebDriverUtil.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.kim.impl.role.RoleBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = ITUtil.PORTAL + "?channelTitle=Role&channelUrl=" 
            + WebDriverUtil.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.role.RoleBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK_FALSE + ITUtil.SHOW_MAINTENANCE_LINKS;

    /**
     * {@inheritDoc}
     * Role
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Role";
    }
   
    public void testIdentityRoleBlanketAppBookmark(Failable failable) throws Exception {
        testIdentityRoleBlanketApprove();
        passed();
    }

    public void testIdentityRoleBlanketAppNav(Failable failable) throws Exception {
        testIdentityRoleBlanketApprove();
        passed();
    }
}
