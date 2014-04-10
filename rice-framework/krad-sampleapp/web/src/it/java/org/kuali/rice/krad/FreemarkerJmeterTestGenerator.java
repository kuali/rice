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
package org.kuali.rice.krad;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import org.apache.commons.io.FileUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The JMeter.ftl must use "@{" for JMeter variables these are converted to "${" after the templating.  Required to avoid
 * Freemarker errors caused by JMeter variables being interpreted as Freemarker variables.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreemarkerJmeterTestGenerator {

    private static Configuration cfg = new Configuration();

    private static final String[] propertiesArray = {"testplanname", "testpath", "viewid", "pageid", "pageundertesttimeoutms"};

    // Templates for File Generation
    private static String DIR_TMPL = "/jmeter/";

    //Configuration
    private static TemplateLoader templateLoader = new ClassTemplateLoader(FreemarkerJmeterTestGenerator.class, DIR_TMPL);

    public static void main(String[] args) throws Exception {
        cfg.setTemplateLoader(templateLoader);

        String template = "KRAD-JMeter.ftl";
        String csvLocation = "/jmeter/KRAD.csv";

        csvFreemarkerTemplate(template, csvLocation);
    }

    private static void csvFreemarkerTemplate(String template, String csvLocation) throws IOException {
        InputStream in = FreemarkerJmeterTestGenerator.class.getResourceAsStream(csvLocation);
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(in));

        try {
            String csvLine = null;
            int i = 0;
            Properties csvProperties = null;
            StringTokenizer tokenizer = null;
            File jmeterTest = null;
            String pathPart = null;

            while ((csvLine = csvReader.readLine()) != null && (!csvLine.isEmpty())) {
                i = 0;
                csvProperties = new Properties();
                System.out.println("Processing " + csvLine);
                tokenizer = new StringTokenizer(csvLine, ",");

                while (tokenizer.hasMoreTokens()) {
                    // remove quotes (from the testplanname)
                    csvProperties.put(propertiesArray[i++], tokenizer.nextToken().replace("\"", ""));
                }

                // TODO would be nice to allow overriding via System params like the other Freemarker Generators do
                pathPart = csvProperties.getProperty(propertiesArray[1]);
                if (pathPart != null) {
                    pathPart = pathPart.substring(pathPart.lastIndexOf(File.separatorChar) + 1, pathPart.length());
                    jmeterTest = new File(pathPart + "_" + csvProperties.getProperty(propertiesArray[2]) + "_" + csvProperties.getProperty(propertiesArray[3]) + ".jmx");
                    String output = FreeMarkerTemplateUtils.processTemplateIntoString(cfg.getTemplate(template), csvProperties);
                    output = output.replace("@{", "${");
                    FileUtils.writeStringToFile(jmeterTest, output);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }
}