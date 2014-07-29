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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit tests for {@link InitializeDataFieldFromDictionaryTask}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitializeDataFieldFromDictionaryTest extends ProcessLoggingUnitTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-InitializeDataFieldFromDictionaryTest");
    }

    @Before
    public void setUp() throws Throwable {
        UifUnitTestUtils.establishMockUserSession("admin");
    }

    @After
    public void tearDown() throws Throwable {
        UifUnitTestUtils.tearDownMockUserSession();
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        UifUnitTestUtils.tearDownMockConfig();
    }

    public static class Bar implements Serializable {
        private static final long serialVersionUID = 1275816843240407178L;

        private String text;

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Foo implements Serializable {
        private static final long serialVersionUID = -8018713299377638909L;

        private Bar descr;

        public Bar getDescr() {
            return this.descr;
        }

        public void setDescr(Bar descr) {
            this.descr = descr;
        }
    }

    public static class TestForm extends UifFormBase {
        private static final long serialVersionUID = 8726078766779752200L;

        private Foo foo;
        private Bar bar;

        public Foo getFoo() {
            return this.foo;
        }

        public void setFoo(Foo foo) {
            this.foo = foo;
        }

        public Bar getBar() {
            return this.bar;
        }

        public void setBar(Bar bar) {
            this.bar = bar;
        }
    }

    public static class TestAttributeProcess implements Runnable {

        @Override
        public void run() {
            View view = ViewLifecycle.getView();
            
            String parentPath = "currentPage.items[0]";
            Component parent = ObjectPropertyUtils.getPropertyValue(view, parentPath);
            
            String foopath = "items[0]";
            DataField foofield = ObjectPropertyUtils.getPropertyValue(parent, foopath);
            ViewLifecyclePhase foophase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                    UifConstants.ViewPhases.INITIALIZE, foofield, parent, foopath, null);
            InitializeDataFieldFromDictionaryTask footask = new InitializeDataFieldFromDictionaryTask();
            footask.setElementState(foophase);

            String barpath = "items[1]";
            DataField barfield = ObjectPropertyUtils.getPropertyValue(parent, barpath);
            ViewLifecyclePhase barphase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                    UifConstants.ViewPhases.INITIALIZE, barfield, parent, barpath, null);
            InitializeDataFieldFromDictionaryTask bartask = new InitializeDataFieldFromDictionaryTask();
            bartask.setElementState(barphase);

            AttributeDefinition fooattribute = footask.findNestedDictionaryAttribute(foofield.getPropertyName());
            assertTrue(fooattribute.isRequired());

            AttributeDefinition barattribute = bartask.findNestedDictionaryAttribute(barfield.getPropertyName());
            assertFalse(barattribute.isRequired());
        }
    }

    @Test
    public void testAttribute() throws Throwable {
        ViewLifecycle
                .encapsulateLifecycle(KRADServiceLocatorWeb.getViewService().getViewById("TestView"),
                        new TestForm(), new MockHttpServletRequest(),
                        new TestAttributeProcess());
    }

}
