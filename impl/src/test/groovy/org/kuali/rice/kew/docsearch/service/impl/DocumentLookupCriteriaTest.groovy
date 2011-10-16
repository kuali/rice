package org.kuali.rice.kew.docsearch.service.impl

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.SerializationUtils
import org.apache.commons.lang.time.StopWatch
import org.joda.time.DateTime
import org.junit.Test
import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria
import org.kuali.rice.kew.api.document.lookup.RouteNodeLookupLogic
import org.kuali.rice.kew.docsearch.DocumentLookupInternalUtils
import static org.junit.Assert.assertEquals

/**
 * Tests DocumentLookupCriteria marshalling and performance
 */
class DocumentLookupCriteriaTest {

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        //JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create("name"), XML, DocumentLookupCriteria.class)
        // DateTimeAdapter ensures DateTimes are marshalled appropriately, but MultiValuedStringMapAdapter is not fully implemented
        DocumentLookupCriteria c = createWithoutDocAttribs("name")
        assertEquals(c, unmarshalJAXB(marshalJAXB(c)))
    }

    @Test
    void testJSONMarshalling() {
        DocumentLookupCriteria c = create("name")
        def s = DocumentLookupInternalUtils.marshalDocumentLookupCriteria(c)
        def d = DocumentLookupInternalUtils.unmarshalDocumentLookupCriteria(s)
        assertEquals(c, d)
    }

    @Test
    void testPerformance() {
        def strats = [
            jaxb: [
                marshal: { marshalJAXB(it) },
                unmarshal: { unmarshalJAXB(it) }
            ],
            serialization: [
                marshal: { SerializationUtils.serialize(it) },
                unmarshal: { SerializationUtils.deserialize(it) }
            ],
            json: [
                marshal: {
                    DocumentLookupInternalUtils.marshalDocumentLookupCriteria(it)
                },
                unmarshal: {
                    DocumentLookupInternalUtils.unmarshalDocumentLookupCriteria(it)
                }
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
                marshalled << strat['marshal'].call(c)
            }
            //println marshalled[0]
            println("Marshal time: " + sw + " " + (sw.getTime() / 1000) + "ms/object")
            sw.reset()
            criterias.clear()
            sw.start()
            for (string in marshalled) {
                criterias << strat['unmarshal'].call(string)
            }
            println("Unmarshal time: " + sw + " " + (sw.getTime() / 1000) + "ms/object")
        }
    }

    protected static String marshalJAXB(DocumentLookupCriteria criteria) {
        StringWriter marshalledCriteriaWriter = new StringWriter()
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentLookupCriteria.class)
        Marshaller marshaller = jaxbContext.createMarshaller()
        marshaller.marshal(criteria, marshalledCriteriaWriter)
        marshalledCriteriaWriter.toString()
    }

    protected static DocumentLookupCriteria unmarshalJAXB(String s) {
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentLookupCriteria.class)
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller()
        return unmarshaller.unmarshal(new StringReader(s))
    }

    protected static DocumentLookupCriteria.Builder createBare(String saveName = null) {
        def builder = DocumentLookupCriteria.Builder.create()
        builder.applicationDocumentId = RandomStringUtils.randomAlphanumeric(20)
        builder.applicationDocumentStatus = RandomStringUtils.randomAlphanumeric(10)
        builder.approverPrincipalName = RandomStringUtils.randomAlphanumeric(20)

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
        return builder
    }

    protected static void addDocAttribs(DocumentLookupCriteria.Builder builder) {
        // TODO: FIXME: MultiValuedStringMapAdapter unmarshal not implemented
        Map<String, List<String>> attrs = new HashMap<String, List<String>>()
        for (i in 1..10) {
            def list = new ArrayList(10)
            for (j in 1..10) {
                list.add(RandomStringUtils.randomAlphanumeric(10))
            }
            attrs.put(RandomStringUtils.randomAlphanumeric(10), list)
        }
        builder.documentAttributeValues = attrs
    }

    protected static void addDates(DocumentLookupCriteria.Builder builder) {
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
    }

    protected static DocumentLookupCriteria createWithoutDocAttribs(String saveName = null) {
        def builder = createBare(saveName)
        addDates(builder)
        return builder.build()
    }

    protected static DocumentLookupCriteria createWithoutDates(String saveName = null) {
        def builder = createBare(saveName)
        addDocAttribs(builder)
        return builder.build()
    }

    protected static DocumentLookupCriteria create(String saveName = null) {
        def builder = createBare(saveName)
        addDates(builder)
        addDocAttribs(builder)
        return builder.build()
    }
}
