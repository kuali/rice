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
package org.kuali.core.document;

import org.junit.Test;
import org.kuali.Constants;
import org.kuali.core.UserSession;
import org.kuali.core.util.GlobalVariables;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

import edu.sampleu.travel.bo.FiscalOfficer;

/**
 * This class...
 * 
 * 
 */
@KNSWithTestSpringContext
public class MaintenanceDocumentTest extends KNSTestBase {

    MaintenanceDocument document;
    

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        document = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument("FiscalOfficerMaintenanceDocument");
    }

    private void setupNewAccountMaintDoc(MaintenanceDocument document) {

        FiscalOfficer fo = new FiscalOfficer();
        fo.setId(new Long(1));
        fo.setUserName("userName");

        document.getOldMaintainableObject().setBusinessObject(null);
        document.getOldMaintainableObject().setBoClass(fo.getClass());
        document.getNewMaintainableObject().setBusinessObject(fo);
        document.getNewMaintainableObject().setBoClass(fo.getClass());

    }

    private void setupEditAccountMaintDoc(MaintenanceDocument document) {

    	FiscalOfficer fo = new FiscalOfficer();
    	fo.setId(new Long(1));
    	fo.setUserName("userName");

        document.getOldMaintainableObject().setBusinessObject(fo);
        document.getOldMaintainableObject().setBoClass(fo.getClass());
        document.getNewMaintainableObject().setBusinessObject(fo);
        document.getNewMaintainableObject().setBoClass(fo.getClass());

    }

    @Test public void test_NewDoc() {

        setupNewAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_NEW_ACTION);

        assertEquals("Document should indicate New.", true, document.isNew());
        assertEquals("Document should not indicate Edit.", false, document.isEdit());
        assertEquals("Old BO should not be present.", false, document.isOldBusinessObjectInDocument());
    }

    @Test public void test_EditDoc() {

        setupEditAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_EDIT_ACTION);

        assertEquals("Document should not indicate New.", false, document.isNew());
        assertEquals("Document should indicate Edit.", true, document.isEdit());
        assertEquals("Old BO should be present.", true, document.isOldBusinessObjectInDocument());

    }

    @Test public void test_CopyDoc() {

        setupEditAccountMaintDoc(document);
        document.getNewMaintainableObject().setMaintenanceAction(Constants.MAINTENANCE_COPY_ACTION);

        assertEquals("Document should indicate New.", true, document.isNew());
        assertEquals("Document should not indicate Edit.", false, document.isEdit());
        assertEquals("Old BO should be present.", true, document.isOldBusinessObjectInDocument());

    }

}
