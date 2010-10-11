/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.util;

import org.kuali.rice.kns.dao.impl.DocumentDaoOjb;

/**
 * Test client implementation of Rice/KNS DocumentDaoObj.
 * Modelled from KFS's FinancialSystemDocumentDaoOjb
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClientDocumentDaoOjb extends DocumentDaoOjb {
    public static ClientDocumentDaoOjb me;

    public ClientDocumentDaoOjb() {
    	super(null, null);
        // DaoSupport makes afterPropertiesSet final...
        // fine, be that way
        me = this;
    }
}
