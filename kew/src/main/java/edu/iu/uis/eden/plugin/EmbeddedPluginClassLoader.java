/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.SimpleEnumeration;

/**
 * A ClassLoader implementation which loads a KEW plugin from the classpath.  The plugin could 
 * be embedded within a directory location in the parent classloader's classpath or within a jar
 * on the parent classloader's classpath.
 * 
 * <p>Because of the method by which the embedded jars are stored (an archive within an archive) this
 * implementation must decompress all of the jars in the embedded plugin "lib" directory and then 
 * read all of the classes and resources from those jars into memory.  This means that using this
 * class will result in a decent amount of memory use (depending on the number of jars in the embedded
 * plugin) in order to allow for the performance of access to those resources to be acceptiable.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmbeddedPluginClassLoader extends PluginClassLoader {
    
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(EmbeddedPluginClassLoader.class);
	
	private static final int BUFFER_SIZE = 2048;
	
	/**
	 * A cache of all the bytes from all of the embedded jars.
	 */
	private Map<URL, Map<String, byte[]>> byteCache = new HashMap<URL, Map<String, byte[]>>();
	
	/**
	 * List of urls to embedded jars to include in the classpath of this classloader.
	 */
    private List<URL> embeddedJarUrls = new ArrayList<URL>();
    
    /**
     * Constucts the classloader to read the embedded plugin from the given path on the classpath. 
     */
    public EmbeddedPluginClassLoader(String pathToEmbeddedPlugin) {
    	initialize(pathToEmbeddedPlugin);
    }

    /**
     * Constucts the classloader to read the embedded plugin from the given path on the classpath.
     * The specified classloader will be the parent of this classloader.
     */
    public EmbeddedPluginClassLoader(ClassLoader parent, String pathToEmbeddedPlugin) {
    	super(parent);
    	initialize(pathToEmbeddedPlugin);
    }
    
    /**
     * Initializes the classloader from the gven embedded plugin path.  This process includes searching the
     * classpath for any .jar files inside of the embedded plugin.  Also, in order for this classloader to
     * be responsive enough for real world use, all of the classes in resources in those jars are loaded into
     * memory and cached for future access.
     */
    protected void initialize(String pathToEmbeddedPlugin) {
    	try {
    		establishEmbeddedUrls(pathToEmbeddedPlugin);
    		cacheBytes();
    	} catch (IOException e) {
    		throw new WorkflowRuntimeException(e);
    	} catch (URISyntaxException e) {
			throw new WorkflowRuntimeException(e);
		}
    }
    
    /**
     * Establishes the set of URLs to resources in this classloader's classpath.  The classpath of this classloader is
     * composed of the classes directory and all of the jars within the lib directory.  The classes directory is
     * added as a standard URL to the parent classloader.
     * <p>
     * The jars have to be handled differently because they may be embedded within the classpath inside of a jar as 
     * opposed to the standard jar loading model where they are located on the filesystem. 
     */
    protected void establishEmbeddedUrls(String pathToEmbeddedPlugin) throws IOException, URISyntaxException {
    	URL embeddedUrl = getParent().getResource(pathToEmbeddedPlugin);
    	if (embeddedUrl == null) {
    		throw new WorkflowRuntimeException("Could not locate embedded plugin on the classpath at: " + pathToEmbeddedPlugin);
    	}
    	URL embeddedClasses = getParent().getResource(pathToEmbeddedPlugin + "/" + CLASSES_DIR + "/");
    	if (embeddedClasses != null) {
    		addURL(embeddedClasses);
    	}
    	URL libUrl = getParent().getResource(pathToEmbeddedPlugin + "/" + LIB_DIR);
    	// 1) if the lib directory is located inside of a jar, we need to search the jar to locate all embedded jars
    	// 2) if the lib directory is on the filesystem, we can load the jars using default URL classloading behavior
    	if (libUrl.getProtocol().equals("jar")) {
    		String jarPath = getJarPath(libUrl);
    		JarFile jarFile = new JarFile(new File(new URI(jarPath)));
    		Enumeration enumeration = jarFile.entries();
    		String libPath = pathToEmbeddedPlugin + "/" + LIB_DIR + "/";
    		while (enumeration.hasMoreElements()) {
    			JarEntry entry = (JarEntry)enumeration.nextElement();
    			String name = entry.getName();
    			if (name.startsWith(libPath)) {
    				String fileName = name.substring(libPath.length());
    				if (!fileName.contains("/") && fileName.endsWith(".jar")) {
    					addEmbeddedJarURL(getParent().getResource(pathToEmbeddedPlugin + "/" + LIB_DIR + "/" + fileName));
    				}
    			}
    		}
    	} else if (libUrl.getProtocol().equals("file")) {
    		File libDirectory = new File(libUrl.toURI());
    		addLibDirectory(libDirectory);
    	}
    }
    
    /**
     * Reads all the data from the embedded jars into memory and caches the bytes of all their resources.
     */
    protected void cacheBytes() {
    	byte[] data = new byte[BUFFER_SIZE];
    	for (Iterator iterator = embeddedJarUrls.iterator(); iterator.hasNext();) {
			URL jarUrl = (URL) iterator.next();
			ZipInputStream inputStream = openStreamToEmbeddedJar(jarUrl);
	    	Map<String, byte[]> byteMap = byteCache.get(jarUrl);
	    	if (byteMap == null) {
	    		byteMap = new HashMap<String, byte[]>();
	    		byteCache.put(jarUrl, byteMap);
	    	}
			try {
	    		ZipEntry entry;
	    		while((entry = inputStream.getNextEntry()) != null) {
	    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    			BufferedOutputStream os = new BufferedOutputStream(baos, BUFFER_SIZE);
	    			try {
	    				int count;
	    				while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
	    					os.write(data, 0, count);
	    				}
	    				os.flush();
	    				byteMap.put(entry.getName(), baos.toByteArray());
	    			} finally {
	    				os.close();
	    			}
	    		}
	    	} catch (IOException e) {
	    		throw new WorkflowRuntimeException(e);
	    	} finally {
	    		try {
	    			inputStream.close();
	    		} catch (IOException e) {
	    			throw new WorkflowRuntimeException(e);
	    		}
	    	}	
		}
    }

    /**
     * Add a URL to an embedded JAR on the classpath.  This URL should be of a  form
     * similar to the following:
     * <pre>jar:file:/path/to/mainjar/mainjar.jar!/pathToEmbeddedPlugin/lib/myEmbeddedJar.jar</pre>
     */
    protected void addEmbeddedJarURL(URL embeddedJarURL) throws MalformedURLException {
    	embeddedJarUrls.add(embeddedJarURL);
    }
     
    /**
     * Attempts to locate the class with the given name.  It accomplishes this by first looking in the list of
     * standard URLs for the given class (this will search the plugin's "classes" directory).  If the class
     * cannot be located there it will then search in the embedded jars (this wil search the plugin's 
     * "lib" directory).
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	Class foundClass = null;
    	try {
    		foundClass = super.findClass(name);
    	} catch (ClassNotFoundException e) {
    		// now look in the embedded jars
    		foundClass = findClassInEmbeddedJars(name);
    	}
    	if (foundClass != null) {
			return foundClass;
		}
    	throw new ClassNotFoundException(name);
	}
    
    /**
     * Searches all of the embedded JARs for the Class with the given name.
     */
    protected Class findClassInEmbeddedJars(String className) {
    	String classNamePath = classNameToPath(className);
    	byte[] classBytes = null;
    	for (URL jarURLKey : byteCache.keySet()) {
    		Map<String, byte[]> byteMap = byteCache.get(jarURLKey);
    		byte[] bytes = byteMap.get(classNamePath);
    		if (bytes != null) {
    			classBytes = bytes;
    		}
    	}
    	if (classBytes != null) {
			return defineClass(className, classBytes, 0, classBytes.length);
    	}
    	return null;
    }
    
    /**
     * Return the cached bytes for the given jar and resource.
     */
    protected byte[] getCachedBytes(String jarPath, String resourceName) {
    	try {
    		Map<String, byte[]> byteMap = byteCache.get(new URL(jarPath));
    		if (byteMap != null) {
    			return byteMap.get(resourceName);
    		}
    	} catch (MalformedURLException e) {
    		throw new WorkflowRuntimeException(e);
    	}
    	return null;
    }
     
    /**
     * Opens a ZipInputStream to the embedded jar represented by the given URL.
     */
    protected ZipInputStream openStreamToEmbeddedJar(URL jarUrl) {
    	int sepIndex = jarUrl.getPath().indexOf("!");
    	if (sepIndex == -1) {
    		throw new WorkflowRuntimeException("Invalid embedded jar url found: " + jarUrl.toString());
    	}
    	// extract the path to the embedded jar within the classpath, given above example, this will be /pathToEmbeddedPlugin/lib/axis.jar
    	String jarPath = jarUrl.getPath().substring(sepIndex+2);
    	return new ZipInputStream(new BufferedInputStream(getParent().getResourceAsStream(jarPath), BUFFER_SIZE));
    }
    
    /**
     * Finds the resource with the given resourceName by first searching the embedded plugin's "classes" directory and
     * then the embedded JARs in the "lib" directory.  It accomplishes the location of embedded jar resources by
     * querying the internal resource cache to fetch the resource's bytes.
     */
	public URL findResource(String resourceName) {
		resourceName = normalizeResourceName(resourceName);
		URL resource = super.findResource(resourceName);
		if (resource == null) {
			try {
				for (URL jarUrl : byteCache.keySet()) {
					Map<String, byte[]> byteMap = byteCache.get(jarUrl);
					if (byteMap.get(resourceName) != null) {
						resource = new URL(null, jarUrl.toString()+"!/"+resourceName, new ByteURLStreamHandler(this));
						break;
					}
				} 
			} catch (MalformedURLException e) {
				throw new WorkflowRuntimeException(e);
			}
		}
		return resource;
	}
	
	/**
	 * Finds resources by first looking in the superclass, and then looking at
	 */
	public Enumeration<URL> findResources(String name) throws IOException {
		// TODO should this implementation somehow search for duplicates resources on the classpath?
		return new SimpleEnumeration<URL>(findResource(name));
	}
		    
    /**
     * A toString implementation which prints a list of all standard and embedded URLs in the classpath of this
     * classloader.
     */
	public String toString() {
        StringBuffer sb = new StringBuffer("[EmbeddedPluginClassLoader: urls=");
        URL[] urls = getURLs();
        if (urls == null) {
            sb.append("null");
        } else {
            for (int i = 0; i < urls.length; i++) {
                sb.append(urls[i]);
                sb.append(",");
            }
            for (Iterator iterator = embeddedJarUrls.iterator(); iterator.hasNext();) {
				URL jarUrl = (URL) iterator.next();
				sb.append(jarUrl);
				sb.append(",");
			}
            // remove trailing comma
            if (urls.length > 1) {
                sb.setLength(sb.length() - 1);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Extract the path to the classpath resources within the parent classloader where the
     * embedded jar represented by the given URL is located.
     * 
     * @param url The URL to the embedded jar
     * @return the path to embedded jar resources, should be of the form /pathToEmbeddedPlugin/lib/myEmbeddedJar.jar
     */
    private String getJarPath(URL url) {
    	String path = url.getPath();
    	return path.substring(0, path.indexOf("!"));
    }
    
    /**
     * Converts the given classname to a resource path.  This is accomplished by replacing all periods (.) with
     * forward slashes (/).
     */
    private String classNameToPath(String className) {
    	className = className.replace('.','/');
        return normalizeResourceName(className)+".class";
    }
    
    /**
     * Normalizes the given resource name by removing any leading forward slashes (/).
     */
	private String normalizeResourceName(String resourceName) {
		while (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		return resourceName;
	}
    
    /**
     * A simple URLStreamHandler implementation which is backed by a connection to an array of bytes.
     * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    private static class ByteURLStreamHandler extends URLStreamHandler {

    	private EmbeddedPluginClassLoader classLoader;
    	
    	public ByteURLStreamHandler(EmbeddedPluginClassLoader classLoader) {
    		this.classLoader = classLoader;
    	}
    	
		protected URLConnection openConnection(URL url) throws IOException {
			return new ByteURLConnection(url, classLoader);
		}
    	
    }
    
    /**
     * A simple URLConnection implementation which represents a connection to an array of bytes
     * via a URL to a resource within an embedded jar
     * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    private static class ByteURLConnection extends URLConnection {
    	
    	private String jarPath;
    	private String resourceName;
    	private EmbeddedPluginClassLoader classLoader;
    	private byte[] bytes;
    	
    	public ByteURLConnection(URL url, EmbeddedPluginClassLoader classLoader) {
    		super(url);
    		this.classLoader = classLoader;
    		String urlString = url.toString();
    		jarPath = urlString.substring(0, urlString.lastIndexOf("!"));
    		resourceName = urlString.substring(urlString.lastIndexOf("!")+2);
    	}
		
    	public void connect() throws IOException {
			bytes = classLoader.getCachedBytes(jarPath, resourceName);
			if (bytes == null) {
				throw new IOException("Could not access byte data for the given URL: " + url.toString());
			}
    	}

		public InputStream getInputStream() throws IOException {
			if (bytes == null) {
				connect();
			}
			return new BufferedInputStream(new ByteArrayInputStream(bytes), BUFFER_SIZE);
		}

		public int getContentLength() {
			return bytes.length;
		}

    }

    public void stop() {
    	LOG.info("Stopping the EmbeddedPluginClassLoader, clearing byte cache.");
    	byteCache.clear();
    	embeddedJarUrls.clear();
    	super.stop();
    }
	
}