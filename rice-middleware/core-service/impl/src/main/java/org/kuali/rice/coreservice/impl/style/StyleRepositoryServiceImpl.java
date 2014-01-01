/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.coreservice.impl.style;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceUtilities;
import org.kuali.rice.coreservice.api.style.Style;
import org.kuali.rice.coreservice.api.style.StyleRepositoryService;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Implements generic StyleService via existing EDL style table
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleRepositoryServiceImpl implements StyleRepositoryService {
    private static final Logger LOG = Logger.getLogger(StyleRepositoryServiceImpl.class);

    private static final String STYLE_CONFIG_PREFIX = "edl.style";

    private StyleXmlParser styleXmlParser;
    private DataObjectService dataObjectService;
    private StyleDao styleDao;

    public void setStyleXmlParser(StyleXmlParser styleXmlParser) {
        this.styleXmlParser = styleXmlParser;
    }

    /**
     * Loads the named style from the database, or (if configured) imports it from a file
     * specified via a configuration parameter with a name of the format edl.style.&lt;styleName&gt;
     * {@inheritDoc}
     */
    @Override
    public Style getStyle(String styleName) {
        if (StringUtils.isBlank(styleName)) {
            throw new RiceIllegalArgumentException("styleName was null or blank");
        }

        StyleBo style = getStyleByName(styleName);
        // try to fetch the style from the database


        // if it's null, look for a config param specifiying a file to load
        if (style == null) {
            String propertyName = STYLE_CONFIG_PREFIX + "." + styleName;
            String location = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
            if (location != null) {

                final InputStream xml;

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
                List<Style> styles = styleXmlParser.parseStyles(xml);
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

    private String getUnableToLoadMessage(String propertyName, String location) {
        return "unable to load resource at '" + location +
                "' specified by configuration parameter '" + propertyName + "'";
    }

    /**
     *
     * @return first style that matches a particular name, null otherwise
     */
    private StyleBo getStyleByName(String styleName){
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("name", styleName);
        attributes.put("active", Boolean.TRUE);
        QueryResults<StyleBo> styleBos =
                dataObjectService.findMatching(StyleBo.class, QueryByCriteria.Builder.andAttributes(attributes).build());
        if(styleBos != null && styleBos.getResults().size() > 0){
            return styleBos.getResults().get(0);
        }
        return null;
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
        StyleBo existingData = getStyleByName(styleBo.getName());
        if (existingData != null) {
            existingData.setActive(false);
            dataObjectService.save(existingData);
        }
        dataObjectService.save(styleBo);
    }

    @Override
    public List<String> getAllStyleNames() {
        return styleDao.getAllStyleNames();
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }
    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    @Required
    public void setStyleDao(StyleDao styleDao) {
        this.styleDao = styleDao;
    }
}
