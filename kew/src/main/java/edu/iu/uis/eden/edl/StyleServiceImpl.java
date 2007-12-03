/*
 * Copyright 2005-2007 The Kuali Foundation.
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
// Created on Jan 4, 2007

package edu.iu.uis.eden.edl;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.edl.dao.EDocLiteDAO;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.StyleXmlParser;
import edu.iu.uis.eden.xml.export.StyleXmlExporter;

/**
 * Implements generic StyleService via existing EDL style table
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StyleServiceImpl implements StyleService {
    private static final Logger LOG = Logger.getLogger(StyleServiceImpl.class);

    private static final String TEMPLATES_CACHE_GROUP_NAME = "Templates";

    /**
     * The Spring-wired DAO bean
     */
    private EDocLiteDAO dao;

    // ---- Spring DAO setter

    public void setStyleDAO(EDocLiteDAO dao) {
        this.dao = dao;
    }

    // ---- StyleService interface

    public EDocLiteStyle getStyle(String styleName) {
        return dao.getEDocLiteStyle(styleName);
    }

    public Templates getStyleAsTranslet(String name) throws TransformerConfigurationException {
        if (name == null) return null;
        Templates translet = fetchTemplatesFromCache(name);
        if (translet == null) {
            EDocLiteStyle edlStyleData = getStyle(name);
            if (edlStyleData == null) {
                //throw new WorkflowRuntimeException("Style " + name + " not found.");
                return null;
            }
        
            if (new Boolean(Utilities.getApplicationConstant(EdenConstants.APP_CONST_EDL_USE_XSLTC)).booleanValue()) {
                LOG.info("using xsltc to compile stylesheet");
                String key = "javax.xml.transform.TransformerFactory";
                String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
                Properties props = System.getProperties();
                props.put(key, value);
                System.setProperties(props);    
            }
        
            TransformerFactory factory = TransformerFactory.newInstance();
            URIResolver resolver = new WidgetURIResolver();
            factory.setURIResolver(resolver);
        
            if (new Boolean(Utilities.getApplicationConstant(EdenConstants.APP_CONST_EDL_USE_XSLTC)).booleanValue()) {
                factory.setAttribute("translet-name",name);
                factory.setAttribute("generate-translet",Boolean.TRUE);
                if (new Boolean(Utilities.getApplicationConstant(EdenConstants.APP_CONST_EDL_DEBUG_TRANSFORM)).booleanValue()) {
                    factory.setAttribute("debug", Boolean.TRUE);    
                }
            
            }
            
            translet = factory.newTemplates(new StreamSource(new StringReader(edlStyleData.getXmlContent())));
            putTemplatesInCache(name, translet);
        }
        return translet;
    }

    /**
     * Returns all styles
     */
    public List<EDocLiteStyle> getStyles() {
        return dao.getEDocLiteStyles();
    }

    /**
     * Returns all style names
     */
    public List<String> getStyleNames() {
        return dao.getEDocLiteStyleNames();
    }

    /**
     * Does not currently take into account style sheet dependences robustly
     */
    public void removeStyleFromCache(String styleName) {
        LOG.info("Removing Style " + styleName + " from the style cache");
        // we don't know what styles may import other styles so we need to flush them all
        KEWServiceLocator.getCacheAdministrator().flushGroup(TEMPLATES_CACHE_GROUP_NAME);
        //KEWServiceLocator.getCacheAdministrator().flushEntry(getTemplatesCacheKey(styleName));
    }

    public void saveStyle(InputStream xml) {
        // convert xml to EDocLiteStyle
        EDocLiteStyle data = parseEDocLiteStyle(parse(xml).getDocumentElement());
        saveStyle(data);
    }

    public void saveStyle(EDocLiteStyle data) {
        EDocLiteStyle existingData = getStyle(data.getName());
        if (existingData != null) {
            existingData.setActiveInd(Boolean.FALSE);
            dao.saveEDocLiteStyle(existingData);
        }
        // if not specified (from xml), mark it as active
        if (data.getActiveInd() == null) {
            data.setActiveInd(Boolean.TRUE);
        }
        dao.saveEDocLiteStyle(data);
        removeStyleFromCache(data.getName());
    }


    // ---- XmlLoader interface implementation

    public void loadXml(InputStream inputStream, WorkflowUser user) {
        StyleXmlParser.loadXml(this, inputStream, user);
    }

    // ---- XmlExporter interface implementation
    public org.jdom.Element export(ExportDataSet dataSet) {
        return new StyleXmlExporter().export(dataSet);
    }
    
    // cache helper methods

    /**
     * Returns the key to be used for caching the Templates for the given style name.
     */
    protected String getTemplatesCacheKey(String styleName) {
        return TEMPLATES_CACHE_GROUP_NAME + ":" + styleName;
    }

    protected Templates fetchTemplatesFromCache(String styleName) {
        return (Templates) KEWServiceLocator.getCacheAdministrator().getFromCache(getTemplatesCacheKey(styleName));
    }

    protected void putTemplatesInCache(String styleName, Templates templates) {
        KEWServiceLocator.getCacheAdministrator().putInCache(getTemplatesCacheKey(styleName), templates, TEMPLATES_CACHE_GROUP_NAME);
    }

    // parsing helper methods

    /**
     * Parses an EDocLiteStyle
     * 
     * @param e
     *            element to parse
     * @return an EDocLiteStyle
     */
    private static EDocLiteStyle parseEDocLiteStyle(Element e) {
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException("style", "name");
        }
        EDocLiteStyle style = new EDocLiteStyle();
        style.setName(name);
        Element stylesheet = null;
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            /*
             * LOG.debug("NodeName: " + child.getNodeName()); LOG.debug("LocalName: " + child.getLocalName()); LOG.debug("Prefix: " + child.getPrefix()); LOG.debug("NS URI: " + child.getNamespaceURI());
             */
            if (child.getNodeType() == Node.ELEMENT_NODE && "xsl:stylesheet".equals(child.getNodeName())) {
                stylesheet = (Element) child;
                break;
            }
        }
        if (stylesheet == null) {
            throw generateMissingChildException("style", "xsl:stylesheet");
        }
        try {
            style.setXmlContent(XmlHelper.writeNode(stylesheet, true));
        } catch (TransformerException te) {
            throw generateSerializationException("style", te);
        }
        return style;
    }

    /**
     * Parses an arbitrary XML stream
     * 
     * @param stream
     *            stream containing XML doc content
     * @return parsed Document object
     */
    private static Document parse(InputStream stream) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        } catch (Exception e) {
            WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error parsing Style XML file", new WorkflowServiceErrorImpl("Error parsing XML file.", EdenConstants.XML_FILE_PARSE_ERROR));
            wsee.initCause(e);
            throw wsee;
        }
    }

    private static WorkflowServiceErrorException generateMissingAttribException(String element, String attrib) {
        return generateException("Style '" + element + "' element must contain a '" + attrib + "' attribute", null);
    }

    private static WorkflowServiceErrorException generateMissingChildException(String element, String child) {
        return generateException("Style '" + element + "' element must contain a '" + child + "' child element", null);
    }

    private static WorkflowServiceErrorException generateSerializationException(String element, TransformerException cause) {
        return generateException("Error serializing EDocLite '" + element + "' element", cause);
    }

    private static WorkflowServiceErrorException generateException(String error, Throwable cause) {
        WorkflowServiceErrorException wsee = new WorkflowServiceErrorException(error, new WorkflowServiceErrorImpl(error, EdenConstants.XML_FILE_PARSE_ERROR));
        if (cause != null) {
            wsee.initCause(cause);
        }
        return wsee;
    }
}