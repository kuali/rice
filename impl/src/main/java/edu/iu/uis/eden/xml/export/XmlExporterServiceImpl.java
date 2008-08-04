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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.BeanInitializationException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.ExportNotSupportedException;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * An implementation of the XmlExporterService which can be configured with a set of
 * services that know how to export various pieces of the {@link ExportDataSet} to XML.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XmlExporterServiceImpl implements XmlExporterService, XmlConstants {

    private List serviceOrder;

    public List getSupportedFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }

    public byte[] export(ExportFormat format, ExportDataSet dataSet) {
        if (!getSupportedFormats().contains(format)) {
            throw new ExportNotSupportedException("Xml Exporter does not support the format " + format.getFormatName());
        }
        if (dataSet == null) {
            throw new ExportNotSupportedException("Xml Exporter cannot handle NULL data.");
        }
        Element rootElement = new Element(DATA_ELEMENT, WORKFLOW_NAMESPACE);
        rootElement.addNamespaceDeclaration(SCHEMA_NAMESPACE);
        rootElement.setAttribute(SCHEMA_LOCATION_ATTR, WORKFLOW_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
        Document document = new Document(rootElement);
        for (Iterator iterator = serviceOrder.iterator(); iterator.hasNext();) {
            XmlExporter exporter = (XmlExporter)KEWServiceLocator.getService((String) iterator.next());
            appendIfNotEmpty(rootElement, exporter.export(dataSet));
        }

        XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
        StringWriter writer = new StringWriter();
        try {
            outputer.output(document, writer);
        } catch (IOException e) {
            throw new WorkflowRuntimeException("Could not write XML data export.", e);
        }
        return writer.toString().getBytes();
    }

    private void appendIfNotEmpty(Element parent, Element child) {
        if (child != null) {
            parent.addContent(child);
        }
    }

    public void setServiceOrder(List serviceOrder) throws BeanInitializationException {
        this.serviceOrder = serviceOrder;
    }

}
