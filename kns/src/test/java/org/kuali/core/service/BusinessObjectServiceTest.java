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
package org.kuali.core.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.TestBase;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;

/**
 * This class tests KULRICE-1666: missing Spring mapping for ojbCollectionHelper
 * (not injected into BusinessObjectDaoTest)
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class BusinessObjectServiceTest extends TestBase {

    public BusinessObjectServiceTest() {}

    /**
     * This method tests saving a BO with a collection member
     *
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        BusinessObjectService businessObjectService = KNSServiceLocator.getBusinessObjectService();
        
        FiscalOfficer fo = new FiscalOfficer();
        fo.setUserName("bhutchin");
        List<TravelAccount> accounts = new ArrayList<TravelAccount>();
        TravelAccount account1 = new TravelAccount();
        account1.setNumber("1");
        account1.setName("account 1");
        account1.setFiscalOfficer(fo);
        accounts.add(account1);

        TravelAccount account2 = new TravelAccount();
        account2.setNumber("2");
        account2.setName("account 2");
        account2.setFiscalOfficer(fo);

        accounts.add(account2);
        fo.setAccounts(accounts);

        businessObjectService.save(fo);
    }

}
