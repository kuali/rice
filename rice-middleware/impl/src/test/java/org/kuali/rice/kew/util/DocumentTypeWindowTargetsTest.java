/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link DocumentTypeWindowTargets}
 *
 * @author Eric Westfall
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({KEWServiceLocator.class})
public class DocumentTypeWindowTargetsTest {

    private static final String _BLANK = "_blank";
    private static final String _SELF = "_self";
    private static final String _PARENT = "_parent";
    private static final String _TOP = "_top";

    // we'll set up the following mock document type hierarchy:
    // A -> B -> D
    // A -> B -> C
    // E -> F -> G -> H
    // I -> J
    private static final List<DocType> DOC_TYPES = new ArrayList<>();
    private static final Map<String, DocType> DOC_TYPE_INDEX = new HashMap<>();
    private static final Map<String, DocumentType> DOCUMENT_TYPE_INDEX = new HashMap<>();
    static {
        DocType A = new DocType("A", null);
        DocType B = new DocType("B", "A");
        DocType C = new DocType("C", "B");
        DocType D = new DocType("D", "B");
        DocType E = new DocType("E", null);
        DocType F = new DocType("F", "E");
        DocType G = new DocType("G", "F");
        DocType H = new DocType("H", "G");
        DocType I = new DocType("I", null);
        DocType J = new DocType("J", "I");
        DOC_TYPES.add(A);
        DOC_TYPES.add(B);
        DOC_TYPES.add(C);
        DOC_TYPES.add(D);
        DOC_TYPES.add(E);
        DOC_TYPES.add(F);
        DOC_TYPES.add(G);
        DOC_TYPES.add(H);
        DOC_TYPES.add(I);
        DOC_TYPES.add(J);
        for (DocType docType : DOC_TYPES) {
            DOC_TYPE_INDEX.put(docType.name, docType);
        }
        DocumentType docTypeA = new DocumentType();
        docTypeA.setName("A");
        DOCUMENT_TYPE_INDEX.put("A", docTypeA);
        DocumentType docTypeB = new DocumentType();
        docTypeB.setName("B");
        DOCUMENT_TYPE_INDEX.put("B", docTypeB);
        DocumentType docTypeC = new DocumentType();
        docTypeC.setName("C");
        DOCUMENT_TYPE_INDEX.put("C", docTypeC);
        DocumentType docTypeD = new DocumentType();
        docTypeD.setName("D");
        DOCUMENT_TYPE_INDEX.put("D", docTypeD);
        DocumentType docTypeE = new DocumentType();
        docTypeE.setName("E");
        DOCUMENT_TYPE_INDEX.put("E", docTypeE);
        DocumentType docTypeF = new DocumentType();
        docTypeF.setName("F");
        DOCUMENT_TYPE_INDEX.put("F", docTypeF);
        DocumentType docTypeG = new DocumentType();
        docTypeG.setName("G");
        DOCUMENT_TYPE_INDEX.put("G", docTypeG);
        DocumentType docTypeH = new DocumentType();
        docTypeH.setName("H");
        DOCUMENT_TYPE_INDEX.put("H", docTypeH);
        DocumentType docTypeI = new DocumentType();
        docTypeI.setName("I");
        DOCUMENT_TYPE_INDEX.put("I", docTypeI);
        DocumentType docTypeJ = new DocumentType();
        docTypeJ.setName("J");
        DOCUMENT_TYPE_INDEX.put("J", docTypeJ);
        // setup a doc type policy on "I"
        DocumentTypePolicy targetPolicy = new DocumentTypePolicy();
        targetPolicy.setDocumentType(docTypeI);
        targetPolicy.setPolicyName(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.DOC_SEARCH_TARGET.getCode());
        targetPolicy.setPolicyStringValue(_PARENT);
        docTypeI.setDocumentTypePolicies(Collections.singletonList(targetPolicy));
    }

    @Mock
    private DocumentTypeService documentTypeService;

