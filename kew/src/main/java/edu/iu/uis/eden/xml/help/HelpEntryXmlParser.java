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
package edu.iu.uis.eden.xml.help;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.help.HelpEntry;
import edu.iu.uis.eden.help.HelpService;

/*
 * A parser for help entry data.  The underlying xml file format is:
 * 
 * <pre>
 *	    <helpEntries>  
 *	      <helpEntry>
 *     	    <helpName>name 1</helpName>
 * 			<helpText>text 1</helpText>
 * 		    <helpKey>key 1</helpKey>
 *    	  </helpEntry>
 * 
 *   	  <helpEntry>
 *    		<helpName>name 2</helpName>
 * 			<helpText>text 2</helpText>
 *			<helpKey>key 2</helpKey>
 * 	  	  </helpEntry>
 *	   </helpEntries>
 * </pre>
 * 
 * <p>The xml file can contain html tags as long as these tags are wrapped between "&lt;![CDATA[" and "]]&rt;"
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HelpEntryXmlParser {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HelpEntryXmlParser.class);

    // Namespace for JDom
    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/Help");
    private static final Namespace NAMESPACE1 = Namespace.getNamespace("xsi", "ns:workflow");
    private static final String DATA_ELEMENT="data";
    private static final String HELP_ENTRIES_ELEMENT = "helpEntries";
    private static final String HELP_ENTRY_ELEMENT = "helpEntry";
    private static final String HELP_NAME_ELEMENT = "helpName";
    private static final String HELP_KEY_ELEMENT = "helpKey";
    private static final String HELP_TEXT_ELEMENT = "helpText";
    
    public List parseHelpEntries(InputStream file) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
        List helpEntries = new ArrayList();
        LOG.debug("Enter parseHelpEntries(..)");
        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        Document document = new DOMBuilder().build(w3cDocument);
        Element root = document.getRootElement();
        LOG.debug("Begin parsing(..)");
        LOG.debug(root.getName());
        
       for (Iterator helpEntriesIt = root.getChildren(HELP_ENTRIES_ELEMENT, NAMESPACE).iterator(); helpEntriesIt.hasNext();) {
            Element helpEntriesE = (Element) helpEntriesIt.next();
            for (Iterator iterator = helpEntriesE.getChildren(HELP_ENTRY_ELEMENT, NAMESPACE).iterator(); iterator.hasNext();) {
                Element helpEntryElement = (Element) iterator.next();
                HelpEntry helpEntry = new HelpEntry();
                LOG.debug(helpEntryElement.getChildText(HELP_NAME_ELEMENT, NAMESPACE));
                helpEntry.setHelpName(helpEntryElement.getChildTextTrim(HELP_NAME_ELEMENT, NAMESPACE));
                LOG.debug(helpEntryElement.getChildText(HELP_TEXT_ELEMENT, NAMESPACE));
                String text=helpEntryElement.getChildTextTrim(HELP_TEXT_ELEMENT,NAMESPACE);
                int start=text.indexOf("<![CDATA[");
                int end=text.indexOf("]]>");
                if (start!=-1 && end!=-1){
                	start+=9;
                	text=text.substring(start,end);
                }
                helpEntry.setHelpText(text);                
                LOG.debug(helpEntryElement.getChildText(HELP_KEY_ELEMENT, NAMESPACE));
                helpEntry.setHelpKey(helpEntryElement.getChildTextTrim(HELP_KEY_ELEMENT, NAMESPACE));
                        
                try {
                    LOG.debug("Saving help entry " + helpEntry.getHelpName());
                    getHelpService().saveXmlEntry(helpEntry);    
                } catch (Exception e) {
                    LOG.error("error saving help entry", e); 
                    LOG.debug(helpEntry.getHelpKey()); 
                }
                
                helpEntries.add(helpEntry);
            }
       }
        LOG.debug("Exit parseHelpEntries(..)"); 
        return helpEntries;
    }
    
    public FileOutputStream getEntriesToXml(List list) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError{
    	FileOutputStream out=null;
    	Element root=new Element(DATA_ELEMENT,NAMESPACE1);
    	Element entries=new Element(HELP_ENTRIES_ELEMENT,NAMESPACE);
    	Iterator iEntry=list.iterator();
    	while(iEntry.hasNext()){
    		HelpEntry entry=(HelpEntry)iEntry.next();
    		Element helpentry=new Element(HELP_ENTRY_ELEMENT,NAMESPACE);
    	    Element helpname=new Element(HELP_NAME_ELEMENT,NAMESPACE);
    	    helpname.setText(entry.getHelpName());
    	    Element helptext=new Element(HELP_TEXT_ELEMENT,NAMESPACE);
    	    helptext.setText("<![CDATA["+entry.getHelpText()+"]]>");
    	    Element helpkey=new Element(HELP_KEY_ELEMENT,NAMESPACE);
    	    helpkey.setText(entry.getHelpKey());
    	    helpentry.addContent(helpname);
    	    helpentry.addContent(helptext);
    	    helpentry.addContent(helpkey);
    	    entries.addContent(helpentry);
    	}
    	root.addContent(entries);
    	Document doc = new Document(root);
    	out = new FileOutputStream("text.xml");
        XMLOutputter serializer = new XMLOutputter();
        serializer.output(doc, out);
        out.flush();
        return out;
    }
    
    private HelpService getHelpService(){
        return  (HelpService) KEWServiceLocator.getService(KEWServiceLocator.HELP_SERVICE);
    }   
    
}
