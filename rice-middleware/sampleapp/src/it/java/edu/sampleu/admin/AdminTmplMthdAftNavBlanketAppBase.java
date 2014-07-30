package edu.sampleu.admin;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class AdminTmplMthdAftNavBlanketAppBase extends AdminTmplMthdAftNavCreateNewBase {

    @Override
    protected String getMenuLinkLocator() {
        return AdminTmplMthdAftNavBase.ADMIN_LOCATOR;
    }

    @Override
    protected String getCreateNewLinkLocator() {
        return AdminTmplMthdAftNavBase.CREATE_NEW_LOCATOR;
    }

    protected void testBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = verifyDocInitiated();
        assertBlanketApproveButtonsPresent();
        createNewLookupDetails();
        blanketApproveTest(docId);
    }

    @Test
    public void testBlanketAppBookmark() throws Exception {
        testBlanketApprove();
        passed();
    }

    @Test
    public void testBlanketAppNav() throws Exception {
        testBlanketApprove();
        passed();
    }
}
