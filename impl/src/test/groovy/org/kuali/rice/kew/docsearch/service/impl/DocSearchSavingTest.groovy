package org.kuali.rice.kew.docsearch.service.impl

import org.junit.Before
import org.junit.Test
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria
import org.kuali.rice.kew.useroptions.UserOptions
import org.kuali.rice.kew.useroptions.UserOptionsServiceImpl
import org.kuali.rice.kew.useroptions.dao.UserOptionsDAO
import static org.junit.Assert.assertEquals
import static DocumentLookupCriteriaTest.marshall
import static DocumentLookupCriteriaTest.unmarshall
import static DocumentLookupCriteriaTest.create

/**
 * Unit tests DocumentLookupCriteria saving behavior of DocumentSearchServiceImpl
 */
class DocSearchSavingTest {
    // mock out UserOptionsDAO - just save in memory
    private def mockUserOptionsDAO = new UserOptionsDAO() {
        def options = new HashMap<String, String>()
        Collection<UserOptions> findByWorkflowUser(String principalId) {
            options.collect {
                def opt = new UserOptions();
                opt.optionId = it.key
                opt.optionVal = it.value
                opt
            }
        }
        List<UserOptions> findByUserQualified(String principalId, String likeString) { null }
        void deleteByUserQualified(String principalId, String likeString) { }
        void save(UserOptions userOptions) {
            options.put(userOptions.optionId, userOptions.optionVal)
        }
        void save(Collection<UserOptions> userOptions) {
            userOptions.each { save(it) }
        }
        void deleteUserOptions(UserOptions userOptions) {
            options.remove(userOptions.optionId)
        }
        UserOptions findByOptionId(String optionId, String principalId) {
            if (options[optionId]) {
              def opt = new UserOptions()
              opt.optionId = optionId
              opt.optionVal = options[optionId]
              opt
            } else {
              null
            }
        }
        Collection<UserOptions> findByOptionValue(String optionId, String optionValue) { null }
        Long getNewOptionIdForActionList() { 0 }
    }

    private def userOptionsService = new UserOptionsServiceImpl()
    private def docSearchService = new DocumentSearchServiceImpl() {
        public void saveSearch(String principalId, DocumentLookupCriteria criteria) {
            super.saveSearch(principalId, criteria)
        }
    }

    @Before
    void init() {
        userOptionsService.setUserOptionsDAO(mockUserOptionsDAO)
        docSearchService.setUserOptionsService(userOptionsService)
    }

    @Test
    void testUnnamedDocSearch() {
        def princ = "not blank" // mocked...

        def allUserOptions_before = userOptionsService.findByWorkflowUser(princ)

        assertEquals(0, allUserOptions_before.size())

        def c1 = create()
        docSearchService.saveSearch(princ, c1)

        def allUserOptions_after = userOptionsService.findByWorkflowUser(princ)

        // saves the "last doc search criteria"
        // and a pointer to the "last doc search criteria"
        assertEquals(allUserOptions_before.size() + 2, allUserOptions_after.size())

        assertEquals("DocSearch.LastSearch.Holding0", userOptionsService.findByOptionId("DocSearch.LastSearch.Order", princ).optionVal)
        assertEquals(marshall(c1), userOptionsService.findByOptionId("DocSearch.LastSearch.Holding0", princ).optionVal)

        // 2nd search

        def c2 = create()
        docSearchService.saveSearch(princ, c2)

        allUserOptions_after = userOptionsService.findByWorkflowUser(princ)

        // 1 more user option
        assertEquals(allUserOptions_before.size() + 3, allUserOptions_after.size())
        
        assertEquals("DocSearch.LastSearch.Holding1,DocSearch.LastSearch.Holding0", userOptionsService.findByOptionId("DocSearch.LastSearch.Order", princ).optionVal)
        assertEquals(marshall(c1), userOptionsService.findByOptionId("DocSearch.LastSearch.Holding0", princ).optionVal)
        assertEquals(marshall(c2), userOptionsService.findByOptionId("DocSearch.LastSearch.Holding1", princ).optionVal)
    }

    @Test
    void testNamedDocSearch() {
        def princ = "not blank" // mocked...

        def allUserOptions_before = userOptionsService.findByWorkflowUser(princ)

        assertEquals(0, allUserOptions_before.size())

        def c1 = create("save1")
        docSearchService.saveSearch(princ, c1)

        def allUserOptions_after = userOptionsService.findByWorkflowUser(princ)

        assertEquals(allUserOptions_before.size() + 1, allUserOptions_after.size())
        assertEquals(marshall(c1), userOptionsService.findByOptionId("DocSearch.NamedSearch." + c1.getSaveName(), princ).optionVal)

        // 2nd search

        def c2 = create("save2")
        docSearchService.saveSearch(princ, c2)

        allUserOptions_after = userOptionsService.findByWorkflowUser(princ)

        // saves a second named search
        assertEquals(allUserOptions_before.size() + 2, allUserOptions_after.size())
        assertEquals(marshall(c2), userOptionsService.findByOptionId("DocSearch.NamedSearch." + c2.getSaveName(), princ).optionVal)
    }
}