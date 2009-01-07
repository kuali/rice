package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.jdom.Element;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.xml.export.GroupXmlExporter;
import org.kuali.rice.kew.xml.export.XmlExporter;

public class GroupXmlServiceImpl implements XmlLoader, XmlExporter{

	private static final String XML_PARSE_ERROR = "general.error.parsexml";
	
    public void loadXml(InputStream inputStream, String principalId) {
        GroupXmlParser parser = new GroupXmlParser();
        try {
            parser.parseGroups(inputStream);
        } catch (Exception e) { //any other exception
            WorkflowServiceErrorException xe = new WorkflowServiceErrorException("Error loading xml file", new WorkflowServiceErrorImpl("Error loading xml file", XML_PARSE_ERROR));
            e.initCause(xe);
            throw xe;
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
