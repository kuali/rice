/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.main;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
     *  ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +"/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Create New Agenda
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Create New Agenda";
    }

    public void testCreateNewAgendaBookmark(JiraAwareFailable failable) throws Exception {
        testCreateNewAgenda();
        passed();
    }
    public void testCreateNewAgendaNav(JiraAwareFailable failable) throws Exception {
        testCreateNewAgenda();
        passed();
    }
}
