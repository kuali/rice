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
package org.kuali.rice.kew.xml.export;

import org.jdom.Document;
import org.junit.Test;
import org.kuali.rice.core.util.XmlHelper;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests exporting Styles
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class StyleXmlExporterTest extends XmlExporterTestCase {

	public void testExportActionConfig() throws Exception {
		// action config has no help entries
    }

    public void testExportEngineConfig() throws Exception {
    	// engine config has no help entries
    }

    /**
     * This will test exporting some styles
     */
    @Test public void testExport() throws Exception {
        loadXmlFile("StyleExportConfig.xml");
        assertExport();
    }

    protected void assertExport() throws Exception {
        List<EDocLiteStyle> oldStyles = KEWServiceLocator.getStyleService().getStyles();

        System.err.println("Styles: " + oldStyles.size());

        ExportDataSet dataSet = new ExportDataSet();
        dataSet.getStyles().addAll(oldStyles);

        byte[] xmlBytes = KEWServiceLocator.getXmlExporterService().export(dataSet);
        assertTrue("XML should be non empty.", xmlBytes != null && xmlBytes.length > 0);
        // quick check to verify that not only is the XML non-empty, but that it might actually contain an attempt at an exported style
        // (otherwise the XML could not contain any styles, and the test would pass with a false positive even though the export never
        // exported anything)
        assertTrue("XML does not contain exported style", new String(xmlBytes).contains("<styles "));
        assertTrue("XML does not contain exported style", new String(xmlBytes).contains("<style name=\"an_arbitrary_style\">"));

        // import the exported xml
        loadXmlStream(new BufferedInputStream(new ByteArrayInputStream(xmlBytes)));

        List<EDocLiteStyle> newStyles = KEWServiceLocator.getStyleService().getStyles();
        assertEquals("Should have same number of old and new Styles.", oldStyles.size(), newStyles.size());
        for (Iterator iterator = oldStyles.iterator(); iterator.hasNext();) {
            EDocLiteStyle oldStyleEntry = (EDocLiteStyle) iterator.next();
            boolean foundAttribute = false;
            for (EDocLiteStyle newStyleEntry: newStyles) {
                if (oldStyleEntry.getName().equals(newStyleEntry.getName())) {
                    // NOTE: xmlns="http://www.w3.org/1999/xhtml" must be set on elements that contain HTML; exporter will automatically append an empty
                    // attribute, which will result in trivially unmatching content
                    assertEquals(canonicalize(oldStyleEntry.getXmlContent()),
                                 canonicalize(newStyleEntry.getXmlContent()));
                    foundAttribute = true;
                }
            }
            assertTrue("Could not locate the new style for name " + oldStyleEntry.getName(), foundAttribute);
        }
    }

    private String canonicalize(String xml) throws Exception {
    	Document document = XmlHelper.buildJDocument(new StringReader(xml));
    	return XmlJotter.jotDocument(document);
    }
}
