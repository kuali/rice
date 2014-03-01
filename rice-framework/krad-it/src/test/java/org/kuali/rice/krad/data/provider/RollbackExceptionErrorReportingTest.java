/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.provider;

import org.eclipse.persistence.exceptions.ValidationException;
import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.springframework.orm.jpa.JpaSystemException;

import javax.persistence.PersistenceException;

import static org.junit.Assert.*;

/**
 * A test to make sure we get the "real" cause of a transaction rollback exception.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@PerSuiteUnitTestData( {
        @UnitTestData(
                sqlFiles = {
                        @UnitTestFile(filename = "classpath:testAccountType.sql", delimiter = ";")
                })
})
public class RollbackExceptionErrorReportingTest extends KRADTestCase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollbackExceptionErrorReportingTest.class);

    protected DataObjectService getDataObjectService() {
        return KradDataServiceLocator.getDataObjectService();
    }

    /**
     * Tests that if you try to change a primary key, a {@link org.springframework.orm.jpa.JpaSystemException} is thrown.
     */
    @Test
    public void changePrimaryKeyValue_Exception() {
        AccountType acctType = getDataObjectService().find(AccountType.class, "CAT");
        assertNotNull( "Error retrieving CAT account type from data object service", acctType );

        acctType.setAccountTypeCode("CLR");

        // test what object getter does
        try {
            getDataObjectService().save(acctType, PersistenceOption.FLUSH);
            fail( "The save method should have failed." );
        } catch (JpaSystemException ex) {
            // JpaSystemException should have been thrown since we are illegally trying to change the
            // primary key on an attached JPA entity
            LOG.info(ex, ex);
            assertNotNull("The thrown rollback exception should have had a cause", ex.getCause());
            assertTrue("Embedded error should have been a javax.persistence.PersistenceException.  But was: " + ex.getCause(), ex.getCause() instanceof PersistenceException);
            assertNotNull("The embedded rollback exception should have had a cause", ex.getCause().getCause());
            assertTrue("The embedded rollback exception should have been a validation exception, but was: " + ex.getCause().getCause(), ex.getCause().getCause() instanceof ValidationException);
        } catch (Exception ex) {
            fail("It should have failed with JpaSystemException");
        }
    }

}
