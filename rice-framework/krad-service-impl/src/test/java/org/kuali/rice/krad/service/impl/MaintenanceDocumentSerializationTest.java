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
package org.kuali.rice.krad.service.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoaderTestUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.annotation.SerializationContext;
import org.kuali.rice.krad.data.provider.annotation.Serialized;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;

import javax.persistence.Transient;
import java.io.Serializable;

import static org.mockito.Mockito.*;

/**
 * Test to verify that our custom {@link org.kuali.rice.krad.data.provider.annotation.Serialized} annotation as well
 * as {@link javax.persistence.Transient} influence the metadata and effect serialization of fields as intended.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentSerializationTest {

    // fields are default visibility as this class is extended

    static final TestKradChildOjb child = new TestKradChildOjb("child", "content1", "content2", "content3", "content4", "content5", "content6");
    static final TestKradDataObj dataObject = new TestKradDataObj("dataObject", child, child, child, child, child, child);
    static final XmlObjectSerializerServiceImpl xmlObjectSerializerServiceImpl = new XmlObjectSerializerServiceImpl();
    static LegacyDataAdapter mockLegacyDataAdapter;

    /**
     * Wire up services and mocks, and plunk them the GRL as needed to get the maintenance document serialization
     * functionality up and working.
     */
    @BeforeClass
    public static void setupServices() {
        // create a mock LegacyDataAdapter that will answer all the questions it is asked appropriately
        mockLegacyDataAdapter = mock(LegacyDataAdapter.class);
        when(mockLegacyDataAdapter.areNotesSupported(any(Class.class))).thenReturn(Boolean.FALSE);
        when(mockLegacyDataAdapter.isProxied(anyObject())).thenReturn(Boolean.FALSE);

        xmlObjectSerializerServiceImpl.setLegacyDataAdapter(mockLegacyDataAdapter);

        // create a DataObjectSerializerServiceImpl that will be used in KRAD to determine whether a given field is serialized
        DataObjectSerializerServiceImpl dataObjectSerializerService = new DataObjectSerializerServiceImpl();
        dataObjectSerializerService.setLegacyDataAdapter(mockLegacyDataAdapter);

        // put needed mock and hand wired services into the GRL
        GlobalResourceLoaderTestUtils.addMockService(KRADServiceLocatorWeb.LEGACY_DATA_ADAPTER, mockLegacyDataAdapter);
        GlobalResourceLoaderTestUtils.addMockService(KRADServiceLocator.KRAD_SERIALIZER_SERVICE, dataObjectSerializerService);
        GlobalResourceLoaderTestUtils.addMockService(KRADServiceLocator.XML_OBJECT_SERIALIZER_SERVICE, xmlObjectSerializerServiceImpl);
    }

    /**
     * Run the serialization / deserialization cycle on the maintenance doc and verify that the annotations properly
     * control which fields are serialized (and thus survive that cycle).
     */
    @Test
    public void kradSerializationAnnotationTest() {
        // rig our maintainable up with the minimal parts needed to test serialization
        MaintainableImpl maintainable = new MaintainableImpl();
        maintainable.setLegacyDataAdapter(mockLegacyDataAdapter);
        maintainable.setDataObject(dataObject);

        MaintenanceDocumentBase kradMaintenanceDoc = new MaintenanceDocumentBase();

        // use our maintenance doc to serialize / deserialize the data object
        kradMaintenanceDoc.setNewMaintainableObject(maintainable);
        kradMaintenanceDoc.populateXmlDocumentContentsFromMaintainables();
        kradMaintenanceDoc.populateMaintainablesFromXmlDocumentContents();
        TestKradDataObj reconstitutedDataObject =
                (TestKradDataObj) kradMaintenanceDoc.getNewMaintainableObject().getDataObject();

        // verify that the fields we expected to survive did, and otherwise are null

        Assert.assertNull("annotated to disable serialization, should be null", reconstitutedDataObject.getChild2());
        Assert.assertNull("annotated as transient with nothing to override, should be null", reconstitutedDataObject.getChild4());
        Assert.assertNull("annotated as transient and to disable serialization, should be null", reconstitutedDataObject.getChild5());

        Assert.assertEquals("This field annotated to disable serialization for a different SerializationType must survive",
                dataObject.getName(), reconstitutedDataObject.getName());

        Assert.assertNotNull("Child object with no field annotations, it must survive",
                reconstitutedDataObject.getChild1());
        Assert.assertNotNull("Annotated to be serialized, it must survive", reconstitutedDataObject.getChild3());
        Assert.assertNotNull("Transient, but annotated to be serialized, it must survive", reconstitutedDataObject.getChild6());

        // verify that the annotations on the child object were respected too

        Assert.assertNull("annotated to disable serialization, should be null", reconstitutedDataObject.getChild1().getContent2());
        Assert.assertNull("annotated as transient with nothing to override, should be null", reconstitutedDataObject.getChild1().getContent4());
        Assert.assertNull("annotated as transient and to disable serialization, should be null", reconstitutedDataObject.getChild1().getContent5());

        Assert.assertEquals("with no field annotations, it must survive", child.getContent1(), reconstitutedDataObject.getChild1().getContent1());
        Assert.assertEquals("Annotated to be serialized, it must survive", child.getContent3(), reconstitutedDataObject.getChild1().getContent3());
        Assert.assertEquals("Transient, but annotated to be serialized, it must survive", child.getContent6(),
                reconstitutedDataObject.getChild1().getContent6());
    }
}

