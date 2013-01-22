/**
 * Copyright 2005-2011 The Kuali Foundation
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NeustarJSTemplateLegacyIT {

    protected final Logger LOG = Logger.getLogger(getClass());

    // File generation
    private Configuration cfg;
    private String PROPS_LOCATION = System.getProperty("neustarJS.props.location", null);
    private String DEFAULT_PROPS_LOCATION = "NeustarJSTemplate/neustarJS.properties";

    // Templates for File Generation
    private static final String DIR_TMPL = "/NeustarJSTemplate/";
    private static final String TMPL_CONTENT = "CreateNewTmpl.ftl";


    public void setUpConfig() throws Exception {
        // generated load users and group resources
        cfg = new Configuration();
        cfg.setTemplateLoader(new ClassTemplateLoader(getClass().getClassLoader().getClass(), DIR_TMPL));
    }

    private void buildFileList(Properties props) throws Exception {
        
        Integer pageCount= Integer.parseInt(props.getProperty("pageCount"));
        
        for(int count=1; count<= pageCount;count++ ){
            try {
                String subTitle= props.getProperty("page"+count);
                props.setProperty("pageId",""+ props.get("page")+count);
                
                // Setting props and building files of KRAD tab
                props.setProperty("viewId",""+ props.get("view"));                          
                File f1= new File("Temp" + File.separatorChar + "Env11 Kitchen Sink "+subTitle +" KRAD WebDriver.txt");                   
                String output1 = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(TMPL_CONTENT), props);
                FileUtils.writeStringToFile(f1, output1);
                
                // Setting props and building files of KRAD tab
                props.setProperty("viewId",""+ props.get("view")+"_KNS");
                File f2= new File("Temp" + File.separatorChar + "Env11 Kitchen Sink "+subTitle +" KNS WebDriver.txt"); 
                String output2 = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(TMPL_CONTENT), props);
                FileUtils.writeStringToFile(f2, output2);
                
            } catch( Exception e) {
                throw new Exception("Unable to generate files for upload", e);
            }
            }
        
    }

    /**
     * In case You want to override properties
     * @param props
     */
    private void systemPropertiesOverride(Properties props) {
        Enumeration<?> names = props.propertyNames();
        Object nameObject;
        String name;
        while (names.hasMoreElements()) {
            nameObject = names.nextElement();
            if (nameObject instanceof String) {
                name = (String)nameObject;
                props.setProperty(name, System.getProperty("freemarker." + name, props.getProperty(name)));
            }
        }
    }

    /**
     * Based on load user and groups manual tests; dynamically generates user and group file
     * and loads into the xml ingester screen
     *
     */
    @Test
    public void testXMLIngesterSuccessfulFileUpload() throws Exception {
        //Configuration Setup
        setUpConfig();
        // update properties with timestamp value if includeDTSinPrefix is true
        Properties props = loadProperties(PROPS_LOCATION, DEFAULT_PROPS_LOCATION);
        //Generate Files
        buildFileList(props);
    }

    /**
     * Loads properties from user defined properties file, if not available uses resource file
     *
     * @return
     * @throws IOException
     */
    private Properties loadProperties(String fileLocation, String resourceLocation) throws IOException {
        Properties props = new Properties();
        InputStream in = null;
        if(fileLocation != null) {
            in = new FileInputStream(fileLocation);
        } else {
            in = getClass().getClassLoader().getResourceAsStream(resourceLocation);
        }
        if(in != null) {
            props.load(in);
            in.close();
        }
        return props;
    }
}
