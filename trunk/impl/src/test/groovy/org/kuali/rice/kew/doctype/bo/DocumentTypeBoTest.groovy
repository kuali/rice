/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.bo

import org.junit.Test
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.junit.Assert
import org.kuali.rice.kew.api.doctype.DocumentTypeContract
import org.kuali.rice.kew.api.doctype.DocumentTypePolicy
import org.kuali.rice.kew.api.doctype.DocumentType

class DocumentTypeBoTest {

    @Test
    public void testEquals() {
        DocumentTypeContract immutable = create();
        org.kuali.rice.kew.doctype.bo.DocumentType bo = org.kuali.rice.kew.doctype.bo.DocumentType.from(immutable)
        Assert.assertEquals(immutable, org.kuali.rice.kew.doctype.bo.DocumentType.to(bo))
    }

    public static DocumentType create() {
        return DocumentType.Builder.create(new DocumentTypeContract() {
            def String id = "fakeid"
            def Long versionNumber = 3
            def String name = "DocumentTypeTestName"
            def Integer documentTypeVersion = 5
            def String label = "documenttypetest label"
            def String description = "documenttypetest description"
            def String parentId = "fakeparentid"
            def boolean active = true
            def String docHandlerUrl = "http://fakedochandlerurl"
            def String helpDefinitionUrl = "http://fakehelpdefinitionurl"
            def String docSearchHelpUrl = "http://fakedocsearchhelpurl"
            def String postProcessorName = "postprocessor name"
            def String applicationId = "application id"
            def boolean current = true
            def String blanketApproveGroupId = "fakeblanketapprovegroupid"
            def String superUserGroupId = "fakesuperusergroupid"
            def Map<DocumentTypePolicy, String> getPolicies() {
               def policies = new HashMap<DocumentTypePolicy, String>();
               policies.put(DocumentTypePolicy.DEFAULT_APPROVE, "true")
               policies
            }
        }).build()
    }
}
