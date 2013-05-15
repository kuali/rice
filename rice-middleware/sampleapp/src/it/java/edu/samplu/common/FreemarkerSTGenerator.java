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

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import sun.applet.Main;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreemarkerSTGenerator {
    private static Configuration cfg = new Configuration();

    // Templates for File Generation
    private static String DIR_TMPL = "/Gen/";

    //Configuration
    private static TemplateLoader templateLoader = new ClassTemplateLoader(Main.class, DIR_TMPL);

    public static void main(String[] args) throws Exception {
        cfg.setTemplateLoader(templateLoader);

        String DEFAULT_PROPS_LOCATION = "/GenFiles/Group.properties";
        String STJUNITBASE_TMPL = "STJUnitBase.ftl";
        String STJUNITBKMRKGEN_TMPL = "STJUnitBkMrkGen.ftl";
        String STJUNITNAVGEN_TMPL = "STJUnitNavGen.ftl";
        String STNGBASE_TMPL = "STNGBase.ftl";
        String STNGBKMRKGEN_TMPL = "STNGBkMrkGen.ftl";
        String STNGNAVGEN_TMPL = "STNGNavGen.ftl";

        //Here we can prepare a list of template & properties file and can iterate to generate files dynamically on single run.
        createFile(DEFAULT_PROPS_LOCATION, STJUNITBASE_TMPL);
        createFile(DEFAULT_PROPS_LOCATION, STJUNITBKMRKGEN_TMPL);
        createFile(DEFAULT_PROPS_LOCATION, STJUNITNAVGEN_TMPL);
        createFile(DEFAULT_PROPS_LOCATION, STNGBASE_TMPL);
        createFile(DEFAULT_PROPS_LOCATION, STNGBKMRKGEN_TMPL);
        createFile(DEFAULT_PROPS_LOCATION, STNGNAVGEN_TMPL);
    }

    private static void createFile(String DEFAULT_PROPS_LOCATION, String TMPL) throws Exception {
        try {
            //Loading Properties file
            Properties props = new Properties();
            InputStream in = null;
            in = Main.class.getResourceAsStream(DEFAULT_PROPS_LOCATION);

            if (in != null) {
                props.load(in);
                in.close();
            } else {
                throw new Exception("Problem with input stream.");
            }

            // build file STJUnitBase and add to array              
            File f1 = new File("src" + File.separatorChar + "it" + File.separatorChar + "resources"
                    + File.separatorChar + "GenFiles" + File.separatorChar + props.getProperty("className")
                    + TMPL.substring(0, TMPL.length() - 4) + ".java");

            //Write Content in file
            FreemarkerUtil.writeTemplateToFile(f1, cfg.getTemplate(TMPL), props);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Unable to generate files", e);
        }
    }
}