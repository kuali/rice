/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.instrument.classloading.WeavingTransformer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class loader for which supports instrumentation and load-time weaving of it's classes by "shadowing" the classes
 * of it's enclosing classloader by loading, defining, and transfomring the byte code within the context of this
 * classloader.
 *
 * <p>Much of this code is based on Spring's {@code ShadowingClassLoader}.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
*/
public class ShadowingInstrumentableClassLoader extends DecoratingClassLoader {

    private static final String[] DEFAULT_EXCLUDES = new String[] {
            "java.", "javax.", "sun.", "oracle.", "com.sun.", "com.ibm.", "COM.ibm.", "org.w3c.", "org.xml.",
            "org.dom4j.", "org.eclipse", "org.aspectj.", "net.sf.cglib", "org.springframework.cglib",
            "org.apache.xerces.", "org.apache.commons.logging."
    };

    private final ClassLoader enclosingClassLoader;
    private final WeavingTransformer weavingTransformer;

    private final Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    private final Set<String> attempted = new HashSet<String>();

    public ShadowingInstrumentableClassLoader(ClassLoader enclosingClassLoader) {
        this(enclosingClassLoader, null);
    }

    public ShadowingInstrumentableClassLoader(ClassLoader enclosingClassLoader, String[] excludedPackages) {
        if (enclosingClassLoader == null) {
            throw new IllegalArgumentException("Enclosing ClassLoader must not be null");
        }
        this.weavingTransformer = new WeavingTransformer(this);
        this.enclosingClassLoader = enclosingClassLoader;
        for (String defaultExcludedPackage : DEFAULT_EXCLUDES) {
            excludePackage(defaultExcludedPackage);
        }
        if (excludedPackages != null) {
            for (String excludedPackage : excludedPackages) {
                excludePackage(excludedPackage);
            }
        }
    }

    public void addTransformer(ClassFileTransformer transformer) {
        weavingTransformer.addTransformer(transformer);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = this.classCache.get(name);
        if (cls != null) {
            return cls;
        }
        if (!attempted.contains(name) && shouldInstrument(name)) {
            return loadAndInstrumentClass(name);
        }
        else {
            return this.enclosingClassLoader.loadClass(name);
        }
    }

    /**
     * Determine whether the given class should be excluded from instrumentation.
     *
     * @param className the name of the class
     *
     * @return whether the specified class should be instrumented
     */
    private boolean shouldInstrument(String className) {
        return !className.equals(getClass().getName()) && !isExcluded(className);
    }

    private Class<?> loadAndInstrumentClass(String name) throws ClassNotFoundException {
        String internalName = StringUtils.replace(name, ".", "/") + ".class";
        attempted.add(name);
        attempted.add(internalName);
        InputStream is = this.enclosingClassLoader.getResourceAsStream(internalName);
        if (is == null) {
            throw new ClassNotFoundException(name);
        }
        byte[] bytes;
        try {
            bytes = ByteStreams.toByteArray(is);
        } catch (IOException e) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", e);
        } finally {
            Closeables.closeQuietly(is);
        }
        bytes = weavingTransformer.transformIfNecessary(name, bytes);
        Class<?> cls = defineClass(name, bytes, 0, bytes.length);
        // Additional check for defining the package, if not defined yet.
        if (cls.getPackage() == null) {
            int packageSeparator = name.lastIndexOf('.');
            if (packageSeparator != -1) {
                String packageName = name.substring(0, packageSeparator);
                definePackage(packageName, null, null, null, null, null, null, null);
            }
        }
        this.classCache.put(name, cls);
        return cls;
    }

    @Override
    public URL getResource(String name) {
        return this.enclosingClassLoader.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.enclosingClassLoader.getResourceAsStream(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.enclosingClassLoader.getResources(name);
    }

}
