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
package org.kuali.rice.edl.impl;

import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.edl.impl.EDLController;
import org.kuali.rice.edl.impl.EDLControllerFactory;
import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.edl.impl.service.EDocLiteService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.test.BaselineTestCase;
import org.w3c.dom.Element;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests EDocLiteServiceImpl
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class EDocLiteServiceImplTest extends KEWTestCase {

	@Test public void testLoadXML() throws FileNotFoundException {
        loadXmlFile("EDocLiteContent.xml");
        loadXmlFile("edlstyle.xml");

        EDocLiteService edls = KEWServiceLocator.getEDocLiteService();
        //edls.loadXml(new FileInputStream("conf/examples/xml/EDocLiteContent.xml"));
        assertTrue("Definition not found", edls.getEDocLiteDefinitions().contains("profile"));
        assertTrue("Style not found", edls.getEDocLiteStyles().contains("Default"));
        assertEquals(1, edls.getEDocLiteAssociations().size());
        EDocLiteDefinition def = edls.getEDocLiteDefinition("profile");
        assertNotNull("'profile' definition not found", def);
        assertEquals("profile", def.getName());
        assertNotNull(def.getActiveInd());
        assertTrue(def.getActiveInd().booleanValue());
        EDocLiteStyle style = edls.getEDocLiteStyle("Default");
        assertNotNull("'Default' style not found", style);
        assertEquals("Default", style.getName());
        assertNotNull(style.getActiveInd());
        assertTrue(style.getActiveInd().booleanValue());
        assertNotNull(style.getXmlContent());
    }

    @Test public void testLoadBadDefinition() throws FileNotFoundException {
        EDocLiteService edls = KEWServiceLocator.getEDocLiteService();
        try {
            edls.loadXml(TestUtilities.loadResource(getClass(), "BadDefinition.xml"), null);
            fail("BadDefinition was successfully parsed.");
        } catch (RuntimeException re) {
            // should probably use type system to detect type of error, not just message string...
            // maybe we need general parsing or "semantic" validation exception
            assertTrue("Wrong exception occurred", re.getMessage().contains("EDocLite definition contains references to non-existent attributes"));
        }
    }

    @Test public void testStoreDefinition() {
        EDocLiteService edls = KEWServiceLocator.getEDocLiteService();
        String defXml = "<edl></edl>";
        try {
            edls.saveEDocLiteDefinition(new ByteArrayInputStream(defXml.getBytes()));
            fail("Storing edl with no name succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of name
        }
        defXml = "<edl name=\"test\"></edl>";
        edls.saveEDocLiteDefinition(new ByteArrayInputStream(defXml.getBytes()));
        EDocLiteDefinition def = edls.getEDocLiteDefinition("test");
        assertNotNull(def);
        assertEquals("test", def.getName());
    }

    @Test public void testStoreStyle() {
        EDocLiteService edls = KEWServiceLocator.getEDocLiteService();
        String styleXml = "<style></style>";
        try {
            edls.saveEDocLiteStyle(new ByteArrayInputStream(styleXml.getBytes()));
            fail("Storing style with no name succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of name
        }
        styleXml = "<style name=\"test\"></style>";
        try {
            edls.saveEDocLiteStyle(new ByteArrayInputStream(styleXml.getBytes()));
            fail("Storing style with no xsl:stylesheet element succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of stylesheet content
        }
        styleXml = "<style name=\"test\"><xsl:stylesheet></xsl:stylesheet></style>";
        edls.saveEDocLiteStyle(new ByteArrayInputStream(styleXml.getBytes()));
        EDocLiteStyle style = edls.getEDocLiteStyle("test");
        assertNotNull(style);
        assertEquals("test", style.getName());
        assertNotNull(style);
        assertNotNull(style.getXmlContent());
    }

    @Test public void testStoreAssociation() {
        EDocLiteService edls = KEWServiceLocator.getEDocLiteService();
        String assocXml = "<association></association>";
        try {
            edls.saveEDocLiteAssociation(new ByteArrayInputStream(assocXml.getBytes()));
            fail("Storing association with no docType succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of doctype
        }
        assocXml = "<association><docType></docType></association>";
        try {
            edls.saveEDocLiteAssociation(new ByteArrayInputStream(assocXml.getBytes()));
            fail("Storing association with empty docType succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to emtpy doctype value
        }
        assocXml = "<association><docType>foobar</docType></association>";
        edls.saveEDocLiteAssociation(new ByteArrayInputStream(assocXml.getBytes()));
        EDocLiteAssociation assoc = edls.getEDocLiteAssociation("foobar");
        assertNull("Inactive Association was found", assoc);

        assocXml = "<association><docType>foobar</docType><active>true</active></association>";
        edls.saveEDocLiteAssociation(new ByteArrayInputStream(assocXml.getBytes()));
        assoc = edls.getEDocLiteAssociation("foobar");
        assertNotNull("Association was not found", assoc);
        assertEquals("foobar", assoc.getEdlName());
        assertNull(assoc.getDefinition());
        assertNull(assoc.getStyle());

        List<EDocLiteAssociation> assocs = edls.getEDocLiteAssociations();
        assertEquals(1, assocs.size());
        assoc = assocs.get(0);
        assertEquals("foobar", assoc.getEdlName());
        assertNull(assoc.getDefinition());
        assertNull(assoc.getStyle());
        assertNotNull(assoc.getActiveInd());
        assertTrue(assoc.getActiveInd().booleanValue());

        assocXml = "<association><style>style name</style><definition>definition name</definition><docType>foobar</docType><active>true</active></association>";
        edls.saveEDocLiteAssociation(new ByteArrayInputStream(assocXml.getBytes()));
        assoc = edls.getEDocLiteAssociation("foobar");
        assertNotNull("Association was not found", assoc);
        assertEquals("foobar", assoc.getEdlName());
        assertEquals("definition name", assoc.getDefinition());
        assertEquals("style name", assoc.getStyle());

        assocs = edls.getEDocLiteAssociations();
        assertEquals(1, assocs.size());
        assoc = (EDocLiteAssociation) assocs.get(0);
        assertNotNull("Association was not found", assoc);
        assertEquals("foobar", assoc.getEdlName());
        assertEquals("definition name", assoc.getDefinition());
        assertEquals("style name", assoc.getStyle());
        assertNotNull(assoc.getActiveInd());
        assertTrue(assoc.getActiveInd().booleanValue());
    }

    /**
     * Tests the caching behavior of configs in EDocLiteServiceImpl.  The config cache is a
     * map of XML org.w3c.dom.Element to config classname mappings.  This cache is, in-reality, maintained
     * by the EDLControllerFactory.
     */
    @Test public void testConfigCaching() throws Exception {
    	ConfigContext.getCurrentContextConfig().putProperty(Config.EDL_CONFIG_LOCATION, "classpath:org/kuali/rice/kew/edl/TestEDLConfig.xml");

    	loadXmlFile("EDocLiteContent.xml");
        loadXmlFile("edlstyle.xml");
        loadXmlFile("widgets.xml");

    	Map config = EDLControllerFactory.fetchConfigFromCache("profile");
    	assertNull("Config should not be cached initially.", config);

    	// fetch the edl controller which should result in caching
		EDLController edlController = KEWServiceLocator.getEDocLiteService().getEDLController("EDocLiteDocType");
		assertNotNull(edlController);

		config = EDLControllerFactory.fetchConfigFromCache("profile");
    	assertNotNull("Config should now be cached.", config);

    	// compare the config in the cache with the config on the EDLController
    	assertEquals("Config processors should be the same.", edlController.getConfigProcessors().size(), config.size());
    	assertEquals(1, config.size());
    	Element key1 = (Element)edlController.getConfigProcessors().keySet().iterator().next();
    	Element key2 = (Element)config.keySet().iterator().next();
    	assertEquals("Key values should be the same", XmlJotter.jotNode(key1), XmlJotter.jotNode(key2));
    	assertEquals("Values should be the same", edlController.getConfigProcessors().get(key1), config.get(key2));

    	// now import the EDocLite again and it should be cleared from the cache
    	loadXmlFile("EDocLiteContent.xml");
    	config = EDLControllerFactory.fetchConfigFromCache("profile");
    	assertNull("Config should no longer be cached.", config);

    	// fetch again and we should be back in action
		edlController = KEWServiceLocator.getEDocLiteService().getEDLController("EDocLiteDocType");
		assertNotNull(edlController);
		config = EDLControllerFactory.fetchConfigFromCache("profile");
    	assertNotNull("Config should now be cached.", config);
    }

    /**
     * Tests the caching of "styles" in EDocLiteServiceImpl.
     *
     * The style cache is really a cache of java.xml.transform.Templates objects which represent
     * the "compiled" stylesheets.
     */
    @Test public void testStyleCaching() throws Exception {
    	ConfigContext.getCurrentContextConfig().putProperty(Config.EDL_CONFIG_LOCATION, "classpath:org/kuali/rice/kew/edl/TestEDLConfig.xml");

    	loadXmlFile("EDocLiteContent.xml");
        loadXmlFile("edlstyle.xml");
        loadXmlFile("widgets.xml");

        // try to grab the templates out of the cache, it shouldn't be cached yet
//        Templates cachedTemplates = new EDocLiteServiceImpl().fetchTemplatesFromCache("Default");
//        assertNull("The default style template should not be cached yet.", cachedTemplates);

        // fetch the Templates object from the service
        EDocLiteAssociation association = KEWServiceLocator.getEDocLiteService().getEDocLiteAssociation("EDocLiteDocType");
        assertNull("We should be using the Default style.", association.getStyle());
        Templates templates = KEWServiceLocator.getEDocLiteService().getStyleAsTranslet(association.getStyle());
        assertNotNull("Templates should not be null.", templates);

        // the Templates should now be cached
//        cachedTemplates = new EDocLiteServiceImpl().fetchTemplatesFromCache("Default");
//        assertNotNull("Templates should now be cached.", cachedTemplates);

//        // the cached Templates should be the same as the Templates we fetched from the service
//        assertEquals("Templates should be the same.", templates, cachedTemplates);

        // now re-import the style and the templates should no longer be cached
        loadXmlFile("edlstyle.xml");
//        cachedTemplates = new EDocLiteServiceImpl().fetchTemplatesFromCache("Default");
//        assertNull("After re-import, the Default style Templates should no longer be cached.", cachedTemplates);

        // re-fetch the templates from the service and verify they are in the cache
        Templates newTemplates = KEWServiceLocator.getEDocLiteService().getStyleAsTranslet(association.getStyle());
        assertNotNull("Templates should not be null.", templates);
//        cachedTemplates = new EDocLiteServiceImpl().fetchTemplatesFromCache("Default");
//        assertNotNull("Templates should now be cached.", cachedTemplates);

        // lastly, check that the newly cached templates are not the same as the original templates
        assertFalse("Old Templates should be different from new Templates.", templates.equals(newTemplates));

    }
}
