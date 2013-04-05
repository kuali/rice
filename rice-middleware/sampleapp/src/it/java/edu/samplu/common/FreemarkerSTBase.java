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
package edu.samplu.common;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @see FreemarkerUtil
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class FreemarkerSTBase extends WebDriverLegacyITBase {

    protected final Logger LOG = Logger.getLogger(getClass());

    protected Configuration cfg;

    /**
     * Calls ftlWrite that also accepts a key, using the output getName as the key.
     * {@link FreemarkerUtil#ftlWrite(java.io.File, freemarker.template.Template, java.io.InputStream)}
     * @param output
     * @param template
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public File ftlWrite(File output, Template template, InputStream inputStream) throws IOException, TemplateException {

        return FreemarkerUtil.ftlWrite(output.getName(), output, template, inputStream);
    }

    /**
     * TODO can we cut this down to one param?
     * {@link FreemarkerUtil#loadProperties(java.io.InputStream)}
     * @param fileLocation null means use resourceLocation
     * @param resourceLocation
     * @return
     * @throws IOException
     */
    protected Properties loadProperties(String fileLocation, String resourceLocation) throws IOException {
        Properties props = new Properties();
        InputStream in = null;
        if(fileLocation != null) {
            in = new FileInputStream(fileLocation);
        } else {
            in = getClass().getClassLoader().getResourceAsStream(resourceLocation);
        }
        if(in != null) {
            FreemarkerUtil.loadProperties(in);
            in.close();
        }

        return props;
    }

    /**
     * {@link FreemarkerUtil#systemPropertiesOverride(java.util.Properties, String)}
     * @param props
     * @param key
     */
    protected void systemPropertiesOverride(Properties props, String key) {
        FreemarkerUtil.systemPropertiesOverride(props, key);
    }

    /**
    * {@link FreemarkerUtil#writeTemplateToFile(java.io.File, freemarker.template.Template, java.util.Properties)}
    * @param file
    * @param template
    * @param props
    * @return
    * @throws IOException
    * @throws TemplateException
    */
    protected static File writeTemplateToFile(File file, Template template, Properties props) throws IOException, TemplateException {
        return FreemarkerUtil.writeTemplateToFile(file, template, props);
    }
}