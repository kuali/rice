package org.kuali.rice.krad.data.provider.jpa;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectExtension;
import org.kuali.rice.krad.data.jpa.testbo.YetAnotherReferencedDataObject;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

// avoid wrapping test in rollback since JPA requires transaction boundary to flush
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class ExtensionForAnnotationTest extends KRADTestCase {
    protected PersistenceProvider getPersistenceProvider() {
        return getKRADTestHarnessContext().getBean("kradTestJpaPersistenceProvider", PersistenceProvider.class);
    }

    @Override
    protected List<String> getPerTestTablesToClear() {
        ArrayList<String> tables = new ArrayList<String>();
        tables.add( "KRTST_TEST_TABLE_T" );
        tables.add( "KRTST_TEST_TABLE_EXT_T" );
        tables.add( "KRTST_TEST_YARDO_T" );
        return tables;
    }

    protected TestDataObject createTestDataObject(String key) {
        TestDataObject dataObject = new TestDataObject();
        dataObject.setPrimaryKeyProperty(key);
        dataObject.setStringProperty("aString");

        TestDataObjectExtension extension = new TestDataObjectExtension();
        extension.setPrimaryKeyProperty(dataObject);
        extension.setExtensionProperty("extraData");
        dataObject.setExtension(extension);

        YetAnotherReferencedDataObject yardo = new YetAnotherReferencedDataObject();
        yardo.setId(key);
        yardo.setSomeOtherStringProperty("otherString");
        dataObject.setYetAnotherReferencedObject(yardo);

        return dataObject;
    }

    @Test
    public void testCreateDataObjectWithExtension() {
        TestDataObject dataObject = createTestDataObject("1");

        getPersistenceProvider().save(dataObject);

        getKRADTestHarnessContext().getBean("kradTestEntityManagerFactory", EntityManagerFactory.class).getCache().evictAll();

        dataObject = getPersistenceProvider().find(TestDataObject.class, "1");
        TestDataObjectExtension extension = getPersistenceProvider().find(TestDataObjectExtension.class, "1");
        YetAnotherReferencedDataObject yardo = getPersistenceProvider().find(YetAnotherReferencedDataObject.class, "1");

        Assert.assertNotNull("TestDataObject 1 not saved", dataObject);
        Assert.assertNotNull("TestDataObjectExtension 1 not saved", extension);
        Assert.assertNotNull("YetAnotherReferencedDataObject 1 not saved", yardo);
    }

    @Test
    public void testRetrieveDataObjectWithExtension() {
        TestDataObject dataObject = createTestDataObject("2");

        getPersistenceProvider().save(dataObject);

        getKRADTestHarnessContext().getBean("kradTestEntityManagerFactory", EntityManagerFactory.class).getCache().evictAll();

        dataObject = getPersistenceProvider().find(TestDataObject.class, "2");
        YetAnotherReferencedDataObject yardo = dataObject.getYetAnotherReferencedObject();
        Assert.assertNotNull("yardo reference was null - not loaded automatically", yardo );
        Assert.assertNotNull("Extension reference was null - extension not loaded automatically", dataObject.getExtension() );
        Assert.assertTrue("extension was not a TestDataObjectExtension: " + dataObject.getExtension().getClass(), dataObject.getExtension() instanceof TestDataObjectExtension );
    }
}
