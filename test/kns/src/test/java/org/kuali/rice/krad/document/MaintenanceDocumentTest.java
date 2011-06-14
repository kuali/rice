/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import org.junit.Test;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.document.bo.AccountManager;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KNSConstants;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertEquals;

/**
 * This class...
 * 
 * 
 */
public class MaintenanceDocumentTest extends KNSTestCase {

    MaintenanceDocument document;
    

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        document = (MaintenanceDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountManagerMaintenanceDocument");
    }

    private void setupNewAccountMaintDoc(MaintenanceDocument document) {

        AccountManager am = new AccountManager();
        am.setAmId(new Long(1));
        am.setUserName("userName");

        document.getOldMaintainableObject().setBusinessObject(null);
        document.getOldMaintainableObject().setBoClass(am.getClass());
        document.getNewMaintainableObject().setBusinessObject(am);
        document.getNewMaintainableObject().setBoClass(am.getClass());

    }

    private void setupEditAccountMaintDoc(MaintenanceDocument document) {

    	AccountManager fo = new AccountManager();
    	fo.setAmId(new Long(1));
    	fo.setUserName("userName");

        document.getOldMaintainableObject().setBusinessObject(fo);
        document.getOldMaintainableObject().setBoClass(fo.getClass());
        document.getNewMaintainableObject().setBusinessObject(fo);
        document.getNewMaintainableObject().setBoClass(fo.getClass());

    }

    @Test public void test_NewDoc() {

        setupNewAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);

        assertEquals("Document should indicate New.", true, document.isNew());
        assertEquals("Document should not indicate Edit.", false, document.isEdit());
        assertEquals("Old BO should not be present.", false, document.isOldBusinessObjectInDocument());
    }

    @Test public void test_EditDoc() {

        setupEditAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);

        assertEquals("Document should not indicate New.", false, document.isNew());
        assertEquals("Document should indicate Edit.", true, document.isEdit());
        assertEquals("Old BO should be present.", true, document.isOldBusinessObjectInDocument());

    }

    @Test public void test_CopyDoc() {

        setupEditAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_COPY_ACTION);

        assertEquals("Document should indicate New.", true, document.isNew());
        assertEquals("Document should not indicate Edit.", false, document.isEdit());
        assertEquals("Old BO should be present.", true, document.isOldBusinessObjectInDocument());

    }

}
