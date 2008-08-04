package edu.iu.uis.eden;

import java.io.InputStream;

import edu.iu.uis.eden.user.WorkflowUser;


public interface XmlLoader {
    public void loadXml(InputStream inputStream, WorkflowUser user);
}
