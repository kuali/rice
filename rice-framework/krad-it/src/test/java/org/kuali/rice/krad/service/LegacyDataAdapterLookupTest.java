package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.Collection;
import java.util.Map;

/**
 * Exercises LegacyDataAdapter lookup calls using LookupServiceTest test cases
 */
public class LegacyDataAdapterLookupTest extends LookupServiceTest {

    //    @Override
    //    protected <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps, boolean unbounded) {
    //        return KRADServiceLocator.getLegacyDataAdapter().findCollectionBySearchHelper(clazz, formProps, unbounded, false, null);
    //    }
    //
    //    @Override
    //    protected <T> Collection<T> findCollectionBySearchUnbounded(Class<T> clazz, Map<String, String> formProps) {
    //        return KRADServiceLocator.getLegacyDataAdapter().findCollectionBySearchHelper(clazz, formProps, true, false, null);
    //    }

    // superclass test cases ensure LegacyDataAdapter lookups are returning the correct results in legacy mode
    // (this assumes KNS is loaded in the TestHarness, and therefore legacy is enabled by default)

    // Additional test cases below explicitly disable legacy data framework, forcing the tests through
    // the JPA path in LegacyDataAdapter

    protected void disableLegacyFramework() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "false");
    }

    protected void enableLegacyFramework() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
    }

    @Test
    public void testJPALookupReturnLimits_Account() throws Exception {
        disableLegacyFramework();
        try {
            testLookupReturnLimits_Account();
        } finally {
            enableLegacyFramework();
        }
    }

    @Test
    public void testJPALookupReturnLimits_TestDataObject() throws Exception {
        disableLegacyFramework();
        try {
            testLookupReturnLimits_TestDataObject();
        } finally {
            enableLegacyFramework();
        }
    }

    @Test
    public void testJPALookupReturnDefaultLimit() throws Exception {
        disableLegacyFramework();
        try {
            testLookupReturnDefaultLimit();
        } finally {
            enableLegacyFramework();
        }
    }

    @Test
    public void testJPALookupReturnDefaultUnbounded_Account() throws Exception {
        disableLegacyFramework();
        try {
            testLookupReturnDefaultUnbounded_Account();
        } finally {
            enableLegacyFramework();
        }
    }

    @Test
    public void testJPALookupReturnDefaultUnbounded_TestDataObject() throws Exception {
        disableLegacyFramework();
        try {
            testLookupReturnDefaultUnbounded_TestDataObject();
        } finally {
            enableLegacyFramework();
        }
    }

    @Test
    @Legacy
    public void testJPADatabaseRelationshipLookup() throws Exception {
        KNSServiceLocator.getDataObjectMetaDataService().getDataObjectRelationship(
                new org.kuali.rice.krad.test.document.bo.AccountExtension(), org.kuali.rice.krad.test.document.bo.AccountExtension.class,
                "accountType", "", true, false, false);
        KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectRelationship(
                new org.kuali.rice.krad.test.document.bo.AccountExtension(), org.kuali.rice.krad.test.document.bo.AccountExtension.class,
                "accountType", "", true, false, false);
    }

}