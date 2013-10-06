/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.freemarker;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.InlineTemplateElement;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Register inline template processing adaptors for high-traffic KRAD templates.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreeMarkerInlineRenderBootstrap implements InitializingBean, ApplicationContextAware {
    
    /**
     * The freemarker configuration.
     */
    private static Configuration freeMarkerConfig;
    
    /**
     * Get the FreeMarker configuration initialized for the current KRAD application. 
     * 
     * @return The FreeMarker configuration initialized for the current KRAD application.
     */
    public static Configuration getFreeMarkerConfig() {
        if (freeMarkerConfig == null) {
            throw new IllegalStateException("FreeMarker configuruation is not available, "
                    + "use krad-base-servlet.xml or define FreeMarkerInlineRenderBootstrap in servlet.xml");
        }
        
        return freeMarkerConfig;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            freeMarkerConfig = ((FreeMarkerConfigurer) applicationContext.getBean("freemarkerConfig"))
                    .createConfiguration();
        } catch (IOException e) {
            throw new IllegalStateException("Error loading freemarker configuration", e);
        } catch (TemplateException e) {
            throw new IllegalStateException("Error loading freemarker configuration", e);
        }
    }

    /**
     * Register high-traffic KRAD template adaptors.  
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        InlineTemplateElement.registerAdaptor("script", new FreeMarkerScriptAdaptor());
        InlineTemplateElement.registerAdaptor("template", new FreeMarkerTemplateAdaptor());
        InlineTemplateElement.registerAdaptor("collectionGroup", new FreeMarkerCollectionGroupAdaptor());
        InlineTemplateElement.registerAdaptor("stacked", new FreeMarkerStackedAdaptor());
        InlineTemplateElement.registerAdaptor("groupWrap-open", new FreeMarkerOpenGroupWrapAdaptor());
        InlineTemplateElement.registerAdaptor("groupWrap-close", new FreeMarkerCloseGroupWrapAdaptor());
    }

}
