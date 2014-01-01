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
