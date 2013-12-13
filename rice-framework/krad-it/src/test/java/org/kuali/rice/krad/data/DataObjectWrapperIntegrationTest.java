package org.kuali.rice.krad.data;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.rice.krad.data.jpa.testbo.AnotherReferencedDataObject;
import org.kuali.rice.krad.data.jpa.testbo.ReferencedDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.YetAnotherReferencedDataObject;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * An integration test which tests various aspects of the default DataObjectWrapper implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB) // set it to CLEAR_DB so we can have more control over the transactions within this test
public class DataObjectWrapperIntegrationTest extends KRADTestCase {

    @Test
    public void testRefreshRelationship_MultipleForeignKeys() {
        TestDataObject testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc");
        testDataObject = getDataObjectService().save(testDataObject);
        assertEquals("abc", testDataObject.getPrimaryKeyProperty());

        // now refresh the "referencedObject" relationship, "stringProperty" represents the internal key here
        assertNull(testDataObject.getReferencedObject());
        assertNull(testDataObject.getStringProperty());

        // right now we have no foreign keys so this should essentially do nothing
        DataObjectWrapper<?> wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("referencedObject");
        // should still be null
        assertNull(testDataObject.getReferencedObject());
        assertNull(testDataObject.getStringProperty());

        // next, let's save a value for referenced object
        ReferencedDataObject referencedDataObject = new ReferencedDataObject();
        referencedDataObject.setStringProperty("cba");
        referencedDataObject.setSomeOtherStringProperty("efg");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // if we set the string property on the TestDataObject and refresh, it should fetch the missing
        // ReferencedDataObject for us
        testDataObject.setStringProperty("cba");
        assertNull(testDataObject.getReferencedObject());
        wrapper.refreshRelationship("referencedObject");
        // now referenced object should not be null any longer
        assertNotNull(testDataObject.getReferencedObject());
        assertEquals("efg", testDataObject.getReferencedObject().getSomeOtherStringProperty());

        // next, try the other direction with null attribute foreign keys and a non-null relationship object
        testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc2");
        referencedDataObject = new ReferencedDataObject();
        referencedDataObject.setStringProperty("cba2");
        referencedDataObject.setSomeOtherStringProperty("efg2");
        referencedDataObject = getDataObjectService().save(referencedDataObject);
        testDataObject.setReferencedObject(referencedDataObject);
        testDataObject = getDataObjectService().save(testDataObject);
        // verify that our foreign key attribute is null
        assertNull(testDataObject.getStringProperty());
        assertNotNull(testDataObject.getReferencedObject());

        // now that it's saved, let's update the referenced data object value and make sure the refresh works
        // first, let's refetch the referenced object and update it
        referencedDataObject = getDataObjectService().find(ReferencedDataObject.class, "cba2");
        assertEquals("efg2", referencedDataObject.getSomeOtherStringProperty());
        referencedDataObject.setSomeOtherStringProperty("efg3");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // it's updated now, so let's verify the original testDataObject is out of date
        assertEquals("efg2", testDataObject.getReferencedObject().getSomeOtherStringProperty());
        wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("referencedObject");
        // now it should be updated!
        assertEquals("efg3", testDataObject.getReferencedObject().getSomeOtherStringProperty());
        // the refresh should also synchronize the foreign key attribute value, so our string property should have a
        // proper value now
        assertEquals("cba2", testDataObject.getStringProperty());

    }

