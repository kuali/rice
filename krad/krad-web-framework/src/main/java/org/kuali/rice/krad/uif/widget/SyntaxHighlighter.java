/*
 * Copyright 2006-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

import java.io.Serializable;

/**
 * Widget that renders text syntax highlighted
 *
 * <p>
 * The widget renders a div with a header. In the div the source code text will be added in pre tags with the
 * specified plugin class that is needed for the plugin to alter the text. An onDocumentReadyScript listener is needed
 * on the page to call the plugin's prettyPrint() function. This is set on the page definition.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "syntaxHighlighter", parent = "Uif-SyntaxHighlighter")
public class SyntaxHighlighter extends WidgetBase {

    private String sourceCodeHeader;
    private String sourceCode;
    private String pluginCssClass;
    
    public SyntaxHighlighter() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }

    /**
     * The header text to display above the source code text
     *
     * @return String
     */
    public String getSourceCodeHeader() {
        return sourceCodeHeader;
    }

    /**
     * Setter for the widget header text
     *
     * @param sourceCodeHeader
     */
    public void setSourceCodeHeader(String sourceCodeHeader) {
        this.sourceCodeHeader = sourceCodeHeader;
    }

    /**
     * The text to render with syntax highlighting
     *
     * @return String
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Setter for the source code text
     *
     * @param sourceCode
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * The class that will be set on the pre tags
     *
     * <p>
     * The class is used by the prettify plugin to identify text to highlight and to specify type of highlighting.
     * </p>
     *
     * @return String
     */
    public String getPluginCssClass() {
        return pluginCssClass;
    }

    /**
     * Setter for the plugin css class
     *
     * @param pluginCssClass
     */
    public void setPluginCssClass(String pluginCssClass) {
        this.pluginCssClass = pluginCssClass;
    }
}
