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

import org.xml.sax.SAXParseException;

/**
 * ExceptionUtils
 * 
 * 
 */
public class DataDictionaryUtils {
    public static boolean saxCaused(Exception e) {
        boolean saxCaused = (e.getCause() instanceof SAXParseException) && (saxCause(e) == null);
        return saxCaused;
    }

    public static Exception saxCause(Exception e) {
        Exception saxCause = null;

        Throwable eCause = e.getCause();
        if (eCause instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException) eCause;
            saxCause = spe.getException();
        }

        return saxCause;
    }

}