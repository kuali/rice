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

import java.util.LinkedHashMap;

import org.junit.Test;
import org.kuali.RicePropertyConstants;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

@KNSWithTestSpringContext
public class DocumentAuthorizerBaseTest extends KNSTestBase {

    private static final String SUPERVISOR_USER = "ABNEY";
    private static final String SUPERVISOR_UNIVERSAL = "2237202707";
    private static final String NONSUPER_USER = "BARTH";
    private static final String NONSUPER_UNIVERSAL = "5998202207";

    private DocumentAuthorizer documentAuthorizer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        documentAuthorizer = new DocumentAuthorizerBase();
    }

    // following is the Supervisor & Initiator grid

    // ## Supervisor? UserIsInitiator? ApprovalRequested? canSupervise Result
    // -----------------------------------------------------------------------------------
    // A true true true true
    //  
    // B true true false false
    //
    // C true false true true
    //
    // D true false false true
    //
    // E false * * false
    //
    @Test public void testCanSuperviseAsInitiatorA() {

        DocumentActionFlags flags;
        Document document;

        // scenario A
        document = new MockDocument(getSuperUser(), true);
        flags = documentAuthorizer.getDocumentActionFlags(document, getSuperUser());
        assertTrue(flags.getCanSupervise());

        // scenario B
        document = new MockDocument(getSuperUser(), false);
        flags = documentAuthorizer.getDocumentActionFlags(document, getSuperUser());
        assertFalse(flags.getCanSupervise());

        // scenario C
        document = new MockDocument(getNonSuperUser(), true);
        flags = documentAuthorizer.getDocumentActionFlags(document, getSuperUser());
        assertTrue(flags.getCanSupervise());

        // scenario D
        document = new MockDocument(getNonSuperUser(), false);
        flags = documentAuthorizer.getDocumentActionFlags(document, getSuperUser());
        assertTrue(flags.getCanSupervise());

    }

    private UniversalUser getSuperUser() {
        return new UniversalUser(SUPERVISOR_UNIVERSAL, SUPERVISOR_USER, true);
    }

    private UniversalUser getNonSuperUser() {
        return new UniversalUser(NONSUPER_UNIVERSAL, NONSUPER_USER, false);
    }

    private class MockDocument extends DocumentBase {

        private MockDocument() {
            super();
        }

        public MockDocument(UniversalUser initiator, boolean isApprovalRequested) {
            this();
            this.documentNumber = "1234567890";
            this.documentHeader.setWorkflowDocument(new MockWorkflowDocument(initiator, isApprovalRequested));
        }

        @Override
        protected LinkedHashMap toStringMapper() {
            LinkedHashMap map = new LinkedHashMap();
            map.put("class", "MockDocument");
            map.put(RicePropertyConstants.DOCUMENT_NUMBER, documentNumber);
            map.put("initiator", documentHeader.getWorkflowDocument().getInitiatorNetworkId());
            return map;
        }

        public boolean getAllowsCopy() {
            return false;
        }

    }

    private class MockWorkflowDocument extends org.kuali.core.impls.MockWorkflowDocument {

        private UniversalUser initiator;
        private boolean approvalRequested;

        private MockWorkflowDocument() {
        };

        public MockWorkflowDocument(UniversalUser initiator, boolean isApprovalRequested) {
            this.initiator = initiator;
            this.approvalRequested = isApprovalRequested;
        }

        public String getInitiatorNetworkId() {
            return initiator.getPersonUserIdentifier();
        }

        public boolean isApprovalRequested() {
            return approvalRequested;
        }

        public boolean userIsInitiator(org.kuali.core.bo.user.UniversalUser user) {
            return initiator.getPersonUniversalIdentifier().equalsIgnoreCase(user.getPersonUniversalIdentifier());
        }

    }

    private class UniversalUser extends org.kuali.core.bo.user.UniversalUser {
        private boolean supervisor;

        private UniversalUser() {
        };

        public UniversalUser(String universalId, String userId, boolean supervisor) {
            this.setPersonUniversalIdentifier(universalId);
            this.setPersonUserIdentifier(userId);
            this.supervisor = supervisor;
        }

        @Override
        public boolean isSupervisorUser() {
            return supervisor;
        }
    }
}
