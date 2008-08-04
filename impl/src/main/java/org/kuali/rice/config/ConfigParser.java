/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.config;

import java.io.IOException;
import java.util.Map;

/**
 * ConfigParser interface, establishes the contract between the parser and consumers
 * of config properties. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ConfigParser {
    /**
     * Parses config file resources
     * @param locations an array of file or classpath config resources
     * @return a Properties object containing config parameters
     */
    public void parse(Map props, String[] locations) throws IOException;
}