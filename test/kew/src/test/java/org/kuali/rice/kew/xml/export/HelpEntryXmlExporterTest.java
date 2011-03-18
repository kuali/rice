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

import org.junit.Test;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class HelpEntryXmlExporterTest extends XmlExporterTestCase {
    
	public void testExportActionConfig() throws Exception {
		// action config has no help entries
    }
    
    public void testExportEngineConfig() throws Exception {
    	// engine config has no help entries
    }
	
    /**
     * This will test some rule attributes with routing and searching config.
     */
    @Test public void testExport() throws Exception {
        loadXmlFile("HelpEntryExportConfig.xml");
        assertExport();
    }
        
    protected void assertExport() throws Exception {
        // export all existing rule attributes
    	HelpEntry entry=new HelpEntry();
    	entry.setHelpKey("");
    	entry.setHelpName("");
    	entry.setHelpText("");
        List oldHelpEntries = KEWServiceLocator.getHelpService().search(entry);
        ExportDataSet dataSet = new ExportDataSet();
        dataSet.getHelp().addAll(oldHelpEntries);
        byte[] xmlBytes = KEWServiceLocator.getXmlExporterService().export(dataSet);
        assertTrue("XML should be non empty.", xmlBytes != null && xmlBytes.length > 0);
        
        // import the exported xml
        loadXmlStream(new BufferedInputStream(new ByteArrayInputStream(xmlBytes)));
        
        List newHelpEntries = KEWServiceLocator.getHelpService().search(entry);
        assertEquals("Should have same number of old and new RuleAttributes.", oldHelpEntries.size(), newHelpEntries.size());
        for (Iterator iterator = oldHelpEntries.iterator(); iterator.hasNext();) {
            HelpEntry oldHelpEntry = (HelpEntry) iterator.next();
            boolean foundAttribute = false;
            for (Iterator iterator2 = newHelpEntries.iterator(); iterator2.hasNext();) {
                HelpEntry newHelpEntry = (HelpEntry) iterator2.next();
                if (oldHelpEntry.getHelpName().equals(newHelpEntry.getHelpName())) {
                    assertHelpEntryExport(oldHelpEntry, newHelpEntry);
                    foundAttribute = true;
                }
            }
            assertTrue("Could not locate the new HelpEntry for name " + oldHelpEntry.getHelpName(), foundAttribute);
        }
    }
    
    private void assertHelpEntryExport(HelpEntry oldHelpEntry, HelpEntry newHelpEntry) {
        // ids should be the same because we don't version rule attributes, but thier version number should be different
        assertEquals("Ids should be the same.", oldHelpEntry.getHelpId(), newHelpEntry.getHelpId());
        assertFalse("Version numbers should be different.", oldHelpEntry.getLockVerNbr().equals(newHelpEntry.getLockVerNbr()));
        assertEquals(oldHelpEntry.getHelpName(), newHelpEntry.getHelpName());
        assertEquals(oldHelpEntry.getHelpKey(), newHelpEntry.getHelpKey());
        assertEquals(oldHelpEntry.getHelpText(), newHelpEntry.getHelpText());
        //assertEquals(StringUtils.deleteWhitespace(oldRuleAttribute.getXmlConfigData()), StringUtils.deleteWhitespace(newRuleAttribute.getXmlConfigData()));
        //assertRuleTemplateAttributes(oldRuleAttribute.getRuleTemplateAttributes(), newRuleAttribute.getRuleTemplateAttributes());
    }
    

        
}
