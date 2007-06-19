package edu.iu.uis.eden.messaging.resourceloading;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;


public class KSBResourceLoaderFactoryTest extends TestCase {

	
	@Test public void testCreateKSBResourceLoader() throws Exception {
		String me = "TestME";
		Properties props = new Properties();
		props.put(Config.MESSAGE_ENTITY, me);
		Config config = new SimpleConfig(props);
		config.parseConfig();
		Core.init(config);
		
		ResourceLoader rl = KSBResourceLoaderFactory.createRootKSBResourceLoader();
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getSpringResourceLoaderName()));
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
		assertNotNull(GlobalResourceLoader.getResourceLoader(KSBResourceLoaderFactory.getSpringResourceLoaderName()));
		assertNotNull(GlobalResourceLoader.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
	}
	
	@Test public void testCreateKSBResourceLoaderNoMessageEntity() throws Exception {
		
		Properties props = new Properties();
		Config config = new SimpleConfig(props);
		config.parseConfig();
		Core.init(config);
		
		boolean errorThrown = false;
		try {
			KSBResourceLoaderFactory.createRootKSBResourceLoader();
			fail("should have thrown configuration exception with no message entity present");
		} catch (ConfigurationException ce) {
			errorThrown = true;
		}
		assertTrue(errorThrown);
	}
	
}