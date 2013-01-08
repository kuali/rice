/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.components;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.TabGroup;
import org.kuali.rice.krad.uif.element.ContentElementBase;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.SyntaxHighlighter;

import java.util.ArrayList;
import java.util.List;

/**
 * The ComponentExhibit component is used to display demostrations of various components along with their source code
 * and documentation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentExhibit extends ContentElementBase {

    private List<Group> demoGroups;
    private List<String> demoSourceCode;
    private SyntaxHighlighter sourceCodeViewer;
    private FieldGroup docLinkFields;
    private TabGroup tabGroup;

    /**
     * Setup the tabGroup with the demoGroups and setup the sourceCodeViewer
     *
     * @see Component#performInitialization(org.kuali.rice.krad.uif.view.View, Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        //Setup tabGroup
        List<Component> tabItems = new ArrayList<Component>();
        tabItems.addAll(tabGroup.getItems());
        tabItems.addAll(demoGroups);
        tabGroup.setItems(tabItems);
        view.assignComponentIds(tabGroup);

        //source code viewer setup
        if(demoSourceCode != null && !demoSourceCode.isEmpty()){
            sourceCodeViewer.setSourceCode(demoSourceCode.get(0));
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        components.add(sourceCodeViewer);
        components.add(tabGroup);

        return components;
    }

    /**
     * Get the demoGroups demonstrating the component's features
     *
     * @return the demoGroups
     */
    public List<Group> getDemoGroups() {
        return demoGroups;
    }

    /**
     * Sets the demoGroups.  This SHOULD NOT be set by xml - use ComponentLibraryView's setDemoGroups.
     *
     * @param demoGroups
     */
    public void setDemoGroups(List<Group> demoGroups) {
        this.demoGroups = demoGroups;
    }

    /**
     * Get the xml sourceCode for the demoGroups' features being demonstrated.
     *
     * @return the sourceCode
     */
    public List<String> getDemoSourceCode() {
        return demoSourceCode;
    }

    /**
     * Sets the demoSourceCode. This SHOULD NOT be set by xml - ComponentLibraryView will automatically read the source.
     *
     * @param demoSourceCode
     */
    public void setDemoSourceCode(List<String> demoSourceCode) {
        this.demoSourceCode = demoSourceCode;
    }

    /**
     * The SyntaxHighlighter component being used by the exhibit to show the demoSourceCode
     *
     * @return the SyntaxHighlighter component
     */
    public SyntaxHighlighter getSourceCodeViewer() {
        return sourceCodeViewer;
    }

    /**
     * Set the SyntaxHighlighter sourceCodeViewer component
     *
     * @param sourceCodeViewer
     */
    public void setSourceCodeViewer(SyntaxHighlighter sourceCodeViewer) {
        this.sourceCodeViewer = sourceCodeViewer;
    }

    /**
     * Get the FieldGroup that contains links to the documentation
     * TODO not yet used
     * @return the FieldGroup that contains documentation links
     */
    public FieldGroup getDocLinkFields() {
        return docLinkFields;
    }

    /**
     * Sets the docLinkFields fieldGroup
     * @param docLinkFields
     */
    public void setDocLinkFields(FieldGroup docLinkFields) {
        this.docLinkFields = docLinkFields;
    }

    /**
     * Get the tabGroup used to display the demoGroups
     *
     * @return the tabGroup used to display the demoGroups
     */
    public TabGroup getTabGroup() {
        return tabGroup;
    }

    /**
     * Set the tabGroup used to display the demoGroups
     *
     * @param tabGroup
     */
    public void setTabGroup(TabGroup tabGroup) {
        this.tabGroup = tabGroup;
    }
}
