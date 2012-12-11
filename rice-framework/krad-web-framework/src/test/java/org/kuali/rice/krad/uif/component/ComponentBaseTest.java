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
package org.kuali.rice.krad.uif.component;

/**
 * various tests for {@link org.kuali.rice.krad.uif.component.ComponentBase}
 *
 @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.control.FileControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UserControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.LinkField;

import java.util.TreeMap;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentBaseTest {
    private Component component;
    private TreeMap<String, String> dataAttributes;

    @Before
    public void setUp() throws Exception {
        // use an action field, since ComponentBase is abstract
        component = new Action();
        component.setId("action1");
        // used a TreeMap since it makes specific guarantees as to the order of entries
        dataAttributes = new TreeMap<String, String>();
        // set data attributes - for testing purposes only - they do not have any functional significance
        dataAttributes.put("iconTemplateName", "cool-icon-%s.png");
        dataAttributes.put("transitions", "3");
        dataAttributes.put("growing-seasons", "{summer:'hot', winter:'cold'}");
        dataAttributes.put("intervals", "{short:2, medium:5, long:13}");
        component.setDataAttributes(dataAttributes);
    }

    @Test
    /**
     * test that complex date attributes are converted into a jquery script ok
     */
    public void testGetComplexDataAttributesJs() throws Exception {
        assertNotNull(component.getComplexDataAttributesJs());
        String expected = "jQuery('#action1').data('growing-seasons', {summer:'hot', winter:'cold'});"
                + "jQuery('#action1').data('intervals', {short:2, medium:5, long:13});";
        assertEquals("complex attributes JS string did not match", expected, component.getComplexDataAttributesJs());
    }

    @Test
    /**
     * test that simple data attributes are converted into inline attributes ok
     */
    public void testGetSimpleDataAttributes() throws Exception {
        assertNotNull(component.getSimpleDataAttributes());
        String expected = " data-iconTemplateName=\"cool-icon-%s.png\" data-transitions=\"3\"";
        assertEquals("simple attributes did not match", expected, component.getSimpleDataAttributes());
    }

    @Test
    /**
     * test that get all attributes js works ok even when the data attributes are null
     */
    public void testGetAllDataAttributesJs() throws Exception {
        assertNotNull(component.getAllDataAttributesJs());
        String expected = "jQuery('#action1').data('growing-seasons', {summer:'hot', winter:'cold'});"
                + "jQuery('#action1').data('iconTemplateName', 'cool-icon-%s.png');"
                + "jQuery('#action1').data('intervals', {short:2, medium:5, long:13});"
                + "jQuery('#action1').data('transitions', 3);";
        assertEquals("all attributes JS string did not match", expected, component.getAllDataAttributesJs());
    }

    @Test
    /**
     *  test that complex date attributes are converted into a jquery script ok even when data attributes are null
     */
    public  void testGetComplexAttributesJSWhenNull () throws Exception{
        component.setDataAttributes(null);
        assertEquals("complex attributes JS string did not match", "", component.getComplexDataAttributesJs());
    }

    @Test
    /**
     * test that simple data attributes are converted into inline attributes ok  when data attributes are null
     */
    public void testGetSimpleDataAttributesWhenNull() throws Exception {
        component.setDataAttributes(null);
        assertEquals("simple attributes did not match", "", component.getSimpleDataAttributes());
    }

    @Test
    /**
     * test that get all attributes js works ok even when the data attributes are null
     */
    public void testGetAllDataAttributesJsWhenNull() throws Exception {
        component.setDataAttributes(null);
        assertEquals("simple attributes did not match", "", component.getAllDataAttributesJs());
    }

    /**
     * test that controls that need to override getComplexAttributes work as expected
     */
    @Test
    public void testGetComplexAttributesOverridingControls() {
        Component[] overridingControls = {new TextControl(), new TextAreaControl(), new FileControl(), new UserControl()};
        for (int i=0; i<overridingControls.length; i++) {
            overridingControls[i].setDataAttributes(dataAttributes);
            assertTrue(overridingControls[i].getClass() + " does not override getComplexAttributes",
                    overridingControls[i].getAllDataAttributesJs().equalsIgnoreCase(
                            overridingControls[i].getComplexDataAttributesJs()));
        }
        // other controls should have a different value for
        Component[] nonOverridingControls = {new Image(), new Action(), new LinkField(), new Message()};
        for (Component component: nonOverridingControls) {
            component.setDataAttributes(dataAttributes);
            assertFalse(component.getClass() + " should not override getComplexAttributes",
                    component.getAllDataAttributesJs().equalsIgnoreCase(
                            component.getComplexDataAttributesJs()));
        }
    }
}
