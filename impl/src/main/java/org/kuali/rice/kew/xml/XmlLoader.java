package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.kuali.rice.kew.user.WorkflowUser;



public interface XmlLoader {
    public void loadXml(InputStream inputStream, WorkflowUser user);
}
