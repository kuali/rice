/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ojb;

import java.util.Collection;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.core.proxy.CollectionProxyDefaultImpl;
import org.apache.ojb.broker.query.Query;
import org.kuali.rice.util.ClassLoaderUtils;
import org.kuali.rice.util.ContextClassLoaderBinder;

/**
 * Sets up the context classloader properly for OJB proxies.  The sequence of events in the super class is as
 * follows:
 * 
 * <pre>
 * public synchronized Collection getData()
 * {
 *    if (!isLoaded())
 *    {
 *      beforeLoading();
 *      setData(loadData());
 *      afterLoading();
 *    }
 *    return _data;
 * }
 * </pre>
 *
 * Therefore, since there is no try-catch-finally in conjunction with this call, we need to handle exceptions thrown
 * from loadData and unbind the classloader appropriately.
 */
public class ContextClassLoaderCollectionProxy extends CollectionProxyDefaultImpl {

	private static final long serialVersionUID = 6425238292133556808L;

	// this object is used by object that's are serialized and we don't want to take this reference
	// across the wire.  May need to acquire the CCL if this is null on beforeLoading() doing nothing for now...
	private transient ClassLoader contextClassLoader;
	
	public ContextClassLoaderCollectionProxy(PBKey aKey, Class aCollClass, Query aQuery) {
		super(aKey, aCollClass, aQuery);
		this.contextClassLoader = ClassLoaderUtils.getDefaultClassLoader();
	}

	public ContextClassLoaderCollectionProxy(PBKey aKey, Query aQuery) {
		super(aKey, aQuery);
		this.contextClassLoader = ClassLoaderUtils.getDefaultClassLoader();	
	}

	@Override
	protected void beforeLoading() {
		ContextClassLoaderBinder.bind(this.contextClassLoader);
		super.beforeLoading();
	}
	
	@Override
	protected Collection loadData() throws PersistenceBrokerException {
		try {
			return super.loadData();
		} catch (Throwable t) {
			ContextClassLoaderBinder.unbind();
			if (t instanceof PersistenceBrokerException) {
				throw (PersistenceBrokerException)t;
			} else if (t instanceof Error) {
				throw (Error)t;
			} else if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				throw new PersistenceBrokerException("Invalid exception type thrown!", t);
			}
		}
	}

	@Override
	protected void afterLoading() {
		super.afterLoading();
		ContextClassLoaderBinder.unbind();
	}
	
}
