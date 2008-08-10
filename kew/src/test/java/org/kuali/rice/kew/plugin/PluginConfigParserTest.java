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
package org.kuali.rice.kew.plugin;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.BaseResourceLoader;
import org.kuali.workflow.test.KEWTestCase;


/**
 * Tests that the PluginConfigParser properly parses the plugin config xml file.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginConfigParserTest extends KEWTestCase {

    private PluginConfigParser parser = new PluginConfigParser();
    private static final String CONFIG_PATH_SUFFIX = "/src/test/resources/org/kuali/rice/kew/plugin/workflow.xml";
    private static final String EDEN_NAME = "jdbc/dev/en/EDEN";
    private static final String SUDS_NAME = "jdbc/dev/en/SUDS";
    private static final String DS_TYPE = "javax.sql.DataSource";
    private static final String FACTORY = "org.apache.commons.dbcp.BasicDataSourceFactory";
    private static final String URL = "jdbc:oracle:thin:@es01.uits.indiana.edu:1521:GEN2DEV";
    
    @Test public void testParse() throws Exception {
    	
        PluginConfig plugin = parser.parse(new File(getBaseDir() + CONFIG_PATH_SUFFIX), ConfigContext.getRootConfig());
        assertNotNull(plugin);

        List listeners = plugin.getListeners();
        assertNotNull(listeners);
        assertEquals(2, listeners.size());
        String listenerClassName1 = (String)listeners.get(0);
        String listenerClassName2 = (String)listeners.get(1);
        assertEquals("org.kuali.rice.kew.plugin.TestPluginListener", listenerClassName1);
        assertEquals("org.kuali.rice.kew.plugin.TestPluginListener2", listenerClassName2);

        assertEquals("Plugin resource loader classname should be base resource loader", BaseResourceLoader.class.getName(), plugin.getResourceLoaderClassname());
    }

}
