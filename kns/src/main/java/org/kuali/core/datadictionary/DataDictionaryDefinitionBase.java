/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.datadictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Common base class for DataDictionaryDefinition types.
 * 
 * 
 */
abstract public class DataDictionaryDefinitionBase implements DataDictionaryDefinition {
    private static Log LOG = LogFactory.getLog(DataDictionaryDefinitionBase.class);

    private final String parseLocation;

    // this boolean is for the dd generator, since it does not parse dd files
    public static boolean isParsingFile = true;

    public DataDictionaryDefinitionBase() {
        if (isParsingFile) {
            String parseFileName = DataDictionaryBuilder.getCurrentFileName();
            int parseLineNumber = DataDictionaryBuilder.getCurrentLineNumber();

            parseLocation = parseFileName + ":" + Integer.toString(parseLineNumber);
        }
        else {
            parseLocation = "";
        }
    }

    /**
     * @return XML filename and line number from the parser which created this object
     */
    protected String getParseLocation() {
        return parseLocation;
    }
}