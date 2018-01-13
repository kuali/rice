/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.engine.node.var.schemes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.PropertiesUtil;
import org.kuali.rice.kew.engine.node.var.Property;
import org.kuali.rice.kew.engine.node.var.PropertyScheme;


/**
 * A property scheme that loads resources from the class loader.
 *  
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ResourceScheme implements PropertyScheme {
    private static final Logger LOG = Logger.getLogger(ResourceScheme.class);

    public String getName() {
        return "resource";
    }
    public String getShortName() {
        return "res";
    }

    public Object load(Property property, RouteContext context) {
        String resource;
        boolean relative = false;
//        if (property.locator.startsWith("/")) {
            resource = property.locator;
//        } else {
//            relative = true;
//            String prefix;
//            /* if a resource prefix is set, use it */
//            if (state.getResourcePrefix() != null) {
//                prefix = state.getResourcePrefix();
//            } else {
//                /* otherwise use the location of the Script class */
//                prefix = Script.class.getPackage().getName().replace('.', '/');
//            }
//            if (!prefix.endsWith("/")) {
//                prefix += "/"; 
//            }
//            resource = prefix + property.locator;
//        }
        String resStr = property.locator + (relative ? "(" + resource + ")" : "");
        LOG.info("Reading resource " + resStr + "...");
        
        InputStream is = getClass().getResourceAsStream(resource);
        if (is == null) {
            throw new RuntimeException("Resource not found: " + resStr);
        }
        try {
            return PropertiesUtil.readResource(is);
        } catch (IOException ioe) {
            throw new RuntimeException("Error loading resource: " + resStr, ioe);
        }
    }

    public String toString() {
        return "[ResourceScheme]";
    }
}