    @Before
    public void setUp() {
        Mockito.when(documentTypeService.findParentNameByName(Matchers.anyString())).then(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DocType docType = (DOC_TYPE_INDEX.get(invocationOnMock.getArguments()[0].toString()));
                return docType == null ? null : docType.parentName;
            }
        });
        Mockito.when(documentTypeService.findByName(Matchers.anyString())).then(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (DOCUMENT_TYPE_INDEX.get(invocationOnMock.getArguments()[0].toString()));
            }
        });
        PowerMockito.mockStatic(KEWServiceLocator.class);
        Mockito.when(KEWServiceLocator.getDocumentTypeService()).thenReturn(documentTypeService);
    }

    @Test
    public void testNullTargetSpec() throws Exception {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets(null, null, _BLANK, "rlt", documentTypeService);
        // should always produce the default
        assertEquals(_BLANK, targets.getDocumentTarget("whatever"));
    }

    @Test
    public void testBlankTargetSpec() throws Exception {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("", "", _SELF, "rlt", documentTypeService);
        // should always produce the default
        assertEquals(_SELF, targets.getDocumentTarget("whatever"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDefaultDocumentTarget() throws Exception {
        new DocumentTypeWindowTargets("", "", null, "rlt", documentTypeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDefaultRouteLogTarget() throws Exception {
        new DocumentTypeWindowTargets("", "", "dt", null, documentTypeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlankDefaultDocumentTarget() throws Exception {
        new DocumentTypeWindowTargets("", "", "", "rlt", documentTypeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlankDefaultRouteLogTarget() throws Exception {
        new DocumentTypeWindowTargets("", "", "dt", "", documentTypeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDocumentTypeService() throws Exception {
        new DocumentTypeWindowTargets("", "", _TOP, "rlt", null);
    }

    /**
     * This tests the case where there is no default in the target spec, but a default provided when constructing
     * the DocumentTypeWindowTargets object
     */
    @Test
    public void testFullTargetSpec_NoDefault() throws Exception {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("B:_blank,G:_top,E:_parent", "B:custom,G:_parent,E:_top", _SELF, "rlt", documentTypeService);

        // document targets

        // everything under B in the hierarchy should be _blank
        assertEquals(_BLANK, targets.getDocumentTarget("B"));
        assertEquals(_BLANK, targets.getDocumentTarget("D"));
        assertEquals(_BLANK, targets.getDocumentTarget("C"));
        // A should fall back to the default
        assertEquals(_SELF, targets.getDocumentTarget("A"));
        // everything under G in the hierarchy should be _top
        assertEquals(_TOP, targets.getDocumentTarget("G"));
        assertEquals(_TOP, targets.getDocumentTarget("H"));
        // everything under E in the hierarchy should be _parent
        assertEquals(_PARENT, targets.getDocumentTarget("E"));
        assertEquals(_PARENT, targets.getDocumentTarget("F"));

        // route log targets

        // everything under B in the hierarchy should be "custom"
        assertEquals("custom", targets.getRouteLogTarget("B"));
        assertEquals("custom", targets.getRouteLogTarget("D"));
        assertEquals("custom", targets.getRouteLogTarget("C"));
        // A should fall back to the default which is "rlt" for the route log
        assertEquals("rlt", targets.getRouteLogTarget("A"));
        // everything under G in the hierarchy should be _parent
        assertEquals(_PARENT, targets.getRouteLogTarget("G"));
        assertEquals(_PARENT, targets.getRouteLogTarget("H"));
        // everything under E in the hierarchy should be _top
        assertEquals(_TOP, targets.getRouteLogTarget("E"));
        assertEquals(_TOP, targets.getRouteLogTarget("F"));
    }

    /**
     * This tests the case where there is a defined default in the target spec (using a wildcard), in which case the
     * default provided in the DocumentTypeWindowTargets object does nothing
     */
    @Test
    public void testFullTargetSpec_WithDefinedDefault() throws Exception {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("B:_blank,G:_top,*:_parent", null, _SELF, "rlt", documentTypeService);
        // everything under B in the hierarchy should be _blank
        assertEquals(_BLANK, targets.getDocumentTarget("B"));
        assertEquals(_BLANK, targets.getDocumentTarget("D"));
        assertEquals(_BLANK, targets.getDocumentTarget("C"));
        // A should fall back to the defined default
        assertEquals(_PARENT, targets.getDocumentTarget("A"));
        // everything under G in the hierarchy should be _top
        assertEquals(_TOP, targets.getDocumentTarget("G"));
        assertEquals(_TOP, targets.getDocumentTarget("H"));
        // everything under E in the hierarchy should be _parent
        assertEquals(_PARENT, targets.getDocumentTarget("E"));
        assertEquals(_PARENT, targets.getDocumentTarget("F"));

        // "rlt" should be the default for route log except for I because of it's document type policy
        assertEquals("rlt", targets.getRouteLogTarget("A"));
        assertEquals("rlt", targets.getRouteLogTarget("E"));
        assertEquals(_PARENT, targets.getRouteLogTarget("I"));

    }

    /**
     * Tests that invalid target specs are essentially ignored, or the bad components of them are ignored
     */
    @Test
    public void testInvalidTargetSpec_IsIgnored() {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("blahblahblah", "blahblahblah", _BLANK, "rlt", documentTypeService);
        assertEquals(_BLANK, targets.getDocumentTarget("A"));
        assertEquals("rlt", targets.getRouteLogTarget("A"));

        targets = new DocumentTypeWindowTargets("blah,blah,B:_top", "blah,blah,B:_parent", _BLANK, "rlt", documentTypeService);
        assertEquals(_TOP, targets.getDocumentTarget("B"));
        assertEquals(_TOP, targets.getDocumentTarget("C"));
        assertEquals(_TOP, targets.getDocumentTarget("D"));
        assertEquals(_BLANK, targets.getDocumentTarget("A"));

        assertEquals(_PARENT, targets.getRouteLogTarget("B"));
        assertEquals(_PARENT, targets.getRouteLogTarget("C"));
        assertEquals(_PARENT, targets.getRouteLogTarget("D"));
        assertEquals("rlt", targets.getRouteLogTarget("A"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTarget_NullValue() {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("", "", _SELF, "rlt", documentTypeService);
        targets.getDocumentTarget(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTarget_BlankValue() {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("", "", _SELF, "rlt", documentTypeService);
        targets.getDocumentTarget("");
    }

    @Test
    public void testGetTarget_WithDocumentTypePolicy() {
        DocumentTypeWindowTargets targets = new DocumentTypeWindowTargets("", "", _BLANK, "rlt", documentTypeService);
        assertEquals(_PARENT, targets.getDocumentTarget("I"));
        assertEquals(_PARENT, targets.getDocumentTarget("J"));

        targets = new DocumentTypeWindowTargets("J:_top", null, _BLANK, "rlt", documentTypeService);
        assertEquals(_PARENT, targets.getDocumentTarget("I"));
        assertEquals(_TOP, targets.getDocumentTarget("J"));
    }

    private static class DocType {
        private final String name;
        private final String parentName;
        DocType(String name, String parentName) {
            this.name = name;
            this.parentName = parentName;
        }
    }


}
