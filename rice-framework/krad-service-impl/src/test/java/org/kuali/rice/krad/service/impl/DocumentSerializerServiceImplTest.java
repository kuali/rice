package org.kuali.rice.krad.service.impl;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.XmlObjectSerializerService;
import org.kuali.rice.krad.util.documentserializer.MetadataPropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluatorBase;
import org.kuali.rice.krad.util.documentserializer.PropertySerializerTrie;
import org.kuali.rice.krad.util.documentserializer.SerializationState;
import org.kuali.rice.krad.workflow.DocumentInitiator;
import org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer;
import org.kuali.rice.krad.workflow.KualiTransactionalDocumentInformation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

/**
 * Unit test for {@link DocumentSerializerServiceImpl}
 *
 * @author Eric Westfall
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentSerializerServiceImplTest {

    @Mock
    private LegacyDataAdapter legacyDataAdapter;
    @Mock
    private XmlObjectSerializerService xmlObjectSerializerService;

    @InjectMocks
    private DocumentSerializerServiceImpl serializerService = new DocumentSerializerServiceImpl();

    @Before
    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testSerializeDocumentToXmlForRouting_TopLevelPropertyInclusion() throws Exception {
        // set up a simple document that is set up with a serializability evaluator that serializes everything
        WrappedDocument document = new WrappedDocument(new MakeItSoPropertySerializibilityEvaluator());
        document.setDocumentProperty1("value1");
        document.setDocumentProperty2(2);

        // now serialize it

        String xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(SIMPLE_DOCUMENT_FULL_XML, xml);
        System.out.println(xml);

        // now let's try again with a partial set of things to serialize

        PropertySerializerTrie metadata = new PropertySerializerTrie();
        metadata.addSerializablePropertyName("document", false);
        metadata.addSerializablePropertyName("document.documentProperty1", false);
        MetadataPropertySerializabilityEvaluator evaluator = new MetadataPropertySerializabilityEvaluator(metadata);
        document = new WrappedDocument(evaluator);
        xml = serializerService.serializeDocumentToXmlForRouting(document);



//        PropertySerializerTrie trie = new PropertySerializerTrie();
//        trie.addSerializablePropertyName("documentProperty1");
//        String xml = serializerService.serializeDocumentToXmlForRouting(document);
        System.out.println(xml);

    }

    public static class WrappedDocument extends TransactionalDocumentBase {

        private String documentProperty1;
        private int documentProperty2;
        private Child child1;
        private Child child2;
        private Child child3;

        private transient PropertySerializabilityEvaluator evaluator;

        public WrappedDocument(PropertySerializabilityEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public void setEvaluator(PropertySerializabilityEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Override
        public KualiDocumentXmlMaterializer wrapDocumentWithMetadataForXmlSerialization() {
            KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
            DocumentInitiator initiator = new DocumentInitiator();
            initiator.setPerson(new MockPerson("johndoe"));
            transInfo.setDocumentInitiator(initiator);
            KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
            xmlWrapper.setDocument(this);
            xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
            return xmlWrapper;
        }

        @Override
        public PropertySerializabilityEvaluator getDocumentPropertySerizabilityEvaluator() {
            return evaluator;
        }

        public String getDocumentProperty1() {
            return documentProperty1;
        }

        public void setDocumentProperty1(String documentProperty1) {
            this.documentProperty1 = documentProperty1;
        }

        public int getDocumentProperty2() {
            return documentProperty2;
        }

        public void setDocumentProperty2(int documentProperty2) {
            this.documentProperty2 = documentProperty2;
        }

        public Child getChild1() {
            return child1;
        }

        public void setChild1(Child child1) {
            this.child1 = child1;
        }

        public Child getChild2() {
            return child2;
        }

        public void setChild2(Child child2) {
            this.child2 = child2;
        }

        public Child getChild3() {
            return child3;
        }

        public void setChild3(Child child3) {
            this.child3 = child3;
        }
    }

    public static class Child {

        private String property1;
        private Grandchild grandchild1;
        private Grandchild grandchild2;
        private Grandchild grandchild3;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public Grandchild getGrandchild1() {
            return grandchild1;
        }

        public void setGrandchild1(Grandchild grandchild1) {
            this.grandchild1 = grandchild1;
        }

        public Grandchild getGrandchild2() {
            return grandchild2;
        }

        public void setGrandchild2(Grandchild grandchild2) {
            this.grandchild2 = grandchild2;
        }

        public Grandchild getGrandchild3() {
            return grandchild3;
        }

        public void setGrandchild3(Grandchild grandchild3) {
            this.grandchild3 = grandchild3;
        }
    }

    public static class Grandchild {

        private String property1;
        private boolean property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public boolean isProperty2() {
            return property2;
        }

        public void setProperty2(boolean property2) {
            this.property2 = property2;
        }
    }

    public static class MakeItSoPropertySerializibilityEvaluator extends PropertySerializabilityEvaluatorBase {
        @Override
        public boolean isPropertySerializable(SerializationState state, Object containingObject, String childPropertyName, Object childPropertyValue) {
            // make it so...
            return true;
        }
    }

    public static class MockPerson implements Person {

        private static final long serialVersionUID = 5330488987382249417L;

        private final String id;

        public MockPerson() {
            id = null;
        }

        MockPerson(String id) {
            this.id = id;
        }

        @Override
        public void refresh() {}

        @Override
        public String getPrincipalId() {
            return id;
        }

        @Override
        public String getPrincipalName() {
            return id;
        }

        @Override
        public String getEntityId() {
            return id;
        }

        @Override
        public String getEntityTypeCode() {
            return null;
        }

        @Override
        public String getFirstName() {
            return "Test";
        }

        @Override
        public String getFirstNameUnmasked() {
            return "Test";
        }

        @Override
        public String getMiddleName() {
            return "User";
        }

        @Override
        public String getMiddleNameUnmasked() {
            return "User";
        }

        @Override
        public String getLastName() {
            return id;
        }

        @Override
        public String getLastNameUnmasked() {
            return id;
        }

        @Override
        public String getName() {
            return "Test User " + id;
        }

        @Override
        public String getNameUnmasked() {
            return "Test User " + id;
        }

        @Override
        public String getEmailAddress() {
            return null;
        }

        @Override
        public String getEmailAddressUnmasked() {
            return null;
        }

        @Override
        public String getAddressLine1() {
            return null;
        }

        @Override
        public String getAddressLine1Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine2() {
            return null;
        }

        @Override
        public String getAddressLine2Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine3() {
            return null;
        }

        @Override
        public String getAddressLine3Unmasked() {
            return null;
        }

        @Override
        public String getAddressCity() {
            return null;
        }

        @Override
        public String getAddressCityUnmasked() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCode() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressPostalCode() {
            return null;
        }

        @Override
        public String getAddressPostalCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressCountryCode() {
            return null;
        }

        @Override
        public String getAddressCountryCodeUnmasked() {
            return null;
        }

        @Override
        public String getPhoneNumber() {
            return null;
        }

        @Override
        public String getPhoneNumberUnmasked() {
            return null;
        }

        @Override
        public String getCampusCode() {
            return null;
        }

        @Override
        public Map<String, String> getExternalIdentifiers() {
            return null;
        }

        @Override
        public boolean hasAffiliationOfType(String affiliationTypeCode) {
            return false;
        }

        @Override
        public List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) {
            return null;
        }

        @Override
        public String getEmployeeStatusCode() {
            return null;
        }

        @Override
        public String getEmployeeTypeCode() {
            return null;
        }

        @Override
        public KualiDecimal getBaseSalaryAmount() {
            return null;
        }

        @Override
        public String getExternalId(String externalIdentifierTypeCode) {
            return null;
        }

        @Override
        public String getPrimaryDepartmentCode() {
            return null;
        }

        @Override
        public String getEmployeeId() {
            return null;
        }

        @Override
        public boolean isActive() {
            return true;
        }

    }

    private static final String SIMPLE_DOCUMENT_FULL_XML = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("<kualiTransactionalDocumentInformation>")
            .append("<documentInitiator>")
            .append("<person class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$MockPerson\">")
            .append("<id>johndoe</id>")
            .append("</person>")
            .append("</documentInitiator>")
            .append("</kualiTransactionalDocumentInformation>")
            .append("<document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("<newCollectionRecord>false</newCollectionRecord>")
            .append("<documentHeader>")
            .append("<newCollectionRecord>false</newCollectionRecord>")
            .append("</documentHeader>")
            .append("<pessimisticLocks/>")
            .append("<adHocRoutePersons/>")
            .append("<adHocRouteWorkgroups/>")
            .append("<notes/>")
            .append("<superUserAnnotation></superUserAnnotation>")
            .append("<documentProperty1>value1</documentProperty1>")
            .append("<documentProperty2>2</documentProperty2>")
            .append("</document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>").toString();


}
