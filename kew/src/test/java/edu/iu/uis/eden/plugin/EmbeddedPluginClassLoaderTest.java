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

@Ignore
public class EmbeddedPluginClassLoaderTest { //extends TestCase {

    	
//	@Test public void testLoadEmbeddedResources() throws Exception {
//    	    
//		// load the embedded-test.jar
//		File embeddedJar = new File(TestUtils.getBaseDir()+"/src/test/resources/edu/iu/uis/eden/plugin/embedded-test.jar");
//		assertTrue(embeddedJar.exists());
//		assertTrue(embeddedJar.isFile());
//		URL embeddedURL = embeddedJar.toURL();
//		PluginClassLoader pluginClassLoader = new PluginClassLoader();
//		pluginClassLoader.addURL(embeddedURL);
//
//		// first test that we can load the META-INF/embedded-workflow.xml file
//		URL embeddedWFXml = pluginClassLoader.getResource("embedded/META-INF/embedded-workflow.xml");
//		assertNotNull("should be able to find embedded-workflow.xml", embeddedWFXml);
//
//		InputStream stream = embeddedWFXml.openStream();
//		int numBytes = 0;
//		while (stream.read() != -1) {
//			numBytes++;
//		}
//		assertTrue("There should be more than 0 bytes in the embedded-workflow.xml file.", numBytes > 0);
//
//		// test that we can load the classes-test.txt file
//		URL classesTestUrl = pluginClassLoader.getResource("embedded/classes/classes-test.txt");
//		assertNotNull("Failed to load the classes-test.txt file.", classesTestUrl);
//
//		// now create the embedded classloader, it's important that the pluginClassLoader is that parent
//		// because that allows us to pull the embedded plugin out of the jar at
//		// test/src/edu/iu/uis/eden/plugin/embedded-test.jar
//		// This jar has some special files in it that we use to verify the jar in this test case.
//		EmbeddedPluginClassLoader embeddedCL = new EmbeddedPluginClassLoader(pluginClassLoader, "embedded");
//
//		// try to load the AxisClient class which should originate in a jar
//		String axisClientClassName = "org.apache.axis.client.AxisClient";
//		try {
//			Class axisClientClass = embeddedCL.loadClass(axisClientClassName);
//			assertEquals("Wrong classloader.", embeddedCL, axisClientClass.getClassLoader());
//		} catch (ClassNotFoundException e) {
//			fail("Could not load the axis client.");
//		}
//		// assert that, even after loading the class, it's still possible to access the .class file as a resource
//		String axisClientClassResourceName = axisClientClassName.replace(".", "/").concat(".class");
//		URL axisClientClassURL = embeddedCL.getResource(axisClientClassResourceName);
//		assertNotNull(axisClientClassURL);
//		// also test that we can load the resource, even if it has a / at the beginning
//		axisClientClassURL = embeddedCL.getResource("/"+axisClientClassResourceName);
//		assertNotNull(axisClientClassURL);
//
//		// try to load the WorkflowUtilityServiceEndpoint which should be in the embedded/classes directory
//		try {
//			Class endpointClass = embeddedCL.loadClass("edu.iu.uis.eden.server.WorkflowUtilityServiceEndpoint");
//			assertEquals("Wrong classloader.", embeddedCL, endpointClass.getClassLoader());
//		} catch (ClassNotFoundException e) {
//			fail("Could not load the workflow utility service endpoint.");
//		}
//
//		// now try loading the same class as a resource
//		URL endpointUrl = embeddedCL.getResource("edu/iu/uis/eden/server/WorkflowUtilityServiceEndpoint.class");
//		assertNotNull("Failed to locate the WorkflowUtilityServiceEndpoint class in the classloader.", endpointUrl);
//
//		// try to find a resource that shouldn't be in our embedded classloader (instead it's parent)
//		URL testSpringUrl = embeddedCL.findResource("TestSpring.xml");
//		assertNull("Shouldn't have been able to find TestSpring.xml", testSpringUrl);
//		// however, if we call getResource it should look in the parent classloader as well
//		testSpringUrl = embeddedCL.getResource("TestSpring.xml");
//		assertNotNull("Should have been able to find TestSpring.xml", testSpringUrl);
//
//
//		// try to load the Spring.xml resource
//		URL springUrl = embeddedCL.getResource("Spring.xml");
//		assertNotNull("Could not locate Spring.xml", springUrl);
//		springUrl = embeddedCL.findResource("Spring.xml");
//		assertNotNull("Could not find Spring.xml", springUrl);
//
//		// try to load the classes-test.txt resource
//		classesTestUrl = embeddedCL.getResource("classes-test.txt");
//		assertNotNull("Could not locate the classes-test.txt resource.", classesTestUrl);
//		BufferedReader reader = new BufferedReader(new InputStreamReader(classesTestUrl.openStream()));
//		String line = reader.readLine();
//		reader.close();
//		assertEquals("classes-test", line);
//
//		// try to load the lib-test.txt resource
//		URL libTestUrl = embeddedCL.getResource("lib-test.txt");
//		assertNotNull("Could not locate the lib-test.txt resource.", libTestUrl);
//		reader = new BufferedReader(new InputStreamReader(libTestUrl.openStream()));
//		line = reader.readLine();
//		reader.close();
//		assertEquals("lib-test", line);
//
//		// test with a slash at the beginning
//		libTestUrl = embeddedCL.getResource("/lib-test.txt");
//		assertNotNull("Could not locate the lib-test.txt resource.", libTestUrl);
//		reader = new BufferedReader(new InputStreamReader(libTestUrl.openStream()));
//		line = reader.readLine();
//		reader.close();
//		assertEquals("lib-test", line);
//
//	}

}
