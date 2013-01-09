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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;

import java.io.Serializable;
import java.util.List;

/**
 * Theme for the current view, currently just a list of stylesheets and js files, but has the potential
 * for expansion in the future
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags(
        {@BeanTag(name = "viewTheme", parent = "Uif-ViewTheme"), @BeanTag(name = "baseTheme", parent = "Uif-BaseTheme"),
                @BeanTag(name = "classicKnsTheme", parent = "Uif-ClassicKnsTheme"),
                @BeanTag(name = "kradTheme", parent = "Uif-KradTheme")})
public class ViewTheme extends UifDictionaryBeanBase implements Serializable {
    private static final long serialVersionUID = 7063256242857896580L;

    private String imageDirectory;
    
    private List<String> cssFiles;
    private List<String> scriptFiles;

    /**
     * Path to the directory (either relative or absolute) that contains images for the theme
     * 
     * <p>
     * Configured diretory will populate the {@link org.kuali.rice.krad.uif.UifConstants.ContextVariableNames#THEME_IMAGES}
     * context variable which can be referenced with an expression for an image source
     * </p>
     * 
     * @return String theme image directory
     */
    public String getImageDirectory() {
        return imageDirectory;
    }

    /**
     * Setter for the directory that contains images for the theme
     * 
     * @param imageDirectory
     */
    public void setImageDirectory(String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * Get the css stylesheets to be imported for this view, this must be a list of .css files
     * with their relative paths
     *
     * @return List<String>
     */
    @BeanTagAttribute(name = "cssFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
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
    @BeanTagAttribute(name = "scriptFiles", type = BeanTagAttribute.AttributeType.LISTVALUE)
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
