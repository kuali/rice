/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.util.spring;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class KualiTransactionInterceptor extends org.springframework.transaction.interceptor.TransactionInterceptor {
    private static final Logger LOG = Logger.getLogger(KualiTransactionInterceptor.class);


    /**
     * @see org.springframework.transaction.interceptor.TransactionAspectSupport#createTransactionIfNecessary(java.lang.reflect.Method,
     *      java.lang.Class)
     */
    protected TransactionInfo createTransactionIfNecessary(Method method, Class targetClass) {
        TransactionInfo txInfo = super.createTransactionIfNecessary(method, targetClass);

        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isInfoEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.info("creating explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.info("creating implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        return txInfo;
    }

    /**
     * @see org.springframework.transaction.interceptor.TransactionAspectSupport#doCloseTransactionAfterThrowing(org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo,
     *      java.lang.Throwable)
     */
    protected void doCloseTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isInfoEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.info("closing explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.info("closing implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        super.completeTransactionAfterThrowing(txInfo, ex);
    }

    /**
     * @see org.springframework.transaction.interceptor.TransactionAspectSupport#doCommitTransactionAfterReturning(org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo)
     */
    protected void doCommitTransactionAfterReturning(TransactionInfo txInfo) {
        // using INFO level since DEBUG level turns on the (somewhat misleading) log statements of the superclass
        if (logger.isInfoEnabled()) {
            if (txInfo != null) {
                TransactionStatus txStatus = txInfo.getTransactionStatus();
                if (txStatus != null) {
                    if (txStatus.isNewTransaction()) {
                        LOG.info("committing explicit transaction for " + txInfo.getJoinpointIdentification());
                    }
                    else {
                        if (txStatus instanceof DefaultTransactionStatus) {
                            DefaultTransactionStatus dtxStatus = (DefaultTransactionStatus) txStatus;

                            if (dtxStatus.isNewSynchronization()) {
                                LOG.info("committing implicit transaction for " + txInfo.getJoinpointIdentification());
                            }
                        }
                    }
                }
            }
        }

        super.commitTransactionAfterReturning(txInfo);
    }
}
