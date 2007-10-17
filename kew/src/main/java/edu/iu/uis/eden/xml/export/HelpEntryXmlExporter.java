/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.xml.export;

import java.util.Iterator;

import org.jdom.Element;

import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.help.HelpEntry;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports {@link HelpEntry}s to XML.
 *
 * @see HelpEntry
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HelpEntryXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private ExportRenderer renderer = new ExportRenderer(HELP_NAMESPACE);
    
    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getHelp().isEmpty()) {
            Element rootElement = renderer.renderElement(null, HELP_ENTRIES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, HELP_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getHelp().iterator(); iterator.hasNext();) {
                HelpEntry helpEntry = (HelpEntry)iterator.next();
                exportHelpEntry(rootElement, helpEntry);
            }
            return rootElement;
        }
        return null;
    }
    
    private void exportHelpEntry(Element parent, HelpEntry helpEntry) {
        Element helpElement = renderer.renderElement(parent, HELP_ENTRY);
        renderer.renderTextElement(helpElement, HELP_NAME, helpEntry.getHelpName());
        renderer.renderTextElement(helpElement, HELP_KEY, helpEntry.getHelpKey());
        renderer.renderCDATAElement(helpElement, HELP_TEXT, helpEntry.getHelpText());
    }
    
}
