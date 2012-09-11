/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;

import java.io.Serializable;
import java.util.List;

/**
 * Theme for the current view, currently just a list of stylesheets and js files, but has the potential
 * for expansion in the future
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewTheme extends UifDictionaryBeanBase implements Serializable{
    private static final long serialVersionUID = 7063256242857896580L;

    private List<String> cssFiles;
    private List<String> scriptFiles;

    /**
     * Get the css stylesheets to be imported for this view, this must be a list of .css files
     * with their relative paths
     *
     * @return List<String>
     */
    public List<String> getCssFiles() {
        return cssFiles;
    }

    /**
     * Set the css stylesheets
     *
     * @param cssFiles
     */
    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }

    /**
     * Get the javascript files to be imported for this view, these must be a list of .js files
     * with their relative paths
     *
     * @return List<String>
     */
    public List<String> getScriptFiles() {
        return scriptFiles;
    }

    /**
     * Set the js files
     *
     * @param scriptFiles
     */
    public void setScriptFiles(List<String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }
}
