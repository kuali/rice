/**
 * Copyright 2005-2017 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
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

        // now let's try again with a partial set of things to serialize

        PropertySerializerTrie metadata = new PropertySerializerTrie();
        metadata.addSerializablePropertyName("document.documentProperty1", false);
        MetadataPropertySerializabilityEvaluator evaluator = new MetadataPropertySerializabilityEvaluator(metadata);
        document = new WrappedDocument(evaluator);
        document.setDocumentProperty1("value1");
        document.setDocumentProperty2(2);

        xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(SIMPLE_DOCUMENT_PROPERTY_1, xml);
    }

    @Test
    public void testSerializeDocumentToXmlForRouting_DeepObjectGraph() throws Exception {
        // set up a simple document that is set up with a serializability evaluator that serializes everything
        WrappedDocument document = setupDeepDocument();
        document.setEvaluator(new MakeItSoPropertySerializibilityEvaluator());
        // now serialize it

        String xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(DEEP_DOCUMENT_FULL_XML, xml);

        // now let's try again with a partial set of things to serialize

        PropertySerializerTrie metadata = new PropertySerializerTrie();
        metadata.addSerializablePropertyName("document.child1", false);
        metadata.addSerializablePropertyName("document.child2.grandchild2", false);
        metadata.addSerializablePropertyName("document.child2.grandchild3", false);
        MetadataPropertySerializabilityEvaluator evaluator = new MetadataPropertySerializabilityEvaluator(metadata);
        document.setEvaluator(evaluator);

        xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(DEEP_DOCUMENT_PARTIAL_XML, xml);
    }

    @Test
    public void testSerializeDocumentToXmlForRouting_Collections() throws Exception {
        // set up a simple document that is set up with a serializability evaluator that serializes everything
        WrappedDocument document = setupCollectionDocument();
        document.setEvaluator(new MakeItSoPropertySerializibilityEvaluator());
        // now serialize it

        String xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(COLLECTION_DOCUMENT_FULL_XML, xml);

        // now let's try again and only serialize the document

        PropertySerializerTrie metadata = new PropertySerializerTrie();
        metadata.addSerializablePropertyName("document", false);
        MetadataPropertySerializabilityEvaluator evaluator = new MetadataPropertySerializabilityEvaluator(metadata);
        document.setEvaluator(evaluator);

        xml = serializerService.serializeDocumentToXmlForRouting(document);
        XMLAssert.assertXMLEqual(COLLECTION_DOCUMENT_PARTIAL_XML_1, xml);

        // now lets dig into some of these collections
        metadata = new PropertySerializerTrie();
        metadata.addSerializablePropertyName("document.children", false);
        metadata.addSerializablePropertyName("document.childMap.entry.string", false);
        evaluator = new MetadataPropertySerializabilityEvaluator(metadata);
        document.setEvaluator(evaluator);

        xml = serializerService.serializeDocumentToXmlForRouting(document);
        System.out.println(xml);
        XMLAssert.assertXMLEqual(COLLECTION_DOCUMENT_PARTIAL_XML_2, xml);
    }

    private WrappedDocument setupDeepDocument() {
        WrappedDocument document = new WrappedDocument(new MakeItSoPropertySerializibilityEvaluator());
        document.setDocumentProperty1("value1");
        document.setDocumentProperty2(1);

        // child 1
        Child child1 = new Child();
        document.setChild1(child1);
        child1.setProperty1("child1property1");
        Grandchild child1grandchild1 = new Grandchild();
        Grandchild child1grandchild2 = new Grandchild();
        Grandchild child1grandchild3 = new Grandchild();
        child1.setGrandchild1(child1grandchild1);
        child1.setGrandchild2(child1grandchild2);
        child1.setGrandchild3(child1grandchild3);

        // grandchild1
        child1grandchild1.setProperty1("child1grandchild1");
        child1grandchild1.setProperty2(true);
        // grandchild2
        child1grandchild2.setProperty1("child1grandchild2");
        child1grandchild2.setProperty2(false);
        // grandchild3
        child1grandchild3.setProperty1("child1grandchild3");
        child1grandchild3.setProperty2(true);

        // child 2
        Child child2 = new Child();
        document.setChild2(child2);
        child2.setProperty1("child2property1");
        Grandchild child2grandchild1 = new Grandchild();
        Grandchild child2grandchild2 = new Grandchild();
        Grandchild child2grandchild3 = new Grandchild();
        child2.setGrandchild1(child2grandchild1);
        child2.setGrandchild2(child2grandchild2);
        child2.setGrandchild3(child2grandchild3);

        // grandchild1
        child2grandchild1.setProperty1("child2grandchild1");
        child2grandchild1.setProperty2(false);
        // grandchild2
        child2grandchild2.setProperty1("child2grandchild2");
        child2grandchild2.setProperty2(true);
        // grandchild3
        child2grandchild3.setProperty1("child2grandchild3");
        child2grandchild3.setProperty2(false);

        return document;
    }

    private WrappedDocument setupCollectionDocument() {
        WrappedDocument document = new WrappedDocument();

        Child child1 = new Child("child1");
        child1.setGrandchild1(new Grandchild("child1grandchild1", true));
        child1.setGrandchild2(new Grandchild("child1grandchild2", false));
        Child child2 = new Child("child2");
        child2.setGrandchild1(new Grandchild("child2grandchild1", true));
        Child child3 = new Child("child3");

        List<Child> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        document.setChildren(children);

        document.setStringArray(new String[] { "a", "b", "c", "d"});

        Child mapChild1 = new Child("mapChild1");
        mapChild1.setGrandchild1(new Grandchild("mapChild1grandchild1", true));
        mapChild1.setGrandchild2(new Grandchild("mapChild1grandchild2", false));
        Child mapChild2 = new Child("mapChild2");
        mapChild2.setGrandchild1(new Grandchild("mapChild2grandchild1", true));
        Child mapChild3 = new Child("mapChild3");


        Map<String, Child> childMap = new HashMap<>();
        childMap.put("mapChild1", mapChild1);
        childMap.put("mapChild2", mapChild2);
        childMap.put("mapChild3", mapChild3);
        document.setChildMap(childMap);

        return document;
    }


    public static class WrappedDocument extends TransactionalDocumentBase {

        private String documentProperty1;
        private int documentProperty2;
        private Child child1;
        private Child child2;
        private Child child3;

        private List<Child> children;
        private String[] stringArray;
        private Map<String, Child> childMap;

        private transient PropertySerializabilityEvaluator evaluator;

        public WrappedDocument() {}

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

        public List<Child> getChildren() {
            return children;
        }

        public void setChildren(List<Child> children) {
            this.children = children;
        }

        public String[] getStringArray() {
            return stringArray;
        }

        public void setStringArray(String[] stringArray) {
            this.stringArray = stringArray;
        }

        public Map<String, Child> getChildMap() {
            return childMap;
        }

        public void setChildMap(Map<String, Child> childMap) {
            this.childMap = childMap;
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

        public Child() {}

        public Child(String property1) {
            this.property1 = property1;
        }

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

        public Grandchild() {}

        public Grandchild(String property1, boolean property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

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
            .append("  <kualiTransactionalDocumentInformation>")
            .append("    <documentInitiator>")
            .append("      <person class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$MockPerson\">")
            .append("        <id>johndoe</id>")
            .append("      </person>")
            .append("    </documentInitiator>")
            .append("  </kualiTransactionalDocumentInformation>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <documentProperty1>value1</documentProperty1>")
            .append("    <documentProperty2>2</documentProperty2>")
            .append("    <documentHeader>")
            .append("      <newCollectionRecord>false</newCollectionRecord>")
            .append("    </documentHeader>")
            .append("    <pessimisticLocks/>")
            .append("    <adHocRoutePersons/>")
            .append("    <adHocRouteWorkgroups/>")
            .append("    <notes/>")
            .append("    <superUserAnnotation></superUserAnnotation>")
            .append("    <newCollectionRecord>false</newCollectionRecord>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

    private static final String SIMPLE_DOCUMENT_PROPERTY_1 = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <documentProperty1>value1</documentProperty1>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

    private static final String DEEP_DOCUMENT_FULL_XML = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <kualiTransactionalDocumentInformation>")
            .append("    <documentInitiator>")
            .append("      <person class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$MockPerson\">")
            .append("        <id>johndoe</id>")
            .append("      </person>")
            .append("    </documentInitiator>")
            .append("  </kualiTransactionalDocumentInformation>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <documentProperty1>value1</documentProperty1>")
            .append("    <documentProperty2>1</documentProperty2>")
            .append("    <child1>")
            .append("      <property1>child1property1</property1>")
            .append("      <grandchild1>")
            .append("        <property1>child1grandchild1</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild1>")
            .append("      <grandchild2>")
            .append("        <property1>child1grandchild2</property1>")
            .append("        <property2>false</property2>")
            .append("      </grandchild2>")
            .append("      <grandchild3>")
            .append("        <property1>child1grandchild3</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild3>")
            .append("    </child1>")
            .append("    <child2>")
            .append("      <property1>child2property1</property1>")
            .append("      <grandchild1>")
            .append("        <property1>child2grandchild1</property1>")
            .append("        <property2>false</property2>")
            .append("      </grandchild1>")
            .append("      <grandchild2>")
            .append("        <property1>child2grandchild2</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild2>")
            .append("      <grandchild3>")
            .append("        <property1>child2grandchild3</property1>")
            .append("        <property2>false</property2>")
            .append("      </grandchild3>")
            .append("    </child2>")
            .append("    <documentHeader>")
            .append("      <newCollectionRecord>false</newCollectionRecord>")
            .append("    </documentHeader>")
            .append("    <pessimisticLocks/>")
            .append("    <adHocRoutePersons/>")
            .append("    <adHocRouteWorkgroups/>")
            .append("    <notes/>")
            .append("    <superUserAnnotation></superUserAnnotation>")
            .append("    <newCollectionRecord>false</newCollectionRecord>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

    private static final String DEEP_DOCUMENT_PARTIAL_XML = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <child1>")
            .append("      <property1>child1property1</property1>")
            .append("      <grandchild1>")
            .append("        <property1>child1grandchild1</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild1>")
            .append("      <grandchild2>")
            .append("        <property1>child1grandchild2</property1>")
            .append("        <property2>false</property2>")
            .append("      </grandchild2>")
            .append("      <grandchild3>")
            .append("        <property1>child1grandchild3</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild3>")
            .append("    </child1>")
            .append("    <child2>")
            .append("      <grandchild2>")
            .append("        <property1>child2grandchild2</property1>")
            .append("        <property2>true</property2>")
            .append("      </grandchild2>")
            .append("      <grandchild3>")
            .append("        <property1>child2grandchild3</property1>")
            .append("        <property2>false</property2>")
            .append("      </grandchild3>")
            .append("    </child2>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();



    private static final String COLLECTION_DOCUMENT_FULL_XML = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <kualiTransactionalDocumentInformation>")
            .append("    <documentInitiator>")
            .append("      <person class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$MockPerson\">")
            .append("        <id>johndoe</id>")
            .append("      </person>")
            .append("    </documentInitiator>")
            .append("  </kualiTransactionalDocumentInformation>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <documentProperty2>0</documentProperty2>")
            .append("    <children>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child1</property1>")
            .append("        <grandchild1>")
            .append("          <property1>child1grandchild1</property1>")
            .append("          <property2>true</property2>")
            .append("        </grandchild1>")
            .append("        <grandchild2>")
            .append("          <property1>child1grandchild2</property1>")
            .append("          <property2>false</property2>")
            .append("        </grandchild2>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child2</property1>")
            .append("        <grandchild1>")
            .append("          <property1>child2grandchild1</property1>")
            .append("          <property2>true</property2>")
            .append("        </grandchild1>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child3</property1>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("    </children>")
            .append("    <stringArray>")
            .append("      <string>a</string>")
            .append("      <string>b</string>")
            .append("      <string>c</string>")
            .append("      <string>d</string>")
            .append("    </stringArray>")
            .append("    <childMap>")
            .append("      <entry>")
            .append("        <string>mapChild2</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("          <property1>mapChild2</property1>")
            .append("          <grandchild1>")
            .append("            <property1>mapChild2grandchild1</property1>")
            .append("            <property2>true</property2>")
            .append("          </grandchild1>")
            .append("        </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      </entry>")
            .append("      <entry>")
            .append("        <string>mapChild3</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("          <property1>mapChild3</property1>")
            .append("        </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      </entry>")
            .append("      <entry>")
            .append("        <string>mapChild1</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("          <property1>mapChild1</property1>")
            .append("          <grandchild1>")
            .append("            <property1>mapChild1grandchild1</property1>")
            .append("            <property2>true</property2>")
            .append("          </grandchild1>")
            .append("          <grandchild2>")
            .append("            <property1>mapChild1grandchild2</property1>")
            .append("            <property2>false</property2>")
            .append("          </grandchild2>")
            .append("        </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      </entry>")
            .append("    </childMap>")
            .append("    <documentHeader>")
            .append("      <newCollectionRecord>false</newCollectionRecord>")
            .append("    </documentHeader>")
            .append("    <pessimisticLocks/>")
            .append("    <adHocRoutePersons/>")
            .append("    <adHocRouteWorkgroups/>")
            .append("    <notes/>")
            .append("    <superUserAnnotation></superUserAnnotation>")
            .append("    <newCollectionRecord>false</newCollectionRecord>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

    private static final String COLLECTION_DOCUMENT_PARTIAL_XML_1 = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <documentProperty2>0</documentProperty2>")
            .append("    <stringArray>")
            .append("      <string>a</string>")
            .append("      <string>b</string>")
            .append("      <string>c</string>")
            .append("      <string>d</string>")
            .append("    </stringArray>")
            .append("    <documentHeader>")
            .append("      <newCollectionRecord>false</newCollectionRecord>")
            .append("    </documentHeader>")
            .append("    <superUserAnnotation></superUserAnnotation>")
            .append("    <newCollectionRecord>false</newCollectionRecord>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

    private static final String COLLECTION_DOCUMENT_PARTIAL_XML_2 = new StringBuilder()
            .append("<org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .append("  <document class=\"org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest$WrappedDocument\">")
            .append("    <children>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child1</property1>")
            .append("        <grandchild1>")
            .append("          <property1>child1grandchild1</property1>")
            .append("          <property2>true</property2>")
            .append("        </grandchild1>")
            .append("        <grandchild2>")
            .append("          <property1>child1grandchild2</property1>")
            .append("          <property2>false</property2>")
            .append("        </grandchild2>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child2</property1>")
            .append("        <grandchild1>")
            .append("          <property1>child2grandchild1</property1>")
            .append("          <property2>true</property2>")
            .append("        </grandchild1>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("      <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("        <property1>child3</property1>")
            .append("      </org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child>")
            .append("    </children>")
            .append("    <childMap>")
            .append("      <entry>")
            .append("        <string>mapChild2</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child/>")
            .append("      </entry>")
            .append("      <entry>")
            .append("        <string>mapChild3</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child/>")
            .append("      </entry>")
            .append("      <entry>")
            .append("        <string>mapChild1</string>")
            .append("        <org.kuali.rice.krad.service.impl.DocumentSerializerServiceImplTest_-Child/>")
            .append("      </entry>")
            .append("    </childMap>")
            .append("  </document>")
            .append("</org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>")
            .toString();

}