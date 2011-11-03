/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.core.impl.impex.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Internal workflow EntityResolver which resolves system ids with the
 * "resource:" prefix to ClassLoader resources
 * 
 * TODO: maybe prefix should be changed from "resource:" to "internal:" or just "workflow:"
 * given that it can be resolved in arbitrary ways other than ClassLoader "resources"
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClassLoaderEntityResolver implements EntityResolver {
    private static final Logger LOG = Logger.getLogger(ClassLoaderEntityResolver.class);

    /**
     * This contains definitions for items in the core "xml" schema, i.e. base, id, lang, and space attributes. 
     */
    private static final String XML_NAMESPACE_SCHEMA = "http://www.w3.org/2001/xml.xsd";
    private static final String XSD_NAMESPACE_SCHEMA = "http://www.w3.org/2001/XMLSchema.xsd";
    
    private final String base;
    public ClassLoaderEntityResolver() {
        this.base = "schema";
    }
    public ClassLoaderEntityResolver(String base) {
        this.base = base;
    }
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        LOG.debug("Resolving '" + publicId + "' / '" + systemId + "'");
        String path = "";
        if (systemId.equals(XML_NAMESPACE_SCHEMA)) {
            path = base + "/xml.xsd";
        } else if (systemId.equals(XSD_NAMESPACE_SCHEMA)) {
            path = base + "/XMLSchema.xsd";
        } else if (systemId.startsWith("resource")) {
            /* It turns out that the stock XMLSchema.xsd refers to XMLSchema.dtd in a relative
               fashion which results in the parser qualifying it to some local file:// path
               which breaks our detection here.
               So I have made a small mod to the stock XMLSchema.xsd so that it instead refers to
               resource:XMLSchema.dtd which can be looked up locally.
               The same is true for XMLSchema.dtd with regard to datatypes.dtd, so I have also
               modified XMLSchema.dtd to refer to resource:datatypes.dtd.
               An alternative would be to rely on publicId, however that would essentially hard code
               the lookup to always be in the classpath and rule out being able to redirect the location
               of the physical resource through the systemId, which is useful.
            */

            // TODO: revisit making this more sophisticated than just the classloader
            // of this class (thread context classloader? plugin classloader?)
            path = base + "/" + systemId.substring("resource:".length());
            // ok, if the path does not itself end in .xsd or .dtd, it is bare/abstract
            // so realize it by appending .xsd
            // this allows us to support looking up files ending with ".dtd" through resource: without
            // having extra logic to attempt to look up both suffixes for every single resource:
            // (all of which except XMLSchema.dtd and datatypes.dtd at this point are .xsd files)
            if (!(systemId.endsWith(".xsd") || systemId.endsWith(".dtd"))) {
                path += ".xsd";
            }
        } else {
            LOG.error("Unable to resolve system id '" + systemId + "' locally...delegating to default resolution strategy.");
            return null;
        }
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            String message = "Unable to find schema (" + path + ") for: " + systemId;
            LOG.error(message);
            throw new SAXException(message);
        }
        return new InputSource(is);
    }
}
