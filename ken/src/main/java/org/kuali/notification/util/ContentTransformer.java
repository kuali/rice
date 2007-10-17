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
package org.kuali.notification.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This class handles XSLT transformations.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ContentTransformer {

    private Transformer transformer;

    /**
     * Constructs a ContentTransformer.java.
     * @param aStyleSheet
     * @throws Exception
     */
    public ContentTransformer(StreamSource aStyleSheet) throws Exception {
        // create transformer        
        TransformerFactory factory = TransformerFactory.newInstance();
        transformer = factory.newTransformer( aStyleSheet );
    }

    /**
     * Constructs a ContentTransformer.java.
     * @param aStyleSheet
     * @param parametermap
     * @throws Exception
     */
    public ContentTransformer(StreamSource aStyleSheet, HashMap parametermap) throws Exception {
       // create transformer
       TransformerFactory factory = TransformerFactory.newInstance();
       transformer = factory.newTransformer( aStyleSheet );
       Iterator iter = parametermap.keySet().iterator();
       while (iter.hasNext()) {
          Object o = iter.next();
          String param = o.toString();
          String value = (String) parametermap.get(param);
          transformer.setParameter(param, value);
       }
    }

    /**
     * This method performs the actual transformation.
     * @param xml
     * @return
     * @throws Exception
     */
    public String transform(String xml) throws Exception {

        // perform transformation
        Source xmlsource = new StreamSource(new StringReader(xml));
        StringWriter sout = new StringWriter();
         
        transformer.transform(xmlsource, new StreamResult(sout));

        // return resulting document
        return sout.toString();
    }
}
