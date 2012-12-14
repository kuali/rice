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
package org.kuali.rice.krad.demo.uif.components;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.TabGroup;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * View for the ComponentLibrary demo examples of Uif Components
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentLibraryView extends FormView {
    private static final long serialVersionUID = 3981186175467661843L;

    private String componentName;
    private String javaFullClassPath;
    private String xmlFilePath;
    private String description;
    private String usage;

    private Group detailsGroup;

    private ComponentExhibit exhibit;
    private List<Group> demoGroups;

    /**
     * Initializes the TabGroup that contains description and usage.  Processes ths source code marked with the
     * ex: comment tags and adds them to the ComponentExhibit for this view.
     *
     * @see Component#performInitialization(org.kuali.rice.krad.uif.view.View, Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        //set page name
        this.getPage().setHeaderText(this.getComponentName());

        TabGroup tabGroup = ComponentFactory.getTabGroup();
        List<Component> tabItems = new ArrayList<Component>();

        //Description processing
        Group descriptionGroup = ComponentFactory.getVerticalBoxGroup();

        //Description header
        Header descriptionHeader = (Header)ComponentFactory.getNewComponentInstance("Uif-SubSectionHeader");
        descriptionHeader.setHeaderLevel("H3");
        descriptionHeader.setHeaderText("Description");
        descriptionHeader.setRender(false);
        descriptionGroup.setHeader(descriptionHeader);
        
        //Description message
        List<Component> descriptionItems = new ArrayList<Component>();
        Message descriptionMessage = ComponentFactory.getMessage();
        descriptionMessage.setMessageText(description);
        descriptionItems.add(descriptionMessage);
        descriptionGroup.setItems(descriptionItems);

        tabItems.add(descriptionGroup);

        //Usage processing
        Group usageGroup = ComponentFactory.getVerticalBoxGroup();

        //Usage header
        Header usageHeader = (Header)ComponentFactory.getNewComponentInstance("Uif-SubSectionHeader");
        usageHeader.setHeaderLevel("H3");
        usageHeader.setHeaderText("Usage");
        usageHeader.setRender(false);
        usageGroup.setHeader(usageHeader);
        
        //Usage message
        List<Component> usageItems = new ArrayList<Component>();
        Message usageMessage = ComponentFactory.getMessage();
        usageMessage.setMessageText(usage);
        usageItems.add(usageMessage);
        usageGroup.setItems(usageItems);
        
        tabItems.add(usageGroup);

        //Properties processing
        Group propertiesGroup = ComponentFactory.getVerticalBoxGroup();

        tabGroup.setItems(tabItems);
        tabGroup.addStyleClass("demo-componentDetailsTabs");

        //Add tabGroup to detailsGroup
        List<Component> detailsItems = new ArrayList<Component>();
        detailsItems.addAll(detailsGroup.getItems());
        detailsItems.add(tabGroup);
        detailsGroup.setItems(detailsItems);
        view.assignComponentIds(detailsGroup);

        //exhibit setup
        List<String> sourceCode = new ArrayList<String>();

        //process source
        Map<String,String> idSourceMap = new HashMap<String,String>();
        if(xmlFilePath != null){
            try {
                //Get the source file
                URL fileUrl = ComponentLibraryView.class.getClassLoader().getResource(xmlFilePath);
                File file = new File(fileUrl.toURI());
                Pattern examplePattern = Pattern.compile("ex:(.*?)(\\s|(-->))");

                boolean readingSource = false;
                String currentSource = "";
                String currentId = "";

                LineIterator lineIt = FileUtils.lineIterator(file);
                while(lineIt.hasNext()){
                    String line = lineIt.next();
                    if(line.contains("ex:") && !readingSource){
                        //found a ex: tag and are not already reading source
                        readingSource = true;

                        Matcher matcher = examplePattern.matcher(line);
                        if(matcher.find()){
                           currentId = matcher.group(1);
                        }

                        currentSource = idSourceMap.get(currentId) != null? idSourceMap.get(currentId):"";

                        if(!currentSource.isEmpty()){
                            currentSource = currentSource + "\n";
                        }
                    }
                    else if(line.contains("ex:") && readingSource){
                        //stop reading source on second ex tag
                        readingSource = false;
                        idSourceMap.put(currentId, currentSource);
                    }
                    else if(readingSource){
                        //when reading source just continue to add it
                        currentSource = currentSource + line + "\n";
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException("file not found or error while reading: "
                        + xmlFilePath + " for source reading", e);
            }
        }

        for(Group demoGroup: demoGroups){
            //add source to the source list by order that demo groups appear
            String groupId = demoGroup.getId();
            String source = idSourceMap.get(groupId);
            if(source != null){
                //translate the source to something that can be displayed
                sourceCode.add(translateSource(source));
            }
        }

        //setup exhibit
        exhibit.setDemoSourceCode(sourceCode);
        exhibit.setDemoGroups(this.getDemoGroups());

        //Add detailsGroup and exhibit to page
        List<Component> pageItems = new ArrayList<Component>();
        pageItems.addAll(this.getPage().getItems());
        pageItems.add(exhibit);
        pageItems.add(detailsGroup);
        this.getPage().setItems(pageItems);
    }

    /**
     * Translates the source by removing chracters that the dom will misinterpret as html and to ensure
     * source spacing is correct
     *
     * @param source the original source
     * @return that translated source used in the SyntaxHighlighter of the exhibit
     */
    private String translateSource(String source){
        //convert characters to ascii equivalent
        source = source.replace("<","&lt;");
        source = source.replace(">","&gt;");
        source = source.replaceAll("[ \\t]","&#32;");

        Pattern linePattern = Pattern.compile("((&#32;)*).*?(\\n)+");
        Matcher matcher = linePattern.matcher(source);
        int toRemove = -1;

        //find the line with the least amount of spaces
        while(matcher.find()){
            String spaces = matcher.group(1);

            int count = StringUtils.countOccurrencesOf(spaces, "&#32;");
            if(toRemove == -1 || count < toRemove){
                toRemove = count;
            }
        }

        matcher.reset();
        String newSource = "";

        //remove the min number of spaces from each line to get them to align left properly in the viewer
        while(matcher.find()){
            String line = matcher.group();
            newSource = newSource + line.replaceFirst("(&#32;){" + toRemove + "}", "");
        }

        //remove very last newline
        newSource = newSource.replaceAll("\\n$", "");
        //replace remaining newlines with ascii equivalent
        newSource = newSource.replace("\n", "&#010;");

        return newSource;
    }

    /**
     * ComponentLibraryView constructor
     */
    public ComponentLibraryView(){
        demoGroups = new ArrayList<Group>();
    }

    /**
     * The name of the component (to be used by this page's header)
     *
     * @return componentName the name of the component being demoed
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the componentName
     *
     * @param componentName
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * Set the java path to the class being used by this component
     * TODO not yet used
     *
     * @return the java path to the class
     */
    public String getJavaFullClassPath() {
        return javaFullClassPath;
    }

    /**
     * Get the java full class path
     *
     * @param javaFullClassPath
     */
    public void setJavaFullClassPath(String javaFullClassPath) {
        this.javaFullClassPath = javaFullClassPath;
    }

    /**
     * The xml file path that contains the source being used for this demo, must start with / (relative path)
     *
     * @return the xml file path
     */
    public String getXmlFilePath() {
        return xmlFilePath;
    }

    /**
     * Set the xml file path
     *
     * @param xmlFilePath
     */
    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    /**
     * The description of the component being demoed by this view
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the usage description and examples of how to use this component
     *
     * @return the usage text
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Set the usage text
     *
     * @param usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * The details group that will contain the description, usage, and properties tabGroup
     *
     * @return the details group
     */
    public Group getDetailsGroup() {
        return detailsGroup;
    }

    /**
     * Set the details group
     *
     * @param detailsGroup
     */
    public void setDetailsGroup(Group detailsGroup) {
        this.detailsGroup = detailsGroup;
    }

    /**
     * Gets the exhibit that will display the example, source code, and tabs to switch between examples
     *
     * @return the ComponentExhibit for this component demo view
     */
    public ComponentExhibit getExhibit() {
        return exhibit;
    }

    /**
     * Set the ComponentExhibit for this demo
     *
     * @param exhibit
     */
    public void setExhibit(ComponentExhibit exhibit) {
        this.exhibit = exhibit;
    }

    /**
     * List of groups that will demostrate the functionality fo the component being demonstrated, these groups are
     * copied directly into componentExhibit - this is an ease of use property
     *
     * @return the demoGroups
     */
    public List<Group> getDemoGroups() {
        return demoGroups;
    }

    /**
     * Set the demoGroups used for demonstrating features of the component
     *
     * @param demoGroups
     */
    public void setDemoGroups(List<Group> demoGroups) {
        this.demoGroups = demoGroups;
    }
}
