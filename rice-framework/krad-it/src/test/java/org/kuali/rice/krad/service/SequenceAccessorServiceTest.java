package org.kuali.rice.krad.service;

import com.google.common.base.Function;
import org.apache.ojb.broker.PersistenceBroker;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.dao.jdbc.SequenceAccessorDaoJdbc;
import org.kuali.rice.krad.service.impl.SequenceAccessorServiceImpl;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.BOContainingPerson;
import org.kuali.rice.krad.test.document.OjbOnly;
import org.kuali.rice.krad.test.document.bo.JPADataObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests SequenceAccessorService can generate ids for sequence
 * by looking up proper class/ORM mapping
 */
public class SequenceAccessorServiceTest extends KRADTestCase {
    private static final String ARBITRARY_SEQUENCE = "trvl_id_seq";
    private static Class<?> JPAONLYDATAOBJECT = JPADataObject.class;
    private static Class<?> OJBONLYDATAOBJECT = BOContainingPerson.class;

    // spy we use for verifying underlying DatabasePlatform calls
    private DatabasePlatform dbPlatformSpy;

    @Before
    public void configureDatabasePlatformSpy() throws Exception {
        DatabasePlatform dbPlatform = KRADServiceLocatorInternal.getService("dbPlatform");
        dbPlatformSpy = spy(dbPlatform);
        SequenceAccessorDaoJdbc dao = KRADServiceLocatorInternal.getService("sequenceAccessorDao");
        dao.setDbPlatform(dbPlatformSpy);
        SequenceAccessorServiceImpl service = extractTarget(KRADServiceLocator.getSequenceAccessorService());
        service.setSequenceAccessorDao(dao);

        // reset legacy flag
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);
    }

    // extract underlying implementation from proxy
    protected <T> T extractTarget(Object o) throws Exception {
        return (T) ((Advised) o).getTargetSource().getTarget();
    }

    // invoke DatabasePlatform directly with JDBC to establish known sequence value
    protected Long getNextSequenceValue(String sequenceName) {
        JdbcTemplate template = new JdbcTemplate(getKRADTestHarnessContext().getBean("riceDataSource", DataSource.class));
        return template.execute(new ConnectionCallback<Long>() {
            @Override
            public Long doInConnection(Connection con) throws SQLException, DataAccessException {
                return KRADServiceLocator.getDatabasePlatform().getNextValSQL(ARBITRARY_SEQUENCE, con);
            }
        });
    }

    protected int printSeqRows(String name) {
        JdbcTemplate template = new JdbcTemplate(getKRADTestHarnessContext().getBean("riceDataSource", DataSource.class));
        List<Map<String, Object>> rows = template.queryForList("select * from " + name);
        System.err.println("ROWS:");
        for (Map<String, Object> row: rows) {
            System.err.println(row);
        }
        return rows.size();
    }

    protected void assertSequenceIdGeneration(String sequenceName, Class dataObjectClass, Function<DatabasePlatform, ?> verifyInvocation) {
        //System.err.println("Before all");
        //assertEquals(0, printSeqRows(sequenceName));
        Long curValue = getNextSequenceValue(sequenceName);
        //System.err.println("After initial");
        //assertEquals(1, printSeqRows(sequenceName));
        Long nextValue = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, dataObjectClass);
        //System.err.println("After one increment");
        //assertEquals(1, printSeqRows(sequenceName));
        verifyInvocation.apply(verify(dbPlatformSpy));
        //System.err.println("After verification of one increment");
        //assertEquals(curValue + 1, (long) nextValue);
        nextValue = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, dataObjectClass);
        //System.err.println("After two increments");
        //assertEquals(1, printSeqRows(sequenceName));
        verifyInvocation.apply(verify(dbPlatformSpy, times(2)));
        assertEquals(curValue + 2, (long) nextValue);
        assertEquals(curValue + 3, (long) getNextSequenceValue(ARBITRARY_SEQUENCE));
        //System.err.println("After verification of two increments");
        //assertEquals(1, printSeqRows(sequenceName));
    }

    /**
     * Test entities are mapped to ORM technology as we expect
     */
    @Test
    public void testModuleServiceDataObjectMapping() {
        KualiModuleService kms = KRADServiceLocatorWeb.getKualiModuleService();
        ModuleService appModuleService = (ModuleService) getKRADTestHarnessContext().getBean("testModule");
        assertSame(appModuleService, kms.getResponsibleModuleService(JPADataObject.class));
        assertSame(appModuleService, kms.getResponsibleModuleService(BOContainingPerson.class));
        // NOTE: the application module service is responsible for the framework class
        // due to JPA PersistenceUnit framework entity merging...
        assertTrue(appModuleService.isResponsibleFor(DocumentHeader.class));
        // ...but framework module service wins because it's registered first:
        ModuleService frameworkModuleService = KRADServiceLocatorInternal.getService("kradApplicationModule");
        assertSame(frameworkModuleService, kms.getResponsibleModuleService(DocumentHeader.class));
    }

    /**
     * Confirm that the entities we are testing are correctly identified as legacy (OJB) or non-legacy (JPA)
     */
    @Test
    public void testDataObjectLegacyDetection() {
        assertTrue(LegacyUtils.useLegacy(OJBONLYDATAOBJECT));
        assertFalse(LegacyUtils.useLegacy(JPAONLYDATAOBJECT));
    }

    /**
     * Test generating a new sequence value for a legacy/OJB-mapped entity
     */
    @Test
    public void testGenerateOJBDataObjectSequenceId() {
        // DatabasePlatform getNextValSQL called with PersistenceBroker since this is an OJB/legacy class
        assertSequenceIdGeneration(ARBITRARY_SEQUENCE, OjbOnly.class, new Function<DatabasePlatform, Object>() {
            @Override
            public Object apply(@Nullable DatabasePlatform input) {
                input.getNextValSQL(eq(ARBITRARY_SEQUENCE), isA(PersistenceBroker.class));
                return null;
            }
        });
    }

    /**
     * Test generating a new sequence value for a non-legacy/JPA-mapped entity
     */
    @Test
    public void testGenerateJPADataObjectSequenceId() {
        // DatabasePlatform getNextValSQL called with EntityManager since this is a JPA/non-legacy class
        assertSequenceIdGeneration(ARBITRARY_SEQUENCE, JPADataObject.class, new Function<DatabasePlatform, Object>() {
            @Override
            public Object apply(@Nullable DatabasePlatform input) {
                input.getNextValSQL(eq(ARBITRARY_SEQUENCE), isA(EntityManager.class));
                return null;
            }
        });
    }

    /**
     * Test generating a new sequence value without specifying class - because in legacy context, defaults to legacy
     * framework entity ORM mapping (OJB)
     */
    @Test
    public void testGenerateFrameworkSequenceIdLegacy() {
        // DatabasePlatform getNextValSQL called with PersistenceBroker when in legacy data context since
        // DocumentHeader.class is mapped as both legacy and non-legacy
        LegacyUtils.beginLegacyContext();
        try {
            assertSequenceIdGeneration(ARBITRARY_SEQUENCE, DocumentHeader.class, new Function<DatabasePlatform, Object>() {
                @Override
                public Object apply(@Nullable DatabasePlatform input) {
                    input.getNextValSQL(eq(ARBITRARY_SEQUENCE), isA(PersistenceBroker.class));
                    return null;
                }
            });
        } finally {
            LegacyUtils.endLegacyContext();
        }
    }

    /**
     * Test generating a new sequence value without specifying class - since not in legacy context, should use JPA
     */
    @Test
    public void testGenerateFrameworkSequenceIdNonLegacy() {
        // DatabasePlatform getNextValSQL called with EntityManager when *not* in legacy data context since
        // DocumentHeader.class is mapped as both legacy and non-legacy
        LegacyUtils.beginLegacyContext();
        try {
            assertSequenceIdGeneration(ARBITRARY_SEQUENCE, DocumentHeader.class, new Function<DatabasePlatform, Object>() {
                @Override
                public Object apply(@Nullable DatabasePlatform input) {
                    input.getNextValSQL(eq(ARBITRARY_SEQUENCE), isA(PersistenceBroker.class));
                    return null;
                }
            });
        } finally {
            LegacyUtils.endLegacyContext();
        }
    }

    /**
     * Disable legacy mode and verify sequence value is generated via the JPA codepath
     */
    //@Ignore("TODO: Pending details on https://jira.kuali.org/browse/KULRICE-9102 current internal ModuleService does not have its own EntityManager/PU")
    @Test
    public void testGenerateFrameworkSequenceId_LegacyDataFrameworkDisabled() {
        // disable legacy mode completely
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "false");
        assertSequenceIdGeneration(ARBITRARY_SEQUENCE, DocumentHeader.class, new Function<DatabasePlatform, Object>() {
            @Override
            public Object apply(@Nullable DatabasePlatform input) {
                input.getNextValSQL(eq(ARBITRARY_SEQUENCE), isA(EntityManager.class));
                return null;
            }
        });
    }
}