/*
 * Copyright 2007-2008 The Kuali Foundation
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

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.test.KSBTestCase;
import org.kuali.rice.test.shared.service.CacheTestService;

public class RiceCacheAdministratorIT extends KSBTestCase {
	
	private CacheTestService client1Cache;
	private CacheTestService client2Cache;
	
	String key = "_key";
	String value = "_value";
	String group = "_group";
	
	@Before public void setUp() throws Exception {
		
		super.setUp();
		
		client1Cache = (CacheTestService)GlobalResourceLoader.getService(new QName("TestCl1", "cacheTestService"));
		client2Cache = (CacheTestService)GlobalResourceLoader.getService(new QName("TestCl2", "cacheTestService"));
		
		assertNotNull("client1Cache should not be null", client1Cache);
		assertNotNull("client2Cache should not be null", client2Cache);
	}
	
	@Test public void testCache() throws Exception {
		
		loadCaches();
		assertCachesNotEmpty();
		
		//drive invalidation from the client1Cache
		client1Cache.flushAll();
		//these waits are extreme and things rarely take this long but when CI bogs down it can take a while
		Thread.sleep(15000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
		client1Cache.flushEntry(this.key);
		Thread.sleep(15000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
		client1Cache.flushGroup(this.group);
		Thread.sleep(15000);
		assertCachesEmpty();
		
		loadCaches();
		assertCachesNotEmpty();
		
	}
	
	private void assertCachesNotEmpty() throws Exception {
		assertEquals(this.value, client1Cache.getFromCache(this.key));
		assertEquals(this.value, client2Cache.getFromCache(this.key));
	}
	
	private void assertCachesEmpty() throws Exception {
		assertNull(client1Cache.getFromCache(this.key));
		assertNull(client2Cache.getFromCache(this.key));
	}
	
	private void loadCaches() throws Exception {	
		client1Cache.putInCache(this.key, this.value, this.group);
		client2Cache.putInCache(this.key, this.value, this.group);
	}
	
}

