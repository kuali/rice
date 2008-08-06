/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.rice.testharness;

import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * A lifecycle for testing with database transactional rollback.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TransactionalLifecycle implements Lifecycle {
    /**
     * Name of the transaction manager to pull from the GlobalResourceLoader.
     * This will most likely be a Spring bean name.
     */
    public static final String TRANSACTION_MANAGER = "transactionManager";

    /**
     * Pulls a transaction manager out of the GlobalResourceLoader
     * @return the transaction manager in the GlobalResourceLoader
     */
    private static PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager) GlobalResourceLoader.getService(TRANSACTION_MANAGER);
    }

    private boolean started;
    private TransactionStatus TRANSACTION_STATUS;

    public boolean isStarted() {
        return started;
    }

    public void start() throws Exception {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setTimeout(3600);
        TRANSACTION_STATUS = getTransactionManager().getTransaction(defaultTransactionDefinition);
        started = true;
    }

    public void stop() throws Exception {
        getTransactionManager().rollback(TRANSACTION_STATUS);
        started = false;
    }
}