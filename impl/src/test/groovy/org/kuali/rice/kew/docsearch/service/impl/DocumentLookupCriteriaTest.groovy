package org.kuali.rice.kew.docsearch.service.impl

import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.joda.time.DateTime
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.DocumentStatus
import org.apache.commons.lang.RandomStringUtils
import org.kuali.rice.kew.api.document.lookup.RouteNodeLookupLogic
import org.apache.commons.lang.time.StopWatch
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.apache.commons.lang.SerializationUtils
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
        assertEquals(c, unmarshall(marshall(c)))
    }

    @Test
    void testJSONMarshalling() {
        // JSON deserializes maps properly, but not DateTimes
        DocumentLookupCriteria c = createWithoutDates("name")
        ObjectMapper m = new ObjectMapper()
        // these options don't seem to allow jackson to set fields directly on DLC (possibly because they're final?)
        // we have to use the Builder instead
        //m.getDeserializationConfig().disable(DeserializationConfig.Feature.AUTO_DETECT_SETTERS)
        //m.getDeserializationConfig().enable(DeserializationConfig.Feature.AUTO_DETECT_FIELDS)
        //m.getDeserializationConfig().enable(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)
        def s = m.writeValueAsString(c)
        def d = m.readValue(s, DocumentLookupCriteria.Builder.class)
        assertEquals(c.getDateApplicationDocumentStatusChangedFrom(), d.build().getDateApplicationDocumentStatusChangedFrom())
        assertEquals(c, d.build())
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
            ],
            json: [
                marshall: {
                    new ObjectMapper().writeValueAsString(it)
                },
                unmarshall: {
                    new ObjectMapper().readValue(it, DocumentLookupCriteria.Builder.class).build()
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
                marshalled << strat['marshall'].call(c)
            }
            //println marshalled[0]
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
        // TODO: FIXME: MultiValuedStringMapAdapter unmarshall not implemented
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
