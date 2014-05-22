/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.bo;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeAttributeContract;
import org.kuali.rice.kew.api.doctype.DocumentTypeContract;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentTypeBoTest {

    private static final List<DocumentTypePolicy> DOCUMENT_TYPE_POLICIES = new ArrayList<DocumentTypePolicy>();
    static {
        DOCUMENT_TYPE_POLICIES.add(new DocumentTypePolicy(null, org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_ROUTE.getCode(), true));
        DOCUMENT_TYPE_POLICIES.add(new DocumentTypePolicy(null, org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_SAVE.getCode(), false));
        DOCUMENT_TYPE_POLICIES.add(new DocumentTypePolicy(null, org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ENROUTE_ERROR_SUPPRESSION.getCode(), false));
        DOCUMENT_TYPE_POLICIES.add(new DocumentTypePolicy(null, org.kuali.rice.kew.api.doctype.DocumentTypePolicy.DOC_SEARCH_TARGET.getCode(), false));
        DOCUMENT_TYPE_POLICIES.get(3).setPolicyStringValue("_top");
    };

    @Test
    public void testEquals() {
        DocumentTypeContract immutable = create();
        org.kuali.rice.kew.doctype.bo.DocumentType bo = org.kuali.rice.kew.doctype.bo.DocumentType.from(immutable);
        Assert.assertEquals(immutable, org.kuali.rice.kew.doctype.bo.DocumentType.to(bo));
    }

    /**
     * Tests policy map generation: DocumentTypePolicy -> (policyValue Boolean).toString()
     */
    @Test
    public void testGetPolicies() {
        org.kuali.rice.kew.doctype.bo.DocumentType dt = new org.kuali.rice.kew.doctype.bo.DocumentType();
        dt.setDocumentTypePolicies(DOCUMENT_TYPE_POLICIES);
        Map<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> policies =
                new HashMap<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String>();
        policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_ROUTE, Boolean.TRUE.toString());
        policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_SAVE, Boolean.FALSE.toString());
        policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ENROUTE_ERROR_SUPPRESSION, Boolean.FALSE.toString());
        policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.DOC_SEARCH_TARGET, "_top");
        assertEquals(policies, dt.getPolicies());
    }

    /**
     * Tests that getPolicyByName works, specifically DT hierarchy traversal
     */
    @Test
    public void testGetPolicyByName_boolean() {
        org.kuali.rice.kew.doctype.bo.DocumentType parentdt = spy(new org.kuali.rice.kew.doctype.bo.DocumentType());
        parentdt.setDocumentTypePolicies(DOCUMENT_TYPE_POLICIES);
        doReturn(null).when(parentdt).getParentDocType();

        org.kuali.rice.kew.doctype.bo.DocumentType childdt = spy(new org.kuali.rice.kew.doctype.bo.DocumentType());
        doReturn(parentdt).when(childdt).getParentDocType();

        DocumentTypePolicy policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_ROUTE.getCode(), false);
        assertTrue(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_SAVE.getCode(), true);
        assertFalse(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ENROUTE_ERROR_SUPPRESSION.getCode(), true);
        assertFalse(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        // test default
        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ALLOW_SU_FINAL_APPROVAL.getCode(), true) ;
        assertTrue(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());
    }

    /**
     * Tests that getPolicyByName works, specifically DT hierarchy traversal
     */
    @Test
    public void testGetPolicyByName_string() {
        org.kuali.rice.kew.doctype.bo.DocumentType parentdt = spy(new org.kuali.rice.kew.doctype.bo.DocumentType());
        parentdt.setDocumentTypePolicies(DOCUMENT_TYPE_POLICIES);
        doReturn(null).when(parentdt).getParentDocType();

        org.kuali.rice.kew.doctype.bo.DocumentType childdt = spy(new org.kuali.rice.kew.doctype.bo.DocumentType());
        doReturn(parentdt).when(childdt).getParentDocType();

        DocumentTypePolicy policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_ROUTE.getCode(), "MUST_ROUTE");
        assertTrue(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.INITIATOR_MUST_SAVE.getCode(), "MUST_SAVE");
        assertFalse(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ENROUTE_ERROR_SUPPRESSION.getCode(), "ERROR_SUPPRESSION");
        assertFalse(policy.getPolicyValue());
        assertTrue(policy.getInheritedFlag());

        // test default
        policy = childdt.getPolicyByName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.ALLOW_SU_FINAL_APPROVAL.getCode(), "DEFAULT VALUE");
        assertTrue(policy.getPolicyValue());
        assertEquals("DEFAULT VALUE", policy.getPolicyStringValue());
        assertTrue(policy.getInheritedFlag());
    }

    public static DocumentType create() {
        return DocumentType.Builder.create(new DocumentTypeContract() {
            @Override
            public String getName() {
                return "DocumentTypeTestName";
            }
            @Override
            public Integer getDocumentTypeVersion() {
                return 5;
            }
            @Override
            public String getLabel() {
                return "documenttypetest label";
            }
            @Override
            public String getDescription() {
                return "documenttypetest description";
            }
            @Override
            public String getParentId() {
                return "fakeparentid";
            }
            @Override
            public boolean isActive() {
                return true;
            }
            @Override
            public String getHelpDefinitionUrl() {
                return "http://fakehelpdefinitionurl";
            }
            @Override
            public String getDocSearchHelpUrl() {
                return "http://fakedocsearchhelpurl";
            }
            @Override
            public String getPostProcessorName() {
                return "postprocessor name";
            }
            @Override
            public String getApplicationId() {
                return "application id";
            }
            @Override
            public boolean isCurrent() {
                return true;
            }
            @Override
            public String getBlanketApproveGroupId() {
                return "fakeblanketapprovegroupid";
            }
            @Override
            public String getSuperUserGroupId() {
                return "fakesuperusergroupid";
            }
            @Override
            public Map<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> getPolicies() {
                Map<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> policies =
                        new HashMap<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String>();
                policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.DEFAULT_APPROVE, "true");
                return policies;
            }
            @Override
            public List<? extends DocumentTypeAttributeContract> getDocumentTypeAttributes() {
                return Collections.emptyList();
            }
            @Override
            public String getResolvedDocumentHandlerUrl() {
                return "http://fakedochandlerurl";
            }
            @Override
            public String getUnresolvedDocHandlerUrl() {
                return "http://fakedochandlerurl";
            }
            @Override
            public String getAuthorizer() {
                return "fakeDocumentTypeAuthorizer";
            }
            @Override
            public String getId() {
                return "fakeid";
            }
            @Override
            public Long getVersionNumber() {
                return 3L;
            }
        }).build();
    }

}
