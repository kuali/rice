/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.impl.config.property;

import java.io.IOException;
import java.util.Map;

/**
 * ConfigParser interface, establishes the contract between the parser and consumers
 * of config properties. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated only used by ConfigParserImplConfig which is deprecated
 */
@Deprecated
public interface ConfigParser {
    /**
     * Parses config file resources
     * @param locations an array of file or classpath config resources
     * @return a Properties object containing config parameters
     */
    public void parse(Map props, String[] locations) throws IOException;
}
