/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Created on May 8, 2006

package edu.iu.uis.eden.test.web.framework.schemes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.ScriptState;
import edu.iu.uis.eden.test.web.framework.Util;

/**
 * A property scheme that interprets the locator as a URL
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class URLScheme implements PropertyScheme {
    private static final Logger LOG = Logger.getLogger(URLScheme.class);

    public String getName() {
        return "url";
    }
    public String getShortName() {
        return "url";
    }

    public Object load(Property property, ScriptState state) {
        LOG.info("Reading url '" + property.locator + "'...");
        InputStream is;
        try {
            is = new URL(property.locator).openStream();
            if (is == null) {
                throw new RuntimeException("Unable to access URL: " + property.locator);
            }
            return Util.readResource(is);
        } catch (IOException ioe) {
            throw new RuntimeException("Error loading resource: " + property.locator, ioe);
        }
    }

    public String toString() {
        return "[URLScheme]";
    }
}