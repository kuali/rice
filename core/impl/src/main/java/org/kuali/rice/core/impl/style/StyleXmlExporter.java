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
package org.kuali.rice.core.impl.style;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.SCHEMA_LOCATION_ATTR;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.SCHEMA_NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.STYLE_NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.STYLE_SCHEMA_LOCATION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.STYLE_STYLE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.STYLE_STYLES;

import java.io.StringReader;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.util.XmlHelper;
import org.kuali.rice.core.util.XmlRenderer;
import org.kuali.rice.core.xml.XmlException;

/**
 * Exports Style definitions to XML.
 *
 * @see org.kuali.rice.edl.impl.service.StyleService
 * @see org.kuali.rice.kew.xml.StyleXmlParser
 * @see EDocLiteStyle
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleXmlExporter implements XmlExporter {
	private static final Logger LOG = Logger.getLogger(StyleXmlExporter.class);

	private XmlRenderer renderer = new XmlRenderer(STYLE_NAMESPACE);
	
	@Override
	public boolean supportPrettyPrint() {
		return false;
	}

	public Element export(ExportDataSet exportDataSet) {
		StyleExportDataSet dataSet = StyleExportDataSet.fromExportDataSet(exportDataSet);
		if (!dataSet.getStyles().isEmpty()) {
			Element rootElement = renderer.renderElement(null, STYLE_STYLES);
			rootElement.setAttribute(SCHEMA_LOCATION_ATTR, STYLE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
			for (Iterator<StyleBo> iter = dataSet.getStyles().iterator(); iter.hasNext();) {
				StyleBo edocLite = iter.next();
				exportStyle(rootElement, edocLite);
			}
			return rootElement;
		}
		return null;
	}

	private void exportStyle(Element parentEl, StyleBo style) {
        if (style == null) {
            LOG.error("Attempted to export style which was not found");
            return;
        }

        Element styleWrapperEl = renderer.renderElement(parentEl, STYLE_STYLE);
        renderer.renderAttribute(styleWrapperEl, "name", style.getName());

        try {
            Element styleEl = XmlHelper.buildJDocument(new StringReader(style.getXmlContent())).getRootElement();
            styleWrapperEl.addContent(styleEl.detach());
		} catch (XmlException e) {
			throw new RuntimeException("Error building JDom document for style", e);
		}
	}	
}
