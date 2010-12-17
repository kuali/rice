/*
 * Copyright 2010 The Kuali Foundation
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

import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;

/**
 * Basic implementation of ChainedXMLFilter.  This class will automatically
 * call the next() method for any non-implemented call.  Subclassers must be
 * careful to do the same for overridden methods or filter processing will
 * short-circuit.
 */
public class ChainedXMLFilterBase extends XMLFilterImpl implements ChainedXMLFilter {
    public void setParent(XMLReader parent) {
        super.setParent(parent);
        if ( parent != null ) {
            parent.setEntityResolver(this);
            parent.setDTDHandler(this);
            parent.setContentHandler(this);
            parent.setErrorHandler(this);
        }
    }
}
