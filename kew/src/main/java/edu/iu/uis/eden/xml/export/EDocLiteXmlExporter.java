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
import org.jdom.Namespace;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.edl.EDocLiteAssociation;
import edu.iu.uis.eden.edl.EDocLiteDefinition;
import edu.iu.uis.eden.edl.EDocLiteService;
import edu.iu.uis.eden.edl.EDocLiteStyle;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports EDocLite definitions to XML.
 *
 * @see EDocLiteDefinition
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDocLiteXmlExporter implements XmlExporter, XmlConstants {

	private static final Logger LOG = Logger.getLogger(EDocLiteXmlExporter.class);

	private ExportRenderer renderer = new ExportRenderer(EDL_NAMESPACE);

	public Element export(ExportDataSet dataSet) {
		if (!dataSet.getEdocLites().isEmpty()) {
			Element rootElement = renderer.renderElement(null, EDL_EDOCLITE);
			rootElement.setAttribute(SCHEMA_LOCATION_ATTR, EDL_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
			for (Iterator iter = dataSet.getEdocLites().iterator(); iter.hasNext();) {
				EDocLiteAssociation edocLite = (EDocLiteAssociation) iter.next();
				exportEDocLite(rootElement, edocLite);
			}
			return rootElement;
		}
		return null;
	}

	private void exportEDocLite(Element parentEl, EDocLiteAssociation edl) {

		try {
			EDocLiteService edlService = KEWServiceLocator.getEDocLiteService();
			if (edl.getDefinition() != null) {  //this probably shouldn't be supported on the entry side...
				EDocLiteDefinition def = edlService.getEDocLiteDefinition(edl.getDefinition());
				if (def == null) {
					LOG.error("Attempted to export definition " + edl.getDefinition() + " which was not found");
					return;
				}
				Element defEl = XmlHelper.buildJDocument(new StringReader(def.getXmlContent())).getRootElement();
				setNamespace(defEl, EDL_NAMESPACE);
				parentEl.addContent(defEl.detach());
			}

			if (edl.getStyle() != null) {//this probably shouldn't be supported on the entry side...
				Element styleWrapperEl = renderer.renderElement(parentEl, EDL_STYLE);
				renderer.renderAttribute(styleWrapperEl, "name", edl.getStyle());
				EDocLiteStyle style = edlService.getEDocLiteStyle(edl.getStyle());
				if (style == null) {
					LOG.error("Attempted to export style " + edl.getStyle() + " which was not found");
					return;
				}
				Element styleEl = XmlHelper.buildJDocument(new StringReader(style.getXmlContent())).getRootElement();
				styleWrapperEl.addContent(styleEl.detach());
			}


			Element associationEl = renderer.renderElement(parentEl, EDL_ASSOCIATION);
			renderer.renderTextElement(associationEl, EDL_DOC_TYPE, edl.getEdlName());
			if (edl.getDefinition() != null) {
				renderer.renderTextElement(associationEl, EDL_DEFINITION, edl.getDefinition());
			}
			if (edl.getStyle() != null) {
				renderer.renderTextElement(associationEl, EDL_STYLE, edl.getStyle());
			}

			renderer.renderTextElement(associationEl, EDL_ACTIVE, edl.getActiveInd().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setNamespace(Element element, Namespace namespace) {
		element.setNamespace(namespace);
		for (Iterator iter = element.getChildren().iterator(); iter.hasNext();) {
			setNamespace((Element)iter.next(), namespace);
		}
	}
}