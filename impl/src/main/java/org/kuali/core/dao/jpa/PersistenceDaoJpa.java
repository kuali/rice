/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.dao.jpa;

import org.kuali.core.dao.PersistenceDao;

public class PersistenceDaoJpa implements PersistenceDao {

	/**
	 * @see org.kuali.core.dao.PersistenceDao#clearCache()
	 */
	public void clearCache() {}

	/**
	 * @see org.kuali.core.dao.PersistenceDao#resolveProxy(java.lang.Object)
	 */
	public Object resolveProxy(Object o) {
		return o;
	}

	/**
	 * @see org.kuali.core.dao.PersistenceDao#retrieveAllReferences(java.lang.Object)
	 */
	public void retrieveAllReferences(Object o) {}

	/**
	 * @see org.kuali.core.dao.PersistenceDao#retrieveReference(java.lang.Object,
	 *      java.lang.String)
	 */
	public void retrieveReference(Object o, String referenceName) {}

}
