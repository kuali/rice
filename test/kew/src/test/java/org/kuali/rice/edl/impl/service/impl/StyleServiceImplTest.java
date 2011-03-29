/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.edl.impl.service.impl;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.edl.impl.dao.EDocLiteDAO;
import org.kuali.rice.edl.impl.service.StyleService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.Assert.*;


/**
 * Tests StyleServiceImpl
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleServiceImplTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(StyleServiceImplTest.class);

	@Test public void testLoadXML() throws FileNotFoundException {
        loadXmlFile("style.xml");

        StyleService styleService = KEWServiceLocator.getStyleService();
        assertNotNull("Style 'an_arbitrary_style' not found", styleService.getStyle("an_arbitrary_style"));

        assertTrue("Style not found among all styles", styleService.getStyleNames().contains("an_arbitrary_style"));

        EDocLiteStyle style = styleService.getStyle("an_arbitrary_style");
        assertNotNull("'an_arbitrary_style' style not found", style);
        assertEquals("an_arbitrary_style", style.getName());
        assertNotNull(style.getActiveInd());
        assertTrue(style.getActiveInd().booleanValue());
        assertNotNull(style.getXmlContent());
    }


	/**
	 * Tests automatic import of styles from files based on configuration properties.
	 * See edl.style.widgets in common-config-defualts.xml, edl.style.gidgets in kew-test-config.xml
	 */
    @Test public void testLoadingFromConfiguredFile() {
        StyleService styleService = KEWServiceLocator.getStyleService();
        EDocLiteDAO dao = (EDocLiteDAO)KEWServiceLocator.getService("enEDocLiteDAO");

        String notThereStyle = "gidgets";
        String isThereStyle = "widgets";

        // first verify that the database doesn't contain these styles already
        assertNull(dao.getEDocLiteStyle(notThereStyle));
        assertNull(dao.getEDocLiteStyle(isThereStyle));

        // test loading an incorrectly configured style
        try {
            // the configured location for the gidgets style doesn't contain a file
            styleService.getStyle(notThereStyle);
            fail("should have thrown " + RiceRuntimeException.class.getSimpleName());
        } catch (RiceRuntimeException e) {
            LOG.info("^^^ CAUGHT EXPECTED EXCEPTION ^^^");
        } catch (Exception e) {
            fail("Wrong exception type '" + e.getClass() + "', should have been '" + RiceRuntimeException.class.getCanonicalName() + "'");
        }

        styleService.getStyle("widgets");
        // should succeed in loading it's style into the database
    }

    @Test public void testInclusions() throws FileNotFoundException, TransformerConfigurationException, TransformerException {
        loadXmlFile("style.xml");

        StyleService styleService = KEWServiceLocator.getStyleService();

        // ignoring the duplicate definition via inclusion test as the behavior seems
        // unspecified
        // XML.com claims it is an "error": http://www.xml.com/pub/a/2000/11/01/xslt/index.html
        // XLST 1.0 spec doesn't seem to specify anything regarding this: http://www.w3.org/TR/xslt
        // Michael Kay's XSLT Programmer's Reference states "...it is implementation-defined
        // whether an XSLT processor will report duplicate declarations as an error , so
        // the behavior may vary from on product to another
        // (although it is not clear to me whether he is speaking specifically of identical
        // literal definitions introduced by re-inclusion of the same exact stylesheet twice, or
        // "logical" duplication of template match criteria)
        /*Templates t = styleService.getStyleAsTranslet("test_includer");
        StringWriter w = new StringWriter();
        StreamResult result = new StreamResult(w);
        try {
            t.newTransformer().transform(new StreamSource(new StringReader("<a/>")), result);
            System.err.println(w.toString());
            fail("Exception not thrown on ambiguous template defs");
        } catch (Exception e) {
            // expected
        }*/

        Writer w = new StringWriter();
        StreamResult result = new StreamResult(w);
        Templates t = styleService.getStyleAsTranslet("test_includer2");
        t.newTransformer().transform(new StreamSource(new StringReader("<a/>")), result);
        assertEquals("oneoneoneoneone", w.toString());

        w = new StringWriter();
        result = new StreamResult(w);
        t.newTransformer().transform(new StreamSource(new StringReader("<b/>")), result);
        assertEquals("22222", w.toString());

        w = new StringWriter();
        result = new StreamResult(w);
        t = styleService.getStyleAsTranslet("test_importer");
        t.newTransformer().transform(new StreamSource(new StringReader("<a/>")), result);
        assertEquals("aaaaa", w.toString());

        w = new StringWriter();
        result = new StreamResult(w);
        t.newTransformer().transform(new StreamSource(new StringReader("<b/>")), result);
        assertEquals("BBBBB", w.toString());

        w = new StringWriter();
        result = new StreamResult(w);
        t.newTransformer().transform(new StreamSource(new StringReader("<c/>")), result);
        assertEquals("CCCCC", w.toString());
    }

    @Test public void testLoadBadDefinition() throws FileNotFoundException {
        StyleService styleService = KEWServiceLocator.getStyleService();
        try {
            styleService.loadXml(TestUtilities.loadResource(getClass(), "badstyle.xml"), null);
            fail("BadDefinition was successfully parsed.");
        } catch (RuntimeException re) {
            // should probably use type system to detect type of error, not just message string...
            // maybe we need general parsing or "semantic" validation exception
            assertTrue("Wrong exception occurred: " + re, re.getMessage().contains("Style 'style' element must contain a 'xsl:stylesheet' child element"));
        }
    }

    @Test public void testStoreStyle() {
        StyleService styleService = KEWServiceLocator.getStyleService();
        String styleXml = "<style></style>";
        try {
            styleService.saveStyle(new ByteArrayInputStream(styleXml.getBytes()));
            fail("Storing style with no name succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of name
        }
        styleXml = "<style name=\"test\"></style>";
        try {
            styleService.saveStyle(new ByteArrayInputStream(styleXml.getBytes()));
            fail("Storing style with no xsl:stylesheet element succeeded");
        } catch (WorkflowServiceErrorException wsee) {
            // expected due to lack of stylesheet content
        }
        styleXml = "<style name=\"test\"><xsl:stylesheet></xsl:stylesheet></style>";
        styleService.saveStyle(new ByteArrayInputStream(styleXml.getBytes()));
        EDocLiteStyle style = styleService.getStyle("test");
        assertNotNull(style);
        assertEquals("test", style.getName());
        assertNotNull(style);
        assertNotNull(style.getXmlContent());
    }

    /**
     * Tests the caching of "styles" in StyleServiceImpl.
     *
     * The style cache is really a cache of java.xml.transform.Templates objects which represent
     * the "compiled" stylesheets.
     */
    @Test public void testStyleCaching() throws Exception {
        loadXmlFile("style.xml");

        // try to grab the templates out of the cache, it shouldn't be cached yet
        Templates cachedTemplates = new StyleServiceImpl().fetchTemplatesFromCache("an_arbitrary_style");
        assertNull("The default style template should not be cached yet.", cachedTemplates);

        // fetch the Templates object from the service
        Templates templates = KEWServiceLocator.getStyleService().getStyleAsTranslet("an_arbitrary_style");
        assertNotNull("Templates should not be null.", templates);
        templates = KEWServiceLocator.getStyleService().getStyleAsTranslet("an_arbitrary_style");
        assertNotNull("Templates should not be null.", templates);

        // the Templates should now be cached
        cachedTemplates = new StyleServiceImpl().fetchTemplatesFromCache("an_arbitrary_style");
        assertNotNull("Templates should now be cached.", cachedTemplates);

        // the cached Templates should be the same as the Templates we fetched from the service
        assertEquals("Templates should be the same.", templates, cachedTemplates);

        // now re-import the style and the templates should no longer be cached
        loadXmlFile("style.xml");
        cachedTemplates = new StyleServiceImpl().fetchTemplatesFromCache("an_arbitrary_style");
        assertNull("After re-import, the Default style Templates should no longer be cached.", cachedTemplates);

        // re-fetch the templates from the service and verify they are in the cache
        Templates newTemplates = KEWServiceLocator.getStyleService().getStyleAsTranslet("an_arbitrary_style");
        assertNotNull("Templates should not be null.", templates);
        newTemplates = KEWServiceLocator.getStyleService().getStyleAsTranslet("an_arbitrary_style");
        assertNotNull("Templates should not be null.", templates);

        cachedTemplates = new StyleServiceImpl().fetchTemplatesFromCache("an_arbitrary_style");
        assertNotNull("Templates should now be cached.", cachedTemplates);

        // lastly, check that the newly cached templates are not the same as the original templates
        assertFalse("Old Templates should be different from new Templates.", templates.equals(newTemplates));

    }
}
