/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.exporter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType;
import org.kuali.rice.krad.exception.ExportNotSupportedException;
import org.kuali.rice.krad.util.KRADConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates exporting a {@code TravelAccountType} to a custom XML format.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountTypeExporter implements Exporter {

    @Override
    public void export(Class<?> dataObjectClass, List<? extends Object> dataObjects, String exportFormat,
            OutputStream outputStream) throws IOException, ExportNotSupportedException {
        Document document = new Document(new Element("travelAccountTypes"));

        for (Object dataObject : dataObjects) {
            Element travelAccountTypeElement = new Element("travelAccountType");
            TravelAccountType travelAccountType = (TravelAccountType) dataObject;

            Element accountTypeCodeElement = new Element("accountTypeCode");
            accountTypeCodeElement.setText(travelAccountType.getAccountTypeCode());
            travelAccountTypeElement.addContent(accountTypeCodeElement);

            Element nameElement = new Element("name");
            nameElement.setText(travelAccountType.getName());
            travelAccountTypeElement.addContent(nameElement);

            Element activeElement = new Element("active");
            activeElement.setText(Boolean.toString(travelAccountType.isActive()));
            travelAccountTypeElement.addContent(activeElement);

            document.getRootElement().addContent(travelAccountTypeElement);
        }

        XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputer.output(document, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not write XML data export.", e);
        }
    }

    @Override
    public List<String> getSupportedFormats(Class<?> dataObjectClass) {
        return Collections.singletonList(KRADConstants.XML_FORMAT);
    }
}
