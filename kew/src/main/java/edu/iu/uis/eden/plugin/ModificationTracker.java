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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Tracks modifications to a set of files and directories.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ModificationTracker implements Modifiable {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
	
	// Maintains a map of root directories that we are watching to sub directories  
    private final Map dirTree = new HashMap();
    
    public synchronized void addURL(URL url) {
        addURL(dirTree, url);
    }
            
    private void addURL(Map parentMap, URL url)  {
        Node node = new Node(url);
        if (!parentMap.keySet().contains(node)) {
            Map subDir = new HashMap();
            parentMap.put(node, subDir);
            if (node.getFile().isDirectory()) {
                File[] files = node.getFile().listFiles();
                for (int index = 0; index < files.length; index++) {
                    try {
                        addURL(subDir, files[index].toURL());
                    } catch (MalformedURLException e) {
                        // TODO fix up this error handling
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    
    public boolean isModified() {
    	return isModified(dirTree);
    }
        
    private boolean isModified(Map dirTree) {
    	for (Iterator iterator = dirTree.keySet().iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			if (node.isModified() || isModified((Map)dirTree.get(node))) {
				return true;
			}
		}
    	return false;
    }
    
    private class Node implements Modifiable {
        private URL url;
        private File file;
        private long lastModified;
        public Node(URL url) {
            this.url = url;
            this.file = new File(url.getFile());
            this.lastModified = file.lastModified();
        }
        public URL getURL() {
            return url;
        }
        public File getFile() {
            return file;
        }
        public long getLastModified() {
            return lastModified;
        }
        public boolean isModified() {
            return lastModified != file.lastModified();
        }
        public boolean equals(Object object) {
            if (object instanceof Node) {
                return ((Node)object).getURL().equals(getURL());
            }
            return false;
        }
        public int hashCode() {
            return url.hashCode();
        }
    }

}
