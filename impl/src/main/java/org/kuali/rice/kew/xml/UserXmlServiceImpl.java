package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.kuali.rice.core.exception.RiceRuntimeException;

public class UserXmlServiceImpl implements XmlLoader {
	
    public void loadXml(InputStream inputStream, String principalId) {
        UserXmlParser parser = new UserXmlParser();
        try {
            parser.parseUsers(inputStream);
        } catch (Exception e) {
            throw new RiceRuntimeException("Error loading xml file", e);
        }
    }

}
