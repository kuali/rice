/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.edl.impl.service.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.core.xml.XmlException;
import org.kuali.rice.edl.impl.WidgetURIResolver;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.edl.impl.dao.EDocLiteDAO;
import org.kuali.rice.edl.impl.service.StyleService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.xml.StyleXmlParser;
import org.kuali.rice.kew.xml.export.StyleXmlExporter;
import org.kuali.rice.kns.util.KNSConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;


/**
 * Implements generic StyleService via existing EDL style table
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleServiceImpl implements StyleService {
    private static final Logger LOG = Logger.getLogger(StyleServiceImpl.class);

    private static final String TEMPLATES_CACHE_GROUP_NAME = "Templates";
    private static final String STYLE_CONFIG_PREFIX = "edl.style";

    /**
     * The Spring-wired DAO bean
     */
    private EDocLiteDAO dao;

    // ---- Spring DAO setter

    public void setStyleDAO(EDocLiteDAO dao) {
        this.dao = dao;
    }

    /**
     * Loads the named style from the database, or (if configured) imports it from a file
     * specified via a configuration parameter with a name of the format edl.style.&lt;styleName&gt;
     * {@inheritDoc}
     * @see org.kuali.rice.edl.impl.service.StyleService#getStyle(java.lang.String)
     */
    public EDocLiteStyle getStyle(String styleName) {
        EDocLiteStyle result = null;
        // try to fetch the style from the database
        result = dao.getEDocLiteStyle(styleName);
        // if it's null, look for a config param specifiying a file to load
        if (result == null) {
            String propertyName = STYLE_CONFIG_PREFIX + "." + styleName;
            String location = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
            if (location != null) {

                InputStream xml = null;

                try {
                    xml = RiceUtilities.getResourceAsStream(location);
                } catch (MalformedURLException e) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location), e);
                } catch (IOException e) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location), e);
                }

                if (xml == null) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location) + ", no such file");
                }

                Element style = findNamedStyle(styleName, xml);

                if (style == null) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location) +
                            ", no style named '"+ styleName +"' in that file");
                }

                EDocLiteStyle loadedStyle = parseEDocLiteStyle(style);
                // redundant check, but it doesn't hurt
                if (!styleName.equals(loadedStyle.getName())) {
                    throw new RiceRuntimeException("EDocLiteStyle loaded from " + location +
                            " does not contain style named " + styleName);
                } else {
                    LOG.info("importing style '" + styleName + "' from '" + location + "' as configured by "+ propertyName);
                    saveStyle(loadedStyle);
                    result = loadedStyle;
                }
            }
        }
        return result;
    }

    /**
     * returns the first <style> element it encounters with the
     * given name attribute from an xml document
     */
    private Element findNamedStyle(String styleName, InputStream xml) {
        Element style = null;

        Document xmlDoc = parse(xml);
        NodeList nodes = xmlDoc.getElementsByTagName("style");
        if (nodes != null) for (int i=0; i<nodes.getLength(); i++) {
            Element element = (Element)nodes.item(i);
            NamedNodeMap attrs = element.getAttributes();
            if (attrs != null) {
                Attr attr = (Attr)attrs.getNamedItem("name");
                if (styleName.equals(attr.getValue())) {
                    style = element;
                    break;
                }
            }
        }
        return style;
    }

    /**
     * This method ...
     *
     * @param propertyName
     * @param location
     * @return
     */
    private String getUnableToLoadMessage(String propertyName, String location) {
        return "unable to load resource at '" + location +
                "' specified by configuration parameter '" + propertyName + "'";
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

            boolean useXSLTC = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.EDOC_LITE_DETAIL_TYPE, KEWConstants.EDL_USE_XSLTC_IND);
            if (useXSLTC) {
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

            if (useXSLTC) {
                factory.setAttribute("translet-name",name);
                factory.setAttribute("generate-translet",Boolean.TRUE);
                String debugTransform = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.EDOC_LITE_DETAIL_TYPE, KEWConstants.EDL_DEBUG_TRANSFORM_IND);
                if (debugTransform.trim().equals("Y")) {
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
        EDocLiteStyle existingData = dao.getEDocLiteStyle(data.getName());
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

    public void loadXml(InputStream inputStream, String principalId) {
        StyleXmlParser.loadXml(this, inputStream, principalId);
    }

    // ---- XmlExporter interface implementation
    public org.jdom.Element export(ExportDataSet dataSet) {
        return new StyleXmlExporter().export(dataSet);
    }
    
	@Override
	public boolean supportPrettyPrint() {
		return false;
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
            style.setXmlContent(XmlJotter.jotNode(stylesheet, true));
        } catch (XmlException te) {
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
            WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error parsing Style XML file", new WorkflowServiceErrorImpl("Error parsing XML file.", KEWConstants.XML_FILE_PARSE_ERROR));
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

    private static WorkflowServiceErrorException generateSerializationException(String element, XmlException cause) {
        return generateException("Error serializing EDocLite '" + element + "' element", cause);
    }

    private static WorkflowServiceErrorException generateException(String error, Throwable cause) {
        WorkflowServiceErrorException wsee = new WorkflowServiceErrorException(error, new WorkflowServiceErrorImpl(error, KEWConstants.XML_FILE_PARSE_ERROR));
        if (cause != null) {
            wsee.initCause(cause);
        }
        return wsee;
    }
}
