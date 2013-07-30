package org.kuali.rice.krad.data.jpa;

import org.junit.Test;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.PersistenceProviderAccountTestBase;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Tests JPAPersistenceProvider
 */
// avoid wrapping test in rollback since JPA requires transaction boundary to flush
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class JpaPersistenceProviderTest extends PersistenceProviderAccountTestBase {
    protected PersistenceProvider getPersistenceProvider() {
        return getKRADTestHarnessContext().getBean("kradJpaPersistenceProvider", PersistenceProvider.class);
    }

    // EclipseLink consumes the underlying exception itself and explicitly rolls back the transaction
    // resulting in just an opaque UnexpectedRollbackException coming out of Spring
    // (underlying exception is never translated by the PersistenceExceptionTranslator)
    // Internal Exception: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'ACCT_TYPE' cannot be null
    @Override
    @Test(expected=UnexpectedRollbackException.class)
    public void testSaveUnlinkedSkipLinking() {
        super.testSaveUnlinkedSkipLinking();
    }
}
