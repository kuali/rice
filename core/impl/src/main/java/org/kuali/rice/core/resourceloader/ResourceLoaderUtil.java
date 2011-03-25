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
package org.kuali.rice.core.resourceloader;

import org.apache.log4j.Logger;
import org.kuali.rice.core.resourceloader.ResourceLoader;

/**
 * A class for {@link ResourceLoader} related utilities.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ResourceLoaderUtil {
    private static final Logger LOG = Logger.getLogger(ResourceLoaderUtil.class);
    
	private ResourceLoaderUtil() {
		throw new UnsupportedOperationException("do not call");
	}

    /**
     * Instantiates className class via no-arg constructor, and returns a proxy
     * that wraps invocations with the specified classLoader as the context classloader
     * @param className the class to instantiate
     * @param classLoader the classLoader to set as the context classloader
     * @return a proxy that wraps an instance with code that sets and unsets the appropriate context classloader
     */
	public static Object createObject(String className, ClassLoader classLoader) {
		try {
			Class theClass = Class.forName(className, true, classLoader);
			return ContextClassLoaderProxy.wrap(theClass.newInstance());
		} catch (ClassNotFoundException e) {
            LOG.error("Error instantiating class '" + className + "' in classloader " + classLoader, e);
            return null;
		} catch (InstantiationException e) {
			throw new ResourceLoaderException(e);
		} catch (IllegalAccessException e) {
			throw new ResourceLoaderException(e);
		}
	}

}