    @Test
    public void testRefreshRelationship_CompoundForeignKeys() {
        TestDataObject testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc");
        testDataObject = getDataObjectService().save(testDataObject);
        assertEquals("abc", testDataObject.getPrimaryKeyProperty());

        // now refresh the "anotherReferencedObject" relationship, "stringProperty" and "dateProperty" represent the internal key here
        assertNull(testDataObject.getAnotherReferencedObject());
        assertNull(testDataObject.getStringProperty());
        assertNull(testDataObject.getDateProperty());

        // right now we have no foreign keys so this should essentially do nothing
        DataObjectWrapper<?> wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("anotherReferencedObject");
        // should still be null
        assertNull(testDataObject.getAnotherReferencedObject());
        assertNull(testDataObject.getStringProperty());
        assertNull(testDataObject.getDateProperty());

        Date date = newDateWithTimeAtStartOfDay();

        // next, let's save a value for referenced object
        AnotherReferencedDataObject referencedDataObject = new AnotherReferencedDataObject();
        referencedDataObject.setStringProperty("cba");
        referencedDataObject.setDateProperty(date);
        referencedDataObject.setSomeOtherStringProperty("efg");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // if we set the internal keys on the TestDataObject and refresh, it should fetch the missing
        // AnotherReferencedDataObject for us
        testDataObject.setStringProperty("cba");
        testDataObject.setDateProperty(date);
        assertNull(testDataObject.getAnotherReferencedObject());
        wrapper.refreshRelationship("anotherReferencedObject");
        // now referenced object should not be null any longer
        assertNotNull(testDataObject.getAnotherReferencedObject());
        assertEquals("efg", testDataObject.getAnotherReferencedObject().getSomeOtherStringProperty());

        // next, try the other direction with null attribute foreign keys and a non-null relationship object
        testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc2");
        referencedDataObject = new AnotherReferencedDataObject();
        referencedDataObject.setStringProperty("cba2");
        Date date2 = newDateWithTimeAtStartOfDay();
        referencedDataObject.setDateProperty(date2);
        referencedDataObject.setSomeOtherStringProperty("efg2");
        referencedDataObject = getDataObjectService().save(referencedDataObject);
        testDataObject.setAnotherReferencedObject(referencedDataObject);
        testDataObject = getDataObjectService().save(testDataObject);
        // verify that our foreign key attributes are null
        assertNull(testDataObject.getStringProperty());
        assertNull(testDataObject.getDateProperty());
        assertNotNull(testDataObject.getAnotherReferencedObject());

        // now that it's saved, let's update the referenced data object value and make sure the refresh works
        // first, let's refetch the referenced object and update it
        Map<String, Object> compoundKey = new LinkedHashMap<String, Object>();
        compoundKey.put("stringProperty", "cba2");
        compoundKey.put("dateProperty", date2);
        referencedDataObject = getDataObjectService().find(AnotherReferencedDataObject.class, new CompoundKey(compoundKey));
        assertEquals("efg2", referencedDataObject.getSomeOtherStringProperty());
        referencedDataObject.setSomeOtherStringProperty("efg3");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // it's updated now, so let's verify the original testDataObject is out of date
        assertEquals("efg2", testDataObject.getAnotherReferencedObject().getSomeOtherStringProperty());
        wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("anotherReferencedObject");
        // now it should be updated!
        assertEquals("efg3", testDataObject.getAnotherReferencedObject().getSomeOtherStringProperty());
        // the refresh should also synchronize the foreign key attribute values, so our string and date property should
        // have a proper value now
        assertEquals("cba2", testDataObject.getStringProperty());
        assertEquals(date2, testDataObject.getDateProperty());

    }

    private Date newDateWithTimeAtStartOfDay() {
        return DateTime.now().withTimeAtStartOfDay().toDate();
    }

    @Test
    public void testRefreshRelationship_OneToOne_SingleForeignKey() {
        TestDataObject testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc");
        testDataObject = getDataObjectService().save(testDataObject);
        assertEquals("abc", testDataObject.getPrimaryKeyProperty());

        // now refresh the "yetAnotherReferencedObject" relationship, the pk represents the internal key here
        assertNull(testDataObject.getAnotherReferencedObject());

        // right now we have no foreign keys so this should essentially do nothing
        DataObjectWrapper<?> wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("yetAnotherReferencedObject");
        // should still be null
        assertNull(testDataObject.getAnotherReferencedObject());

        // next, let's save a value for referenced object
        YetAnotherReferencedDataObject referencedDataObject = new YetAnotherReferencedDataObject();
        referencedDataObject.setId("abc");
        referencedDataObject.setSomeOtherStringProperty("efg");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // now, if we refresh it should fetch the missing AnotherReferencedDataObject for us
        assertNull(testDataObject.getYetAnotherReferencedObject());
        wrapper.refreshRelationship("yetAnotherReferencedObject");
        // now referenced object should not be null any longer
        assertNotNull(testDataObject.getYetAnotherReferencedObject());
        assertEquals("efg", testDataObject.getYetAnotherReferencedObject().getSomeOtherStringProperty());

        // now that it's saved, let's update the referenced data object value and make sure the refresh works
        // first, let's refetch the referenced object and update it
        referencedDataObject = getDataObjectService().find(YetAnotherReferencedDataObject.class, "abc");
        assertEquals("efg", referencedDataObject.getSomeOtherStringProperty());
        referencedDataObject.setSomeOtherStringProperty("efg2");
        referencedDataObject = getDataObjectService().save(referencedDataObject);

        // it's updated now, so let's verify the original testDataObject is out of date
        assertEquals("efg", testDataObject.getYetAnotherReferencedObject().getSomeOtherStringProperty());
        wrapper = getDataObjectService().wrap(testDataObject);
        wrapper.refreshRelationship("yetAnotherReferencedObject");
        // now it should be updated!
        assertEquals("efg2", testDataObject.getYetAnotherReferencedObject().getSomeOtherStringProperty());
        // pk should still be the same
        assertEquals("abc", testDataObject.getPrimaryKeyProperty());
    }

    @Test
    public void testRefreshRelationship_ErrorCases() {
        TestDataObject testDataObject = new TestDataObject();
        testDataObject.setPrimaryKeyProperty("abc");
        testDataObject = getDataObjectService().save(testDataObject);
        assertEquals("abc", testDataObject.getPrimaryKeyProperty());

        DataObjectWrapper<?> wrapper = getDataObjectService().wrap(testDataObject);

        try {
            wrapper.refreshRelationship("badReferenceName");
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {}

        try {
            wrapper.refreshRelationship("");
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {}

        // use an invalid foreign key, the refresh should essentially do nothing
        testDataObject.setStringProperty("thisDontPointToNuthin!");
        assertEquals("thisDontPointToNuthin!", testDataObject.getStringProperty());
        assertNull(testDataObject.getReferencedObject());

    }


    private DataObjectService getDataObjectService() {
        return KradDataServiceLocator.getDataObjectService();
    }

}
