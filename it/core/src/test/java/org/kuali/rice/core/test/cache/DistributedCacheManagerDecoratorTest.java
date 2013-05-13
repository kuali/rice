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
package org.kuali.rice.core.test.cache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.core.impl.cache.DistributedCacheManagerDecorator;
import org.kuali.rice.core.test.CORETestCase;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.cache.CacheAdminService;
import org.kuali.rice.core.api.cache.CacheTarget;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import javax.xml.namespace.QName;
import static org.junit.Assert.*;


/**
 * Default test base for a full KNS enabled unit test.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DistributedCacheManagerDecoratorTest extends CORETestCase {

	 private static final String ROLE_RESPONSIBILITY_CACHE = "http://rice.kuali.org/kim/v2_0/RoleResponsibilityType";
	    private static final String ROLE_TYPE_CACHE = "http://rice.kuali.org/kim/v2_0/RoleType";
	    private static final String DELEGATE_TYPE_CACHE = "http://rice.kuali.org/kim/v2_0/DelegateTypeType";
	    private static final String ROLE_MEMBER_TYPE= "http://rice.kuali.org/kim/v2_0/RoleMemberType";
	    private static final String PERMISSION_TYPE = "http://rice.kuali.org/kim/v2_0/PermissionType";
	    private static final String INNER_CLASS = "CacheMessageSendingTransactionSynchronization";

	    @Before
	    public void setUp() throws Exception {
	        super.setUp();
	    }

	    @Test
	    public void testDuplicateCacheRemovalCase1() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	        Queue<CacheTarget> targets = Queues.newLinkedBlockingQueue(); 
	        targets.add(CacheTarget.entireCache(ROLE_RESPONSIBILITY_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_RESPONSIBILITY_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_MEMBER_TYPE));
	        targets.add(CacheTarget.entireCache(ROLE_MEMBER_TYPE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key2"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key3"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key4"));

	        ArrayList<CacheTarget> correctResults = Lists.newArrayList(
	                CacheTarget.entireCache(ROLE_RESPONSIBILITY_CACHE), 
	                CacheTarget.entireCache(ROLE_MEMBER_TYPE),
	                CacheTarget.entireCache(ROLE_TYPE_CACHE), 
	                CacheTarget.entireCache(DELEGATE_TYPE_CACHE),
	                CacheTarget.entireCache(PERMISSION_TYPE));

	        Collection<CacheTarget> results = new ArrayList<CacheTarget>(invokeExhaustQueue(targets));
	        assertTrue(CollectionUtils.diff(correctResults, results).isEmpty());
	    }

	    @Test
	    public void testDuplicateCacheRemovalCase2() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	        Queue<CacheTarget> targets = Queues.newLinkedBlockingQueue();
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key2"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key3"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key4"));

	        ArrayList<CacheTarget> correctResults = Lists.newArrayList(
	                CacheTarget.entireCache(ROLE_TYPE_CACHE), 
	                CacheTarget.entireCache(DELEGATE_TYPE_CACHE),
	                CacheTarget.entireCache(PERMISSION_TYPE), 
	                CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"),
	                CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key2"), 
	                CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key3"),
	                CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key4"));

	        Collection<CacheTarget> results = new ArrayList<CacheTarget>(invokeExhaustQueue(targets));
	        assertTrue(CollectionUtils.diff(correctResults, results).isEmpty());
	    }

	    @Test
	    public void testDuplicateCacheRemovalCase3() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	        Queue<CacheTarget> targets = Queues.newLinkedBlockingQueue();
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(ROLE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(DELEGATE_TYPE_CACHE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.entireCache(PERMISSION_TYPE));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"));
	        targets.add(CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key2"));
	        targets.add(CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key2"));

	        ArrayList<CacheTarget> correctResults = Lists.newArrayList(
	                CacheTarget.entireCache(ROLE_TYPE_CACHE), 
	                CacheTarget.entireCache(DELEGATE_TYPE_CACHE),
	                CacheTarget.entireCache(PERMISSION_TYPE), 
	                CacheTarget.singleEntry(ROLE_MEMBER_TYPE, "key1"),
	                CacheTarget.singleEntry(ROLE_RESPONSIBILITY_CACHE, "key2"));

	        Collection<CacheTarget> results = new ArrayList<CacheTarget>(invokeExhaustQueue(targets));
	        assertTrue(CollectionUtils.diff(correctResults, results).isEmpty());
	    }

	    /*
	     * Invoking the DistributedCacheManagerDecorator via reflection since the exhaustQueue method is a private method
	     * in a private inner class.
	     */
	    protected Collection<CacheTarget> invokeExhaustQueue(Queue<CacheTarget> targets) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
	        Class<?> c = DistributedCacheManagerDecorator.class;
	        Class<?>[] classes = c.getDeclaredClasses();
	        Class<?> correctInnerClass = null;

	        //Trying to find the correct inner class  
	        for (Class<?> clazz : classes) {
	            if (clazz.getName().endsWith(INNER_CLASS)) {
	                correctInnerClass = clazz;
	                break;
	            }
	        }

	        Constructor<?> constructor = correctInnerClass.getDeclaredConstructors()[0];
	        constructor.setAccessible(true);
	        Object inner = constructor.newInstance(c.newInstance());
	        Method method = inner.getClass().getDeclaredMethod("exhaustQueue", new Class[] {Queue.class});
	        method.setAccessible(true);

	        return (Collection<CacheTarget>) method.invoke(inner, new Object[]{targets});
	    }
  
}
