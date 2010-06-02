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
package org.kuali.rice.core.xml.schema;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is a description of what this class does - dseibert don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SchemaValidationErrorHandler implements ErrorHandler {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SchemaValidationErrorHandler.class);

	public SchemaValidationErrorHandler(){
	}
	

    public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.error(sAXParseException.toString(), sAXParseException);
    }

    public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.fatal(sAXParseException.toString(), sAXParseException);
    }

    public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.warn(sAXParseException.toString(), sAXParseException);
    }
    

}
