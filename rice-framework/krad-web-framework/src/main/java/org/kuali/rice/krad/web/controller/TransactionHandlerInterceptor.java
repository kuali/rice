/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring interceptor class that will start the Rice configured transaction on pre handle (before binding
 * and controller) and commit after controller execution.
 * <p/>
 * <p>For KRAD, this interceptor should be listed first
 * (before {@link org.kuali.rice.krad.web.controller.UifControllerHandlerInterceptor})</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.core.framework.persistence.jta.Jta
 */
public class TransactionHandlerInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<TransactionStatus> context = new ThreadLocal<TransactionStatus>();

    @Autowired()
    @Qualifier("transactionManager")
    PlatformTransactionManager txManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("request");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);
        context.set(status);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        completeTransaction(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        completeTransaction(ex);
    }

    /**
     * Completes the request transaction if needed.
     *
     * @param ex any exception that might have been thrown, will cause a rollback
     */
    protected void completeTransaction(Exception ex) {
        TransactionStatus status = context.get();

        if (status == null) {
            return;
        }

        try {
            if (!status.isCompleted()) {
                if (ex == null && !status.isRollbackOnly()) {
                    txManager.commit(status);
                } else {
                    txManager.rollback(status);
                }
            }
        } finally {
            context.remove();
        }
    }
}
