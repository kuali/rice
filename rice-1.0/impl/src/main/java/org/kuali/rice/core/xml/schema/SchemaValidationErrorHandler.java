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
package org.kuali.rice.core.xml.schema;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is a the default error handler used by the RiceXmlSchemaFactory.
 * 
 * 
 * The class contains counters for compileErrors and compileWarnings.
 * *
 * Note: Since the current behavior of this ErrorHanlder is to throw 
 * SAXParseExceptions on error() or fatalError(), the compileError count 
 * is not very useful).  However, custom errorHandler classes that extend 
 * this class may find them useful. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SchemaValidationErrorHandler implements ErrorHandler {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SchemaValidationErrorHandler.class);

	protected int compileErrors = 0;
	protected int compileWarnings = 0;
	
	public SchemaValidationErrorHandler(){
	}
	
	/**
	 * 
	 * This method is called when an error is encountered during schema parsing.
	 * It's current behavior is to log and re-throw any exceptions.  
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
    public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.error(sAXParseException.toString(), sAXParseException);
        compileErrors++;
        throw sAXParseException;
    }

    /**
	 * This method is called when fatal error is encountered during schema parsing.
	 * It's current behavior is to log and re-throw any exceptions.  
     * 
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.fatal(sAXParseException.toString(), sAXParseException);
        compileErrors++;
        throw sAXParseException;
    }

    /**
	 * This method is called when warning is encountered during schema parsing.
	 * It's current behavior is to log the error, increment the warning count,
	 * and continue parsing.  
     * 
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        LOG.warn(sAXParseException.toString(), sAXParseException);
        compileWarnings++;
    }


	public int getCompileErrors() {
		return this.compileErrors;
	}


	public void setCompileErrors(int compileErrors) {
		this.compileErrors = compileErrors;
	}


	public int getCompileWarnings() {
		return this.compileWarnings;
	}


	public void setCompileWarnings(int compileWarnings) {
		this.compileWarnings = compileWarnings;
	}
    

}
