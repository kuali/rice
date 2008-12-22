package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kim.bo.Person;



public interface XmlLoader {
    public void loadXml(InputStream inputStream, String principalId);
}
