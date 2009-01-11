package org.kuali.rice.kew.xml;

import java.io.InputStream;



public interface XmlLoader {
    public void loadXml(InputStream inputStream, String principalId);
}
