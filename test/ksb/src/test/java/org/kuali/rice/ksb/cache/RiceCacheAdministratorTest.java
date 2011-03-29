/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ksb.cache;

import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.test.KSBTestCase;
import org.springframework.context.ApplicationContext;

import javax.xml.namespace.QName;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RiceCacheAdministratorTest extends KSBTestCase {
	
	String key = "_key";
	Object value = "_value";
	String group = "_group";
	
	@Override
	public boolean startClient1() {
		return true;
	}

	@Test public void testCache() throws Exception {
		
		loadCaches();
		assertCachesNotEmpty();
		
		//drive invalidation from this cache
		RiceCacheAdministrator cache = (RiceCacheAdministrator)getSpringContextResourceLoader().getService(new QName("cache"));
		cache.flushAll();
		//these waits are extreme and things rarely take this long but when CI bogs down it can take a while
		Thread.sleep(4000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
		cache.flushEntry(this.key);
		Thread.sleep(4000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
		cache.flushGroup(this.group);
		Thread.sleep(4000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
	}
	
	private void assertCachesNotEmpty() throws Exception {
		RiceCacheAdministrator cache = (RiceCacheAdministrator)getSpringContextResourceLoader().getService(new QName("cache"));
		RiceCacheAdministrator client1Cache = (RiceCacheAdministrator)getServiceFromTestClient1SpringContext("cache");
		
		assertEquals(this.value, cache.getFromCache(this.key));
		assertEquals(this.value, client1Cache.getFromCache(this.key));
	}
	
	private void assertCachesEmpty() throws Exception {
		RiceCacheAdministrator cache = (RiceCacheAdministrator)getSpringContextResourceLoader().getService(new QName("cache"));
		RiceCacheAdministrator client1Cache = (RiceCacheAdministrator)getServiceFromTestClient1SpringContext("cache");
		
		assertNull(cache.getFromCache(this.key));
		assertNull(client1Cache.getFromCache(this.key));
	}
	
	private void loadCaches() throws Exception {	
		RiceCacheAdministrator cache = (RiceCacheAdministrator)getSpringContextResourceLoader().getService(new QName("cache"));
		RiceCacheAdministrator client1Cache = (RiceCacheAdministrator)getServiceFromTestClient1SpringContext("cache");
		
		cache.putInCache(this.key, this.value, this.group);
		client1Cache.putInCache(this.key, this.value, this.group);
	}

    public static Object getServiceFromTestClient1SpringContext(String serviceName) {
        for (Map.Entry<ClassLoader, Config> configEntry : ConfigContext.getConfigs()) {
            if (configEntry.getKey() instanceof WebAppClassLoader) {
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                // to make GRL select services from correct classloader
                Thread.currentThread().setContextClassLoader(configEntry.getKey());
                try {
                    // TestClient1SpringContext found in web.xml of TestClient1
                    ApplicationContext appContext = (ApplicationContext) ConfigContext.getCurrentContextConfig().getObject("TestClient1SpringContext");

                    return appContext.getBean(serviceName);
                } finally {
                    Thread.currentThread().setContextClassLoader(old);
                }
            }
        }
        throw new RiceRuntimeException("Couldn't find service " + serviceName + " in TestClient1 Spring Context");
    }
	
}

