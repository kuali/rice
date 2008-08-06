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
package org.kuali.rice.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.kuali.rice.core.exception.RiceRuntimeException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceUtilities {
	
    public static String collectStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
    
	public static String getIpNumber() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RiceRuntimeException("Error retrieving ip number.", e);
		}
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RiceRuntimeException("Error retrieving host name.", e);
		}
	}

	/**
	 * The standard Spring FileSystemResourceLoader does not support normal absolute file paths
	 * for historical backwards-compatibility reasons.  This class simply circumvents that behavior
	 * to allow proper interpretation of absolute paths (i.e. not stripping a leading slash)  
	 */
	private static class AbsoluteFileSystemResourceLoader extends FileSystemResourceLoader {
        @Override
        protected Resource getResourceByPath(String path) {
            return new FileSystemResource(path);
        }
	}

	/**
	 * Attempts to retrieve the resource stream.
	 * 
	 * @param resourceLoc resource location; syntax supported by {@link DefaultResourceLoader} 
	 * @return the resource stream or null if the resource could not be obtained
	 * @throws MalformedURLException
	 * @throws IOException
	 * @see DefaultResourceLoader
	 */
	public static InputStream getResourceAsStream(String resourceLoc) throws MalformedURLException, IOException {
	    AbsoluteFileSystemResourceLoader rl = new AbsoluteFileSystemResourceLoader();
	    rl.setClassLoader(Thread.currentThread().getContextClassLoader());
	    Resource r = rl.getResource(resourceLoc);
	    if (r.exists()) {
	        return r.getInputStream();
	    } else {
	        return null;
	    }
//	    
//        if (resourceLoc.lastIndexOf("classpath:") > -1) {
//            String configName = resourceLoc.split("classpath:")[1];
//            /*ClassPathResource cpr = new  ClassPathResource(configName, Thread.currentThread().getContextClassLoader());
//            if (cpr.exists()) {
//                return cpr.getInputStream();
//            } else {
//                return null;
//            }*/
//            return Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
//        } else if (resourceLoc.lastIndexOf("http://") > -1 || resourceLoc.lastIndexOf("file:/") > -1) {
//            return new URL(resourceLoc).openStream();
//        } else {
//            try {
//                return new FileInputStream(resourceLoc);
//            } catch (FileNotFoundException e) {
//                return null; // logged by caller
//            }
//        }
    }

	/**
     * This method searches for an exception of the specified type in the stack trace of the given
     * exception.
     * @param topLevelException the exception whose stack to traverse
     * @param exceptionClass the exception class to look for
     * @return the first instance of an exception of the specified class if found, or null otherwise
     */
    public static <T extends Throwable> T findExceptionInStack(Throwable topLevelException, Class<T> exceptionClass) {
        Throwable t = topLevelException;
        while (t != null) {
            if (exceptionClass.isAssignableFrom(t.getClass())) return (T) t;
            t = t.getCause();
        }
        return null;
    }
}