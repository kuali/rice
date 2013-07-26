package org.kuali.rice.core.api.util;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * A ResourceLoader implementation which loads from the classpath if the path name is prefixed with "classpath:" and
 * loads as a {@link FileSystemResource} if not.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClasspathOrFileResourceLoader extends DefaultResourceLoader {

    @Override
    protected Resource getResourceByPath(String path) {
        return new FileSystemResource(path);
    }

}
