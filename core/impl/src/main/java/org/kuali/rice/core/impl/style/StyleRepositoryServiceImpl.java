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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.style.Style;
import org.kuali.rice.core.api.style.StyleRepositoryService;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;


/**
 * Implements generic StyleService via existing EDL style table
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleRepositoryServiceImpl implements StyleRepositoryService {
    private static final Logger LOG = Logger.getLogger(StyleRepositoryServiceImpl.class);

    static final String TEMPLATES_CACHE_GROUP_NAME = "Templates";
    private static final String STYLE_CONFIG_PREFIX = "edl.style";

    private StyleDao styleDao;
    private RiceCacheAdministrator cache;

    public void setStyleDao(StyleDao styleDao) {
        this.styleDao = styleDao;
    }

    public void setCache(RiceCacheAdministrator cache) {
        this.cache = cache;
    }

    /**
     * Loads the named style from the database, or (if configured) imports it from a file
     * specified via a configuration parameter with a name of the format edl.style.&lt;styleName&gt;
     * {@inheritDoc}
     *
     * @see org.kuali.rice.edl.impl.service.StyleService#getStyle(java.lang.String)
     */
    @Override
    public Style getStyle(String styleName) {
        if (StringUtils.isBlank(styleName)) {
            throw new RiceIllegalArgumentException("styleName was null or blank");
        }

        // try to fetch the style from the database
        StyleBo style = styleDao.getStyle(styleName);
        // if it's null, look for a config param specifiying a file to load
        if (style == null) {
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

                LOG.info("Automatically loading style '" + styleName + "' from '" + location + "' as configured by " + propertyName);
                List<Style> styles = CoreImplServiceLocator.getStyleXmlLoader().parseStyles(xml);
                for (Style autoLoadedStyle : styles) {
                    if (autoLoadedStyle.getName().equals(styleName)) {
                        return autoLoadedStyle;
                    }
                }
                throw new RiceRuntimeException("Failed to locate auto-loaded style '" + styleName + "' after successful parsing of file from '" + location + "' as configured by " + propertyName);
            }
        }
        return StyleBo.to(style);
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

    /**
     * Does not currently take into account style sheet dependences robustly
     */
    private void removeStyleFromCache(String styleName) {
        LOG.info("Removing Style " + styleName + " from the style cache");
        // we don't know what styles may import other styles so we need to flush them all
        cache.flushGroup(TEMPLATES_CACHE_GROUP_NAME);
        //KEWServiceLocator.getCacheAdministrator().flushEntry(getTemplatesCacheKey(styleName));
    }

    @Override
    public void saveStyle(Style data) {
        if (data == null) {
            throw new RiceIllegalArgumentException("The given style was null.");
        }
        StyleBo styleToUpdate = StyleBo.from(data);
        saveStyleBo(styleToUpdate);
    }

    protected void saveStyleBo(StyleBo styleBo) {
        StyleBo existingData = styleDao.getStyle(styleBo.getName());
        if (existingData != null) {
            existingData.setActive(false);
            styleDao.saveStyle(existingData);
        }
        styleDao.saveStyle(styleBo);
        removeStyleFromCache(styleBo.getName());
    }

    @Override
    public List<String> getAllStyleNames() {
        return styleDao.getAllStyleNames();
    }

    // cache helper methods

    /**
     * Returns the key to be used for caching the Templates for the given style name.
     */
    protected String getTemplatesCacheKey(String styleName) {
        return TEMPLATES_CACHE_GROUP_NAME + ":" + styleName;
    }

    protected Templates fetchTemplatesFromCache(String styleName) {
        return (Templates) cache.getFromCache(getTemplatesCacheKey(styleName));
    }

    protected void putTemplatesInCache(String styleName, Templates templates) {
        cache.putInCache(getTemplatesCacheKey(styleName), templates, TEMPLATES_CACHE_GROUP_NAME);
    }

}
