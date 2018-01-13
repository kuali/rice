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
package org.kuali.rice.krad.dao.proxy;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.SessionDocument;
import org.kuali.rice.krad.dao.SessionDocumentDao;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SessionDocumentDaoProxy implements SessionDocumentDao {

	private static Logger LOG = Logger.getLogger(SessionDocumentDaoProxy.class);

	private SessionDocumentDao sessionDocumentDaoJpa;
    private SessionDocumentDao sessionDocumentDaoOjb;
	
    private SessionDocumentDao getDao(Class clazz) {
    	return LegacyUtils.useLegacy(clazz) ? sessionDocumentDaoOjb : sessionDocumentDaoJpa;
    }
    
	public void setSessionDocumentDaoJpa(SessionDocumentDao sessionDocumentDaoJpa) {
		this.sessionDocumentDaoJpa = sessionDocumentDaoJpa;
	}

	public void setSessionDocumentDaoOjb(SessionDocumentDao sessionDocumentDaoOjb) {
		this.sessionDocumentDaoOjb = sessionDocumentDaoOjb;
	}

	public void purgeAllSessionDocuments(Timestamp expirationDate) {
	    	getDao(SessionDocument.class).purgeAllSessionDocuments(expirationDate);
	    }

}
