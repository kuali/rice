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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.eclipse.persistence.exceptions.TransactionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.framework.persistence.jta.Jta;
import org.kuali.rice.krad.data.jpa.eclipselink.JtaTransactionController;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import static org.junit.Assert.*;

/**
 * Tests the {@link JtaTransactionController}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class JtaTransactionControllerTest {

    @Mock private TransactionManager transactionManager;
    @Mock private UserTransaction userTransaction;

    /**
     * When JTA is disabled, attempts to invoke this method will trigger an IllegalStateException which the superclass
     * will wrap in a {@link TransactionException}.
     * @throws Exception
     */
    @Test
    public void testAcquireTransactionManager_JtaDisabled() throws Exception {
        assertFalse(Jta.isEnabled());
        try {
            new JtaTransactionController();
            fail("A TransactionException should have been thrown");
        } catch (TransactionException e) {
            assertEquals(TransactionException.ERROR_OBTAINING_TRANSACTION_MANAGER, e.getErrorCode());
            assertEquals(IllegalStateException.class, e.getInternalException().getClass());
        }
    }

    @Test
    public void testAcquireTransactionManager() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        try {
            assertTrue(Jta.isEnabled());
            JtaTransactionController controller = new JtaTransactionController();
            assertEquals(transactionManager, controller.acquireTransactionManager());
        } finally {
            Jta.reset();
        }
    }


}