/**
 * Test class with fields that have all the permutations of @Transient, @Serialized(enabled=true)
 * and @Serialized(enabled=false) to verify the functionality in each case.
 */
class TestKradDataObj extends PersistableBusinessObjectBase implements Serializable {

    // annotated, but for a different SerializationType -- should be ignored
    @Serialized(enabled = false, forContexts = { /* SerializationContext.WORKFLOW */ })
    private String name;

    // a child object without any special annotations.  Should survive.
    private TestKradChildOjb child1;

    // a child object that is annotated to not be serialized in our specific SerializationType.  Should not survive.
    @Serialized(enabled = false, forContexts = { SerializationContext.MAINTENANCE })
    private TestKradChildOjb child2;


    // a child object that is annotated to be serialized.  Should survive.
    @Serialized(enabled = true)
    private TestKradChildOjb child3;

    // a child object that is marked JPA transient.  Should not survive.
    @Transient
    private TestKradChildOjb child4;

    // a child object that is marked JPA transient, and is annotated to not be serialized.  Should not survive.
    @Serialized(enabled = false)
    @Transient
    private TestKradChildOjb child5;

    // a child object that is marked JPA transient, and is annotated to be serialized for all SerializationTypes (which
    // is the default SerializationType).  Should survive.
    @Serialized(enabled = true, forContexts = { SerializationContext.ALL })
    @Transient
    private TestKradChildOjb child6;

    TestKradDataObj(String name, TestKradChildOjb child1, TestKradChildOjb child2, TestKradChildOjb child3,
            TestKradChildOjb child4, TestKradChildOjb child5, TestKradChildOjb child6) {
        this.name = name;
        this.child1 = child1;
        this.child2 = child2;
        this.child3 = child3;
        this.child4 = child4;
        this.child5 = child5;
        this.child6 = child6;
    }

    public String getName() {
        return name;
    }

    public TestKradChildOjb getChild1() {
        return child1;
    }

    public TestKradChildOjb getChild2() {
        return child2;
    }

    public TestKradChildOjb getChild3() {
        return child3;
    }

    public TestKradChildOjb getChild4() {
        return child4;
    }

    public TestKradChildOjb getChild5() {
        return child5;
    }

    public TestKradChildOjb getChild6() {
        return child6;
    }
}

/**
 * Test class with fields that have all the permutations of @Transient, @Serialized(enabled=true)
 * and @Serialized(enabled=false) to verify the functionality in each case.
 */
class TestKradChildOjb implements Serializable {

    private String name;

    private String content1;

    @Serialized(enabled = false)
    private String content2;

    @Serialized(enabled = true)
    private String content3;

    @Transient
    private String content4;

    @Serialized(enabled = false)
    @Transient
    private String content5;

    @Serialized(enabled = true)
    @Transient
    private String content6;

    TestKradChildOjb(String name, String content1, String content2, String content3, String content4, String content5,
            String content6) {
        this.name = name;
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
        this.content4 = content4;
        this.content5 = content5;
        this.content6 = content6;
    }

    public String getName() {
        return name;
    }

    public String getContent1() {
        return content1;
    }

    public String getContent2() {
        return content2;
    }

    public String getContent3() {
        return content3;
    }

    public String getContent4() {
        return content4;
    }

    public String getContent5() {
        return content5;
    }

    public String getContent6() {
        return content6;
    }
}


