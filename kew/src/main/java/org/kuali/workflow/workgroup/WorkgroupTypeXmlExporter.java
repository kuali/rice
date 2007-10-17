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
package org.kuali.workflow.workgroup;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.xml.XmlConstants;
import edu.iu.uis.eden.xml.export.ExportRenderer;
import edu.iu.uis.eden.xml.export.XmlExporter;

/**
 * Exports {@link WorkgroupType}s to XML.
 *
 * @see WorkgroupType
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private ExportRenderer renderer = new ExportRenderer(WORKGROUP_TYPE_NAMESPACE);

    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getWorkgroupTypes().isEmpty()) {
            Element rootElement = renderer.renderElement(null, WORKGROUP_TYPES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, WORKGROUP_TYPE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (WorkgroupType workgroupType : dataSet.getWorkgroupTypes()) {
                exportWorkgroupType(rootElement, workgroupType);
            }
            return rootElement;
        }
        return null;
    }

    private void exportWorkgroupType(Element parent, WorkgroupType workgroupType) {
        Element workgroupTypeElement = renderer.renderElement(parent, WORKGROUP_TYPE);
        renderer.renderTextElement(workgroupTypeElement, NAME, workgroupType.getName());
        renderer.renderTextElement(workgroupTypeElement, LABEL, workgroupType.getLabel());
        if (!StringUtils.isBlank(workgroupType.getDescription())) {
        	renderer.renderTextElement(workgroupTypeElement, DESCRIPTION, workgroupType.getDescription());
        }
        if (!StringUtils.isBlank(workgroupType.getDocumentTypeName())) {
        	renderer.renderTextElement(workgroupTypeElement, DOCUMENT_TYPE, workgroupType.getDocumentTypeName());
        }

        exportAttributes(workgroupTypeElement, workgroupType.getActiveAttributes());
    }

    private void exportAttributes(Element parent, List<WorkgroupTypeAttribute> workgroupTypeAttributes) {
        if (!workgroupTypeAttributes.isEmpty()) {
            Element attributesElement = renderer.renderElement(parent, ATTRIBUTES);
            for (WorkgroupTypeAttribute attribute : workgroupTypeAttributes) {
                Element attributeElement = renderer.renderElement(attributesElement, ATTRIBUTE);
                renderer.renderTextElement(attributeElement, NAME, attribute.getAttribute().getName());
            }
        }
    }

}
