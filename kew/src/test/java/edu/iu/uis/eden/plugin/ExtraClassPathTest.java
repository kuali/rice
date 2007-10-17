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

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.test.TestUtilities;

/**
 * Tests that the extra classpath features of the plugin work as advertised.
 *
 * <p>Adds the test/src/edu/iu/uis/eden/plugin/classes directory to the extra classes on
 * the classpath.  Adds the test/src/edu/iu/uis/eden/plugin/lib directory to the extra
 * libs on the classpath.  Within the lib directory is a jar called extraclasspath.jar.
 * Inside this jar is a single resource called extraclasspath-lib.txt.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExtraClassPathTest extends KEWTestCase {

	@Override
	public void setUp() throws Exception {
		// we want to copy the ziptest plugin into the plugin directories before the
		// test harness starts up.  That way the plugin will be loaded at startup time.
		TestUtilities.initializePluginDirectories();
		File pluginZipFile = new File("test/src/edu/iu/uis/eden/plugin/extraclasspathtest.zip");
		assertTrue(pluginZipFile.exists());
		assertTrue(pluginZipFile.isFile());
		FileUtils.copyFileToDirectory(pluginZipFile, TestUtilities.getPluginsDirectory());
		pluginZipFile = new File(TestUtilities.getPluginsDirectory(), pluginZipFile.getName());
		FileUtils.forceDeleteOnExit(pluginZipFile);
		super.setUp();
	}



	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		TestUtilities.cleanupPluginDirectories();
	}

	@Ignore
	@Test public void testExtraClassPath() throws Exception {
		// first of all, let's check that the plugin was loaded when the test harness started up
		PluginRegistry registry = PluginUtils.getPluginRegistry();
		List<PluginEnvironment> environments = registry.getPluginEnvironments();
		assertEquals("There should be 1 plugin environment.", 1, environments.size());

		PluginEnvironment environment = environments.get(0);
		assertEquals("Should be the extraclasspathtest plugin.", "extraclasspathtest", environment.getPlugin().getName().getLocalPart());

		// check that the properties were configured correctly
		File extraClassesDir = new File(environment.getPlugin().getConfig().getProperty(Config.EXTRA_CLASSES_DIR));
		assertTrue("extra classes dir should exist.", extraClassesDir.exists());
		assertTrue("extra classes dir should be a directory.", extraClassesDir.isDirectory());
		File extraLibDir = new File(environment.getPlugin().getConfig().getProperty(Config.EXTRA_LIB_DIR));
		assertTrue("extra lib dir should exist.", extraLibDir.exists());
		assertTrue("extra lib dir should be a directory.", extraLibDir.isDirectory());

		// now verify that the resources from the extra classes and extra lib dirs got loaded
		ClassLoader classLoader = environment.getPlugin().getClassLoader();
		assertNotNull("Resource should exist.", classLoader.getResource("extraclasspath-classes.txt"));
		assertNotNull("Resource should exist.", classLoader.getResource("extraclasspath-lib.txt"));

	}

}
