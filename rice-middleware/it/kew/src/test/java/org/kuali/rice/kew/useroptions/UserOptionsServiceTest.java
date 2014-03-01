/*
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

package org.kuali.rice.kew.useroptions;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.identity.principal.Principal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for {link@ UserOptionsServiceImpl}
 * Note not all methods for UserOptionsServiceImpl are tested in this test class, some methods are tested within the
 *  {@link org.kuali.rice.kew.preferences.PreferencesServiceTest} test class
 */
public class UserOptionsServiceTest extends KEWTestCase {

    /* Static string for the refresh rate user option */
    private final static String REFRESH_RATE = "REFRESH_RATE";

    /* Static String for setting the refresh rate value */
    private final static String ELEVEN = "11";

    /**
     * Test save method when a principal id, option id, and option value are given
     * @throws Exception if errors while validating save method.
     */
    @Test
    public void testUserOptionsSaveWithPrincipalIdOptionIdOptionVal() throws Exception {
        final UserOptionsService userOptionsService = this.getUserOptionsService();
        Principal principal = this.getPrincipalByPrincipalName("ewestfal");
        Collection<UserOptions> userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertTrue("UserOptions should by empty", userOptions.isEmpty());

        // save a user option to the datasource
        userOptionsService.save(principal.getPrincipalId(), REFRESH_RATE, "11");

        // now ensure we get the same user option back from the datasource
        userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertFalse("UserOptions should not by empty", userOptions.isEmpty());
        UserOptions firstUsrOpt = new ArrayList<UserOptions>(userOptions).get(0);
        assertTrue("UserOption option id should be " + REFRESH_RATE, REFRESH_RATE.equals(firstUsrOpt.getOptionId()));
        assertTrue("UserOption option value should be 11", ELEVEN.equals(firstUsrOpt.getOptionVal()));
    }

    /**
     * Test save method when a principal id and a {@link Map} keyed by option ids and option values as the mapped value
     * @throws Exception if errors while validating save method.
     */
    @Test
    public void testUserOptionsSaveWithOptionMap() throws Exception {
        final UserOptionsService userOptionsService = this.getUserOptionsService();
        Principal principal = this.getPrincipalByPrincipalName("ewestfal");
        Collection<UserOptions> userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertTrue("UserOptions should by empty", userOptions.isEmpty());

        // build a option map and persist it to the datasource
        Map<String, String> userOptMap = new HashMap<String, String>();
        userOptMap.put("favoriteColor", "blue");
        userOptMap.put("backgroundColor","white");
        userOptMap.put(REFRESH_RATE, "30");
        userOptionsService.save(principal.getPrincipalId(), userOptMap);

        userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertFalse("UserOptions should not be empty", userOptions.isEmpty());
        assertTrue("UserOptions should contain three items", userOptions.size() == 3);
    }

    /**
     * Test to ensure findByUserQualified method is filtering results with the given principal id and likeString.
     * @throws Exception if errors while validating findByUserQualified method
     */
    @Test
    public void testFindByUserQualified() throws Exception {
        final UserOptionsService userOptionsService = this.getUserOptionsService();
        Principal principal = this.getPrincipalByPrincipalName("ewestfal");
        Collection<UserOptions> userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertTrue("UserOptions should by empty", userOptions.isEmpty());

        // build a option map and persist it to the datasource
        Map<String, String> userOptMap = new HashMap<String, String>();
        userOptMap.put("favoriteColor", "blue");
        userOptMap.put("favoriteFood","pizza");
        userOptMap.put("favoriteTimeOfYear", "summer");
        userOptMap.put(REFRESH_RATE, "30");
        userOptMap.put("lastSaveDate","now");
        userOptionsService.save(principal.getPrincipalId(), userOptMap);

        userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertFalse("UserOptions should not be empty", userOptions.isEmpty());
        assertTrue("UserOptions should contain five items", userOptions.size() == 5);

        userOptions = userOptionsService.findByUserQualified(principal.getPrincipalId(), "favorite%");
        assertFalse("UserOptions should not be empty", userOptions.isEmpty());
        assertTrue("UserOptions should contain 3 items.", userOptions.size() == 3);
        for (UserOptions opt: userOptions) {
            assertTrue("UserOptionId (" + opt.getOptionId() + ") should start with favorite",
                    opt.getOptionId().startsWith("favorite"));
        }
    }

    /**
     * Test retrieveEmailPreferenceUserOptions method is filtering results with the given principal id and email setting
     * @throws Exception if errors while validating retrieveEmailPreferenceUserOptions method
     */
    @Test
    public void testRetrieveEmailPreferenceUserOptions() throws Exception {
        final UserOptionsService userOptionsService = this.getUserOptionsService();
        Principal principal = this.getPrincipalByPrincipalName("ewestfal");
        Collection<UserOptions> userOptions = userOptionsService.findByWorkflowUser(principal.getPrincipalId());
        assertTrue("UserOptions should by empty", userOptions.isEmpty());

        // test with user options not related to email preferences
        Map<String, String> userOptMap1 = new HashMap<String, String>();
        userOptMap1.put("favoriteColor", "blue");
        userOptMap1.put("favoriteFood","pizza");
        userOptMap1.put("favoriteTimeOfYear", "summer");
        userOptMap1.put(REFRESH_RATE, "30");
        List<UserOptions> optList = userOptionsService.retrieveEmailPreferenceUserOptions("testValue");
        assertTrue("OptList should be empty.", optList.isEmpty());

        // test with user options related to email preferences
        Map<String, String> userOptMap2 = new HashMap<String, String>();
        userOptMap2.put("favoriteColor", "blue");
        userOptMap2.put("favoriteFood","pizza");
        userOptMap2.put("favoriteTimeOfYear", "summer");
        userOptMap2.put(REFRESH_RATE, "30");
        userOptMap2.put(KewApiConstants.EMAIL_RMNDR_KEY, KewApiConstants.EMAIL_RMNDR_DAY_VAL);
        userOptMap2.put("Error" + KewApiConstants.DOCUMENT_TYPE_NOTIFICATION_PREFERENCE_SUFFIX,
                KewApiConstants.EMAIL_RMNDR_DAY_VAL);
        userOptMap2.put("Warning" + KewApiConstants.DOCUMENT_TYPE_NOTIFICATION_PREFERENCE_SUFFIX,
                KewApiConstants.EMAIL_RMNDR_DAY_VAL);
        userOptionsService.save(principal.getPrincipalId(), userOptMap2);
        // query for email preferences user options only
        optList = userOptionsService.retrieveEmailPreferenceUserOptions(KewApiConstants.EMAIL_RMNDR_DAY_VAL);
        assertFalse("OptList should not be empty", optList.isEmpty());
        assertTrue("OptList should contain 3 items, optList size is: " + optList.size(), optList.size() == 3);
    }

    /**
     * Helper method for test class returns an implementation of the {@link UserOptionsService}.
     * @return an implementation of the {@link UserOptionsService}
     */
    private UserOptionsService getUserOptionsService() {
        return KEWServiceLocator.getUserOptionsService();
    }

    /**
     * Helper method for test class returns a {@link Principal} for the given principal name.
     * @param principleName the unique identifier of the user to search for
     * @return a {@link Principal} for the given principal name.
     */
    private Principal getPrincipalByPrincipalName(String principleName) {
        return  KEWServiceLocator.getIdentityHelperService().getPrincipalByPrincipalName(principleName);
    }
}
