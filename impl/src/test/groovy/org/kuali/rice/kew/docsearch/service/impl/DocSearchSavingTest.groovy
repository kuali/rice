package org.kuali.rice.kew.docsearch.service.impl

import org.kuali.rice.kew.useroptions.UserOptionsService
import org.kuali.rice.kew.useroptions.dao.impl.UserOptionsDAOOjbImpl
import org.kuali.rice.kew.useroptions.dao.UserOptionsDAO
import org.kuali.rice.kew.useroptions.UserOptions
import org.junit.Before
import org.kuali.rice.kew.useroptions.UserOptionsServiceImpl
import org.kuali.rice.kew.docsearch.dao.DocumentSearchDAO
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults.Builder
import org.kuali.rice.kew.impl.document.lookup.DocumentLookupGenerator
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria
import org.kuali.rice.core.api.uif.RemotableAttributeField
import org.junit.Test
import static org.junit.Assert.assertEquals
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import org.joda.time.DateTime
import org.apache.commons.lang.RandomStringUtils
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.lookup.RouteNodeLookupLogic
import org.apache.commons.lang.time.StopWatch
import javax.xml.bind.Unmarshaller
import org.apache.commons.lang.SerializationUtils

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

    @Test
    void testPerformance() {
        def strats = [
            jaxb: [
                marshall: { marshall(it) },
                unmarshall: { unmarshall(it) }
            ],
            serialization: [
                marshall: { SerializationUtils.serialize(it) },
                unmarshall: { SerializationUtils.deserialize(it) }
            ]
        ]

        for (key in strats.keySet()) {
            println("Strategy: " + key)
            def strat = strats[key]

            def sw = new StopWatch()
            sw.start()
            def criterias = []
            for (i in 1..1000) {
              criterias << create(RandomStringUtils.randomAlphanumeric(100))
            }
            println("Build time: " + sw + " " + (sw.getTime() / 1000) + "ms/object")
            sw.reset()
            sw.start()
            def marshalled = []
            for (c in criterias) {
                marshalled << strat['marshall'].call(c)
            }
            //println marshalled[0]
            //println marshalled[0].size()
            println("Marshall time: " + sw + " " + (sw.getTime() / 1000) + "ms/object")
            sw.reset()
            criterias.clear()
            sw.start()
            for (string in marshalled) {
                criterias << strat['unmarshall'].call(string)
            }
            println("Unmarshall time: " + sw + " " + (sw.getTime() / 1000) + "ms/object")
        }
    }

    
    protected static String marshall(DocumentLookupCriteria criteria) {
        StringWriter marshalledCriteriaWriter = new StringWriter()
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentLookupCriteria.class)
        Marshaller marshaller = jaxbContext.createMarshaller()
        marshaller.marshal(criteria, marshalledCriteriaWriter)
        marshalledCriteriaWriter.toString()
    }

    protected static DocumentLookupCriteria unmarshall(String s) {
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentLookupCriteria.class)
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller()
        return unmarshaller.unmarshal(new StringReader(s))
    }

    protected static DocumentLookupCriteria create(String saveName = null) {
        def builder = DocumentLookupCriteria.Builder.create()
        builder.applicationDocumentId = RandomStringUtils.randomAlphanumeric(20)
        builder.applicationDocumentStatus = RandomStringUtils.randomAlphanumeric(10)
        builder.approverPrincipalName = RandomStringUtils.randomAlphanumeric(20)
        builder.dateApplicationDocumentStatusChangedFrom = new DateTime()
        builder.dateApplicationDocumentStatusChangedTo = new DateTime()
        builder.dateApprovedFrom = new DateTime()
        builder.dateApprovedTo = new DateTime()
        builder.dateCreatedFrom = new DateTime()
        builder.dateCreatedTo = new DateTime()
        builder.dateFinalizedFrom = new DateTime()
        builder.dateFinalizedTo = new DateTime()
        builder.dateLastModifiedFrom = new DateTime()
        builder.dateLastModifiedTo = new DateTime()
        Map<String, List<String>> attrs = new HashMap<String, List<String>>()
        for (i in 1..10) {
            def list = new ArrayList(10)
            for (j in 1..10) {
                list.add(RandomStringUtils.randomAlphanumeric(10))
            }
            attrs.put(RandomStringUtils.randomAlphanumeric(10), list)
        }
        builder.documentAttributeValues = attrs
        builder.documentId = RandomStringUtils.randomAlphanumeric(10)
        builder.documentTypeName = RandomStringUtils.randomAlphanumeric(10)
        builder.documentStatusCategories = Arrays.asList([ DocumentStatusCategory.PENDING, DocumentStatusCategory.SUCCESSFUL ] as DocumentStatusCategory[])
        builder.documentStatuses = Arrays.asList([ DocumentStatus.ENROUTE, DocumentStatus.INITIATED, DocumentStatus.SAVED ] as DocumentStatus[])
        builder.initiatorPrincipalName = RandomStringUtils.randomAlphanumeric(20)
        builder.maxResults = 1000
        builder.routeNodeName = RandomStringUtils.randomAlphanumeric(10)
        builder.saveName = saveName
        builder.startAtIndex = 1
        builder.title = RandomStringUtils.randomAlphanumeric(20)
        builder.viewerGroupId = RandomStringUtils.randomAlphanumeric(10)
        builder.viewerPrincipalName = RandomStringUtils.randomAlphanumeric(20)
        builder.routeNodeLookupLogic = RouteNodeLookupLogic.EXACTLY
        return builder.build()
    }
}
