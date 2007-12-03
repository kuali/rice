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

import java.io.StringReader;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom.Element;

import edu.iu.uis.eden.edl.EDocLiteStyle;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports Style definitions to XML.
 *
 * @see edu.iu.uis.eden.edl.StyleService
 * @see edu.iu.uis.eden.xml.StyleXmlParser
 * @see EDocLiteStyle
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StyleXmlExporter implements XmlExporter, XmlConstants {
	private static final Logger LOG = Logger.getLogger(StyleXmlExporter.class);

	private ExportRenderer renderer = new ExportRenderer(STYLE_NAMESPACE);
	
	public Element export(ExportDataSet dataSet) {
		if (!dataSet.getStyles().isEmpty()) {
			Element rootElement = renderer.renderElement(null, STYLE_STYLES);
			rootElement.setAttribute(SCHEMA_LOCATION_ATTR, STYLE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
			for (Iterator iter = dataSet.getStyles().iterator(); iter.hasNext();) {
				EDocLiteStyle edocLite = (EDocLiteStyle) iter.next();
				exportStyle(rootElement, edocLite);
			}
			return rootElement;
		}
		return null;
	}

	private void exportStyle(Element parentEl, EDocLiteStyle style) {
        if (style == null) {
            LOG.error("Attempted to export style which was not found");
            return;
        }

        Element styleWrapperEl = renderer.renderElement(parentEl, STYLE_STYLE);
        renderer.renderAttribute(styleWrapperEl, "name", style.getName());

        try {
            Element styleEl = XmlHelper.buildJDocument(new StringReader(style.getXmlContent())).getRootElement();
            styleWrapperEl.addContent(styleEl.detach());
		} catch (InvalidXmlException e) {
			throw new RuntimeException("Error building JDom document for style", e);
		}
	}	
}