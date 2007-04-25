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
import org.kuali.core.datadictionary.exception.ParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Defines exception-handling for the XML parses used by the DataDictionaryBuilder's Digester
 * 
 * 
 */
public class XmlErrorHandler implements ErrorHandler {
    // logger
    private static Log LOG = LogFactory.getLog(XmlErrorHandler.class);

    private final String fileName;

    public XmlErrorHandler(String fileName) {
        this.fileName = fileName;
    }


    public void warning(SAXParseException e) {
        String parseMessage = assembleMessage("warning", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    public void error(SAXParseException e) {
        String parseMessage = assembleMessage("error", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    public void fatalError(SAXParseException e) {
        String parseMessage = assembleMessage("fatal error", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    private String assembleMessage(String messageType, SAXParseException e) {
        StringBuffer message = new StringBuffer(messageType);
        message.append(" parsing dataDictionary input file '");
        message.append(fileName);
        message.append("' , line ");
        message.append(e.getLineNumber());
        message.append(", column ");
        message.append(e.getColumnNumber());
        message.append(":");
        message.append(e.getMessage());

        return message.toString();
    }
}