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
package org.kuali.rice.core.api.impex.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.common.base.Preconditions;

/**
 * An XmlDoc implementation backed by a string representing any location Spring's URL resource
 * loading API can understand. Can also represent a path on the local file system.
 * 
 * Any of these work (assuming the corresponding file exists and is readable)
 * 
 * <pre>
 *   classpath:workflow.xml
 *   http://myurl.com/workflow.xml
 *   C:\temp\workflow.xml
 *   /home/[username]/workflow.xml
 * </pre>
 * 
 * @see org.kuali.rice.core.api.impex.xml.batch.XmlDoc
 * @see org.kuali.rice.core.api.impex.xml.impl.impex.BaseXmlDoc
 * 
 * @throws IllegalArgumentException If any constructor argument is null or if the location does not
 *         exist
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LocationXmlDoc extends BaseXmlDoc {

    private final String location;
    private final Resource resource;
    private final String name;

    public LocationXmlDoc(String location, XmlDocCollection collection) {
        this(location, getName(location), collection);
    }

    public LocationXmlDoc(String location, String name, XmlDocCollection collection) {
        super(collection);
        Preconditions.checkArgument(!StringUtils.isBlank(location), "'location' cannot be blank");
        Preconditions.checkArgument(!StringUtils.isBlank(name), "'name' cannot be blank");
        Preconditions.checkNotNull(collection, "'collection' cannot be null");
        this.location = location;
        this.name = name;
        this.resource = getResource(location);
        Preconditions.checkArgument(resource.exists(), "[" + location + "] does not exist");
    }

    static Resource getResource(String location) {
        if (isExistingFile(location)) {
            return new FileSystemResource(location);
        } else {
            ResourceLoader loader = new DefaultResourceLoader();
            return loader.getResource(location);
        }
    }

    static boolean isExistingFile(String location) {
        return new File(location).exists();
    }

    static String getName(String location) {
        Resource resource = getResource(location);
        return resource.getFilename();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream getStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            LocationXmlDoc doc = (LocationXmlDoc) o;
            return location.equals(doc.getLocation());
        }
    }

    public String getLocation() {
        return this.location;
    }
}
