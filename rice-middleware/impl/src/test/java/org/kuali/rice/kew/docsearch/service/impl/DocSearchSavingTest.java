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
package org.kuali.rice.kew.docsearch.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.RouteNodeLookupLogic;
import org.kuali.rice.kew.docsearch.DocumentSearchInternalUtils;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchSavingTest {

    private class MockUserOptionsService implements UserOptionsService {

        private HashMap<String, String> options = new HashMap<String, String>();

        public Collection<UserOptions> findByWorkflowUser(String principalId) {
            List<UserOptions> userOpts = new ArrayList<UserOptions>();
            for (Map.Entry<String, String> entry : options.entrySet()) {
                UserOptions opt = new UserOptions();
                opt.setOptionId(entry.getKey());
                opt.setOptionVal(entry.getValue());
                userOpts.add(opt);
            }

            return userOpts;
        }

        public List<UserOptions> findByUserQualified(String principalId, String likeString) {
            String prefix = likeString.replaceAll("%", "");
            List<UserOptions> userOpts = new ArrayList<UserOptions>();
            for(Map.Entry<String, String> entry : options.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    UserOptions opt = new UserOptions();
                    opt.setOptionId(entry.getKey());
                    opt.setOptionVal(entry.getValue());
                    userOpts.add(opt);
                }
            }

            return userOpts;
        }

        public void save(UserOptions userOptions) {
            options.put(userOptions.getOptionId(), userOptions.getOptionVal());
        }

        public void save(String principalId, Map<String, String> optionsMap) {
            options.putAll(optionsMap);
        }

        public void save(String principalId, String optionId, String optionValue) {
            options.put(optionId, optionValue);
        }

        public void deleteUserOptions(UserOptions userOptions) {
            options.remove(userOptions.getOptionId());
        }

        public UserOptions findByOptionId(String optionId, String principalId) {
            if (options.containsKey(optionId)) {
                UserOptions opt = new UserOptions();
                opt.setOptionId(optionId);
                opt.setOptionVal(options.get(optionId));
                return opt;
            } else {
                return null;
            }
        }

        public List<UserOptions> retrieveEmailPreferenceUserOptions(String emailSetting) {
            return null;
        }
    }

    private class MockDocumentSearchService extends DocumentSearchServiceImpl {
        private static final int MAX_SEARCH_ITEMS = 5;
        private static final String LAST_SEARCH_ORDER_OPTION = "DocSearch.LastSearch.Order";
        private static final String NAMED_SEARCH_ORDER_BASE = "DocSearch.NamedSearch.";
        private static final String LAST_SEARCH_BASE_NAME = "DocSearch.LastSearch.Holding";

        public void saveSearch(String principalId, DocumentSearchCriteria criteria) {
            if (StringUtils.isBlank(principalId)) {
                return;
            }

            try {
                String savedSearchString = DocumentSearchInternalUtils.marshalDocumentSearchCriteria(criteria);

                if (StringUtils.isNotBlank(criteria.getSaveName())) {
                    userOptionsService.save(principalId, NAMED_SEARCH_ORDER_BASE + criteria.getSaveName(), savedSearchString);
                } else {
                    // first determine the current ordering
                    UserOptions searchOrder = userOptionsService.findByOptionId(LAST_SEARCH_ORDER_OPTION, principalId);
                    // no previous searches, save under first id
                    if (searchOrder == null) {
                        userOptionsService.save(principalId, LAST_SEARCH_BASE_NAME + "0", savedSearchString);
                        userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, LAST_SEARCH_BASE_NAME + "0");
                    } else {
                        String[] currentOrder = searchOrder.getOptionVal().split(",");
                        // we have reached MAX_SEARCH_ITEMS
                        if (currentOrder.length == MAX_SEARCH_ITEMS) {
                            // move the last item to the front of the list, and save
                            // over this key with the new criteria
                            // [5,4,3,2,1] => [1,5,4,3,2]
                            String searchName = currentOrder[currentOrder.length - 1];
                            String[] newOrder = new String[MAX_SEARCH_ITEMS];
                            newOrder[0] = searchName;
                            for (int i = 0; i < currentOrder.length - 1; i++) {
                                newOrder[i + 1] = currentOrder[i];
                            }

                            String newSearchOrder = rejoinWithCommas(newOrder);
                            // save the search string under the searchName (which used to be the last name in the list)
                            userOptionsService.save(principalId, searchName, savedSearchString);
                            userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
                        } else {
                            // saves the search to the front of the list with incremented index
                            // [3,2,1] => [4,3,2,1]
                            // here we need to do a push to identify the highest used number which is from the
                            // first one in the array, and then add one to it, and push the rest back one
                            int absMax = 0;
                            for (String aCurrentOrder : currentOrder) {
                                int current = new Integer(aCurrentOrder.substring(LAST_SEARCH_BASE_NAME.length(),
                                        aCurrentOrder.length()));
                                if (current > absMax) {
                                    absMax = current;
                                }
                            }
                            String searchName = LAST_SEARCH_BASE_NAME + ++absMax;
                            String[] newOrder = new String[currentOrder.length + 1];
                            newOrder[0] = searchName;
                            for (int i = 0; i < currentOrder.length; i++) {
                                newOrder[i + 1] = currentOrder[i];
                            }

                            String newSearchOrder = rejoinWithCommas(newOrder);
                            // save the search string under the searchName (which used to be the last name in the list)
                            userOptionsService.save(principalId, searchName, savedSearchString);
                            userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
                        }
                    }
                }
            } catch (Exception e) {
                // ignore
            }

        }

        /**
         * Returns a String result of the String array joined with commas
         * @param newOrder array to join with commas
         * @return String of the newOrder array joined with commas
         */
        private String rejoinWithCommas(String[] newOrder) {
            StringBuilder newSearchOrder = new StringBuilder("");
            for (String aNewOrder : newOrder) {
                if (newSearchOrder.length() != 0) {
                    newSearchOrder.append(",");
                }
                newSearchOrder.append(aNewOrder);
            }
            return newSearchOrder.toString();
        }
    }

    private MockDocumentSearchService docSearchService;
    private MockUserOptionsService userOptionsService;

    @Before
    public void init() {
        userOptionsService = new MockUserOptionsService();
        docSearchService = new MockDocumentSearchService();
        docSearchService.setUserOptionsService(userOptionsService);
    }

    @Test
    public void testConsumesExceptions() {
        // assuming a null criteria will cause an NPE
        docSearchService.saveSearch("princ", null);
    }

    @Test
    public void testUnnamedDocSearch() throws Exception {
        // mocked...
        String princ = "not blank";

        Collection<UserOptions> allUserOptions_before = userOptionsService.findByWorkflowUser(princ);

        assertEquals(0, allUserOptions_before.size());
        DocumentSearchCriteria c1 = saveSearch(princ, null);

        Collection<UserOptions> allUserOptions_after = userOptionsService.findByWorkflowUser(princ);

        // saves the "last doc search criteria"
        // and a pointer to the "last doc search criteria"
        assertEquals(allUserOptions_before.size() + 2, allUserOptions_after.size());

        assertEquals("DocSearch.LastSearch.Holding0", userOptionsService.findByOptionId("DocSearch.LastSearch.Order",
                princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c1), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding0", princ).getOptionVal());

        // 2nd search

        DocumentSearchCriteria c2 = saveSearch(princ, null);

        allUserOptions_after = userOptionsService.findByWorkflowUser(princ);

        // 1 more user option
        assertEquals(allUserOptions_before.size() + 3, allUserOptions_after.size());
        assertEquals("DocSearch.LastSearch.Holding1,DocSearch.LastSearch.Holding0", userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Order", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c1), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding0", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c2), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding1", princ).getOptionVal());

        docSearchService.clearNamedSearches(princ);
        assertEquals(0, userOptionsService.findByWorkflowUser(princ).size());
    }

    @Test
    public void testNamedDocSearch() throws Exception {
        // mocked...
        String princ = "not blank";
        Collection<UserOptions> allUserOptions_before = userOptionsService.findByWorkflowUser(princ);

        assertEquals(0, allUserOptions_before.size());

        DocumentSearchCriteria c1 = saveSearch(princ, "save1");

        Collection<UserOptions> allUserOptions_after = userOptionsService.findByWorkflowUser(princ);
        assertEquals(allUserOptions_before.size() + 1, allUserOptions_after.size());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c1), userOptionsService.findByOptionId(
                "DocSearch.NamedSearch." + c1.getSaveName(), princ).getOptionVal());

        // 2nd search
        DocumentSearchCriteria c2 = saveSearch(princ, "save2");

        allUserOptions_after = userOptionsService.findByWorkflowUser(princ);
        // saves a second named search
        assertEquals(allUserOptions_before.size() + 2, allUserOptions_after.size());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c2), userOptionsService.findByOptionId(
                "DocSearch.NamedSearch." + c2.getSaveName(), princ).getOptionVal());

        docSearchService.clearNamedSearches(princ);
        assertEquals(0, userOptionsService.findByWorkflowUser(princ).size());
    }

    @Test
    public void testSavedSearchOrdering() throws Exception {
        // searches start wrapping after this...
        Integer MAX_SEARCH_ITEMS = 5;
        String princ = "not blank";

        Collection<UserOptions> allUserOptions_before = userOptionsService.findByWorkflowUser(princ);

        DocumentSearchCriteria c1 = saveSearch(princ, null);
        DocumentSearchCriteria c2 = saveSearch(princ, null);
        DocumentSearchCriteria c3 = saveSearch(princ, null);
        DocumentSearchCriteria c4 = saveSearch(princ, null);
        DocumentSearchCriteria c5 = saveSearch(princ, null);

        Collection<UserOptions> allUserOptions_after = userOptionsService.findByWorkflowUser(princ);
        assertEquals(allUserOptions_before.size() + 5 + 1, allUserOptions_after.size());
        assertEquals("DocSearch.LastSearch.Holding4,DocSearch.LastSearch.Holding3,DocSearch.LastSearch.Holding2,"
                + "DocSearch.LastSearch.Holding1,DocSearch.LastSearch.Holding0", userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Order", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c5), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding4", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c4), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding3", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c3), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding2", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c2), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding1", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c1), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding0", princ).getOptionVal());

        // now add 1 more

        DocumentSearchCriteria c6 = saveSearch(princ, null);


        allUserOptions_after = userOptionsService.findByWorkflowUser(princ);

        // order should have wrapped around, now Holding0 is first, and contains c6 criteria
        // still 5 entries
        assertEquals(allUserOptions_before.size() + 5 + 1, allUserOptions_after.size());
        assertEquals("DocSearch.LastSearch.Holding0,DocSearch.LastSearch.Holding4,DocSearch.LastSearch.Holding3,"
                + "DocSearch.LastSearch.Holding2,DocSearch.LastSearch.Holding1", userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Order", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c6), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding0", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c5), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding4", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c4), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding3", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c3), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding2", princ).getOptionVal());
        assertEquals(DocumentSearchInternalUtils.marshalDocumentSearchCriteria(c2), userOptionsService.findByOptionId(
                "DocSearch.LastSearch.Holding1", princ).getOptionVal());

        docSearchService.clearNamedSearches(princ);
        assertEquals(0, userOptionsService.findByWorkflowUser(princ).size());
    }

    private DocumentSearchCriteria saveSearch(String princ, String name) {
        DocumentSearchCriteria dsc = createDocSearchCriteria(name);
        docSearchService.saveSearch(princ, dsc);
        return dsc;
    }

    private DocumentSearchCriteria createDocSearchCriteria(String saveName) {
        DocumentSearchCriteria.Builder builder = DocumentSearchCriteria.Builder.create();
        builder.setApplicationDocumentId(RandomStringUtils.randomAlphanumeric(5));
        builder.setApplicationDocumentStatus(RandomStringUtils.randomAlphanumeric(5));
        builder.setApproverPrincipalName(RandomStringUtils.randomAlphanumeric(5));
        builder.setApproverPrincipalId(RandomStringUtils.randomAlphanumeric(5));
        builder.setDocumentId( RandomStringUtils.randomAlphanumeric(5));
        builder.setDocumentTypeName(RandomStringUtils.randomAlphanumeric(5));
        builder.setDocumentStatusCategories(Arrays.asList(DocumentStatusCategory.PENDING, DocumentStatusCategory.SUCCESSFUL));
        builder.setDocumentStatuses(Arrays.asList(DocumentStatus.ENROUTE, DocumentStatus.INITIATED, DocumentStatus.SAVED));
        builder.setInitiatorPrincipalName(RandomStringUtils.randomAlphanumeric(10));
        builder.setInitiatorPrincipalId(RandomStringUtils.randomAlphanumeric(10));
        builder.setMaxResults(500);
        builder.setRouteNodeName(RandomStringUtils.randomAlphanumeric(5));
        builder.setSaveName(saveName);
        builder.setStartAtIndex(1);
        builder.setTitle(RandomStringUtils.randomAlphanumeric(10));
        builder.setGroupViewerId(RandomStringUtils.randomAlphanumeric(5));
        builder.setViewerPrincipalName(RandomStringUtils.randomAlphanumeric(10));
        builder.setViewerPrincipalId(RandomStringUtils.randomAlphanumeric(10));
        builder.setRouteNodeLookupLogic(RouteNodeLookupLogic.EXACTLY);

        builder.setDateApplicationDocumentStatusChangedFrom(new DateTime());
        builder.setDateApplicationDocumentStatusChangedTo(new DateTime());
        builder.setDateApprovedFrom(new DateTime());
        builder.setDateApprovedTo(new DateTime());
        builder.setDateCreatedFrom(new DateTime());
        builder.setDateCreatedTo(new DateTime());
        builder.setDateFinalizedFrom(new DateTime());
        builder.setDateFinalizedTo(new DateTime());
        builder.setDateLastModifiedFrom(new DateTime());
        builder.setDateLastModifiedTo(new DateTime());

        Map<String, List<String>> attrs = new HashMap<String, List<String>>();
        for (int i = 1;i<6;i++) {
            ArrayList<String> list = new ArrayList<String>();
            for (int j = 1;j<6;j++) {
                list.add(RandomStringUtils.randomAlphanumeric(5));
            }
            attrs.put(RandomStringUtils.randomAlphanumeric(5), list);
        }
        builder.setDocumentAttributeValues(attrs);

        return builder.build();
    }
}
