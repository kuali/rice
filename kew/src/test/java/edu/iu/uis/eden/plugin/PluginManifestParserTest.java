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
package edu.iu.uis.eden.plugin;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

/**
 * Tests that the PluginManifestParser properly parses the plugin manifest xml file.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginManifestParserTest extends KEWTestCase {

//    private PluginManifestParser parser = new PluginManifestParser();
//    private static final String MANIFEST_PATH = "test/src/edu/iu/uis/eden/plugin/workflow.xml";
//    private static final String EDEN_NAME = "jdbc/dev/en/EDEN";
//    private static final String SUDS_NAME = "jdbc/dev/en/SUDS";
//    private static final String DS_TYPE = "javax.sql.DataSource";
//    private static final String FACTORY = "org.apache.commons.dbcp.BasicDataSourceFactory";
//    private static final String URL = "jdbc:oracle:thin:@es01.uits.indiana.edu:1521:GEN2DEV";
    
//    public PluginManifestParserTest(String name) {
//        super(name);
//    }

    @Ignore("Re-implement this test according to EN-583")
    @Test public void testParse() throws Exception {
    	
//        PluginManifest plugin = parser.parse(new File(MANIFEST_PATH), Core.getRootConfig());
//        assertNotNull(plugin);
//        assertEquals(5, plugin.getExtensions().size());
//
//        List extensions = plugin.getExtensions();
//        Iterator extIter = extensions.iterator();
//        int foundCounter = 0;
//        while (extIter.hasNext()) {
//            ExtensionConfig ec = (ExtensionConfig) extIter.next();
//            if ("userService".equals(ec.getPoint()) && "edu.iu.uis.eden.user.impl.IUUserServiceExtension".equals(ec.getExtensionClass())) {
//                foundCounter++;
//            } else if ("workgroupService".equals(ec.getPoint()) && "edu.iu.uis.eden.workgroup.IUWorkgroupServiceExtension".equals(ec.getExtensionClass())) { 
//                foundCounter++;
//            } else if ("lookupableService".equals(ec.getPoint()) && "edu.iu.uis.eden.lookupable.IULookupableServiceExtension".equals(ec.getExtensionClass())) {
//                foundCounter++;
//            } else if ("webAuthenticationService".equals(ec.getPoint()) && "edu.iu.uis.eden.web.IUWebAuthenticationServiceExtension".equals(ec.getExtensionClass())) {
//                foundCounter++;
//            } else if ("notes".equals(ec.getPoint()) && "edu.iu.uis.eden.plugin.extension.ConfigurableNotesExtension".equals(ec.getExtensionClass())) {
//                foundCounter++;
//            } 
//            if ("notes".equals(ec.getPoint())) {
//                assertEquals(1, ec.getParameters().size());
//                assertEquals(true, ec.getParameters().containsKey("defaultClass"));
//                String parameter1 = (String)ec.getParameters().get("defaultClass");
//                assertNotNull(parameter1);
//                assertEquals("edu.iu.uis.eden.notes.DefaultNoteAttribute", parameter1);
//            }
//        }
//        
//        assertEquals(5, foundCounter);
//        List listeners = plugin.getListeners();
//        assertNotNull(listeners);
//        assertEquals(2, listeners.size());
//        String listenerClassName1 = (String)listeners.get(0);
//        String listenerClassName2 = (String)listeners.get(1);
//        assertEquals("edu.iu.uis.eden.plugin.TestPluginListener", listenerClassName1);
//        assertEquals("edu.iu.uis.eden.plugin.TestPluginListener2", listenerClassName2);
    }

}
