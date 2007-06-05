/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Spring transaction manager.
 */
public class KualiTransactionManager extends JtaTransactionManager {
	private static final long serialVersionUID = 1L;

	/*
	 * JtaTransactionManager will throw an exception on commit if the
	 * transaction has already been rolled back. This is an override to catch
	 * the exception and resume processing.
	 * 
	 * @see org.springframework.transaction.jta.JtaTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
	 */
	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		try {
			super.doCommit(status);
		} catch (UnexpectedRollbackException ex) {
			// resume
		}
	}

}
