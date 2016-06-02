/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.core.framework.persistence.jta;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Transaction interceptor which does logging at various levels 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiTransactionInterceptor extends TransactionInterceptor {

    private static final Logger LOG = Logger.getLogger(KualiTransactionInterceptor.class);
	
	@Override
    protected TransactionInfo createTransactionIfNecessary(PlatformTransactionManager tm, TransactionAttribute txAttr,
                                                           final String joinpointIdentification) {
        TransactionInfo txInfo = super.createTransactionIfNecessary(tm, txAttr, joinpointIdentification);

        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isDebugEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.debug("creating explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.debug("creating implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        return txInfo;
    }

	@Override
    protected void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        if (txInfo.getTransactionAttribute().rollbackOn(ex)) {
            LOG.fatal("Exception caught by Transaction Interceptor, this will cause a rollback at the end of the transaction.", ex);
        }

        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isDebugEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.debug("closing explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.debug("closing implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        super.completeTransactionAfterThrowing(txInfo, ex);
    }
	
    @Override
    protected void commitTransactionAfterReturning(TransactionInfo txInfo) {
        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isDebugEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.debug("committing explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.debug("committing implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        super.commitTransactionAfterReturning(txInfo);
    }



}
