/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is a description of what this class does - dseibert don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TestSchemaValidationErrorHandler implements ErrorHandler {

	public TestSchemaValidationErrorHandler(){
	}
	

    public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("ERROR: " + sAXParseException.toString());
        JaxpSchemaValidationTest.incrementCompileErrors();
    }

    public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("FATAL ERROR: " + sAXParseException.toString());
        JaxpSchemaValidationTest.incrementCompileErrors();
    }

    public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("WARNING: " + sAXParseException.toString());
        JaxpSchemaValidationTest.incrementCompileErrors();
    }
    

}
