package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.jdom.Element;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.xml.export.GroupXmlExporter;
import org.kuali.rice.kew.xml.export.XmlExporter;

public class GroupXmlServiceImpl implements XmlLoader, XmlExporter{

	public void loadXml(InputStream inputStream, String principalId) {
        GroupXmlParser parser = new GroupXmlParser();
        try {
            parser.parseGroups(inputStream);
        } catch (Exception e) {
            throw new RiceRuntimeException("Error loading xml file", e);
        }
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.xml.export.XmlExporter#export(org.kuali.rice.kew.export.ExportDataSet)
     */
    public Element export(ExportDataSet dataSet) {
        GroupXmlExporter exporter = new GroupXmlExporter();
        return exporter.export(dataSet);
    }


}
