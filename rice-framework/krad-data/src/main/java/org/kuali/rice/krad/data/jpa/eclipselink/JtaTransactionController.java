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

import org.eclipse.persistence.transaction.JTATransactionController;
import org.kuali.rice.core.framework.persistence.jta.Jta;

import javax.transaction.TransactionManager;

/**
 * An implementation of EclipseLink's {@link org.eclipse.persistence.sessions.ExternalTransactionController} which will
 * utilize the JTA TransactionManager being used by the KRAD application.
 *
 * <p>
 * It locates this via a call to {@link org.kuali.rice.core.framework.persistence.jta.Jta#getTransactionManager()}.  So
 * the application must ensure that it has configured and setup JTA properly within it's application environment.
 * </p>
 *
 * <p>
 * The superclass for this class, which is part of EclipseLink, attempts to invoke the
 * {@link #acquireTransactionManager()} from the default contructor. So an attempt will be made to acquire the JTA
 * transaction manager as soon as an instance of this object is created. This means that it must be ensured that JPA is
 * enabled prior to the creation of an instance of this controller class.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JtaTransactionController extends JTATransactionController {

    /**
     * {@inheritDoc}
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        if (!Jta.isEnabled()) {
            throw new IllegalStateException("Attempting to use EclipseLink with JTA, but JTA is not configured properly"
                    + "for this KRAD application!");
        }
        return Jta.getTransactionManager();
    }

}
