/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.applicationconstants.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;

/*
 * A parser for application constant data.  Schema is ApplicationConstants.xsd
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsXmlParser {

    private static final Logger LOG = Logger.getLogger(ApplicationConstantsXmlParser.class);

    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/ApplicationConstants");
    private static final String APP_CONSTANTS_ELEMENT = "ApplicationConstants";
    private static final String APP_CONSTANT_ELEMENT = "ApplicationConstant";
    private static final String CONSTANT_NAME_ELEMENT = "ConstantName";
    private static final String CONSTANT_VALUE_ELEMENT = "ConstantValue";

    public List parseAppConstEntries(InputStream file) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
        List constEntries = new ArrayList();

        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        Document document = new DOMBuilder().build(w3cDocument);
        Element root = document.getRootElement();

        for (Iterator constantsIt = root.getChildren(APP_CONSTANTS_ELEMENT, NAMESPACE).iterator(); constantsIt.hasNext();) {
            Element constantsElement = (Element) constantsIt.next();
            for (Iterator iterator = constantsElement.getChildren(APP_CONSTANT_ELEMENT, NAMESPACE).iterator(); iterator.hasNext();) {
                Element constElement = (Element) iterator.next();

                String name = constElement.getChildTextTrim(CONSTANT_NAME_ELEMENT, NAMESPACE);
                String value = constElement.getChildTextTrim(CONSTANT_VALUE_ELEMENT, NAMESPACE);

                LOG.info("Processing constant: " + name);

                ApplicationConstant constant = null;; 
                try {
                    LOG.debug("Looking up Application Constant: " + name);
                    constant = KEWServiceLocator.getApplicationConstantsService().findByName(name);
                    if (constant!= null) {
                        LOG.debug("Found existing Application Constant: " + name);
                        constant.setApplicationConstantValue(value);
                    } else {
                        constant = new ApplicationConstant();
                        constant.setApplicationConstantName(name);
                        constant.setApplicationConstantValue(value);
                    }
                    LOG.debug("Saving Application Constant: " + constant.getApplicationConstantName());
                    // TODO we're not flusing the cache here to get around problem with testing, this
                    // is typically only used in a test situation but we need to fix this!
                    KEWServiceLocator.getApplicationConstantsService().save(constant);
                } catch (Exception e) {
                    LOG.error("error saving Application Constant", e);
                }

                // NOTE: should we move this into the try/catch block, or do we want to preserve a constant
                // in memory even when it has not been persisted successfully?
                constEntries.add(constant);
            }
        }
        
//        ((ApplicationConstantsCache) SpringServiceLocator.getCache().getCachedObjectById(ApplicationConstantsCache.APPLICATION_CONSTANTS_CACHE_ID)).reload();
        return constEntries;
    }
}
