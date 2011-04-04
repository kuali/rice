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

package org.kuali.rice.core.impl.style;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.style.Style;
import org.kuali.rice.core.api.style.StyleRepositoryService;
import org.kuali.rice.core.api.style.StyleService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.ksb.cache.RiceCacheAdministrator;


/**
 * Implements generic StyleService via existing EDL style table
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleServiceImpl implements StyleService {
	
    private static final Logger LOG = Logger.getLogger(StyleServiceImpl.class);

    private StyleRepositoryService styleRepositoryService;
    private RiceCacheAdministrator cache;

    public void setStyleRepositoryService(StyleRepositoryService styleRepositoryService) {
    	this.styleRepositoryService = styleRepositoryService;
    }
    
    public void setCache(RiceCacheAdministrator cache) {
    	this.cache = cache;
    }

    /**
     * Loads the named style from the database, or (if configured) imports it from a file
     * specified via a configuration parameter with a name of the format edl.style.&lt;styleName&gt;
     * {@inheritDoc}
     * @see org.kuali.rice.edl.impl.service.StyleService#getStyle(java.lang.String)
     */
    @Override
    public Style getStyle(String styleName) {
    	return styleRepositoryService.getStyle(styleName);
    }

    @Override
    public Templates getStyleAsTranslet(String name) throws TransformerConfigurationException {
        if (name == null) return null;
        Templates translet = fetchTemplatesFromCache(name);
        if (translet == null) {
            Style style = getStyle(name);
            if (style == null) {
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
            factory.setURIResolver(new StyleUriResolver(this));

            if (useXSLTC) {
                factory.setAttribute("translet-name",name);
                factory.setAttribute("generate-translet",Boolean.TRUE);
                String debugTransform = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.EDOC_LITE_DETAIL_TYPE, KEWConstants.EDL_DEBUG_TRANSFORM_IND);
                if (debugTransform.trim().equals("Y")) {
                    factory.setAttribute("debug", Boolean.TRUE);
                }
            }

            translet = factory.newTemplates(new StreamSource(new StringReader(style.getXmlContent())));
            putTemplatesInCache(name, translet);
        }
        return translet;
    }

    @Override
    public void saveStyle(Style style) {
    	styleRepositoryService.saveStyle(style);
    }
    
    @Override
    public List<String> getAllStyleNames() {
        return styleRepositoryService.getAllStyleNames();
    }
        
    // cache helper methods

    /**
     * Returns the key to be used for caching the Templates for the given style name.
     */
    protected String getTemplatesCacheKey(String styleName) {
        return StyleRepositoryServiceImpl.TEMPLATES_CACHE_GROUP_NAME + ":" + styleName;
    }

    protected Templates fetchTemplatesFromCache(String styleName) {
        return (Templates) cache.getFromCache(getTemplatesCacheKey(styleName));
    }

    protected void putTemplatesInCache(String styleName, Templates templates) {
        cache.putInCache(getTemplatesCacheKey(styleName), templates, StyleRepositoryServiceImpl.TEMPLATES_CACHE_GROUP_NAME);
    }

}
