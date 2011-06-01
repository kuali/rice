/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.MessageField;
import org.kuali.rice.kns.web.spring.form.UifFormBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating new UIF components from their base definitions
 * in the dictionary
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactory {

    private static final Map<String, Component> componentDefinitions = new HashMap<String, Component>();

    /**
     * Adds a component instance to the factories map of components indexed by the given dictionary id
     *
     * @param componentDictionaryId - id to index component under
     * @param component - component instance to add
     */
    public static void addComponentDefinition(String componentDictionaryId, Component component) {
       componentDefinitions.put(componentDictionaryId, ComponentUtils.copyObject(component));
    }

    /**
     * Returns a new <code>Component</code> instance from the given component id initialized by the
     * corresponding dictionary configuration
     *
     * @param componentDictionaryId - id for the component to retrieve, note this is the id intially
     * assigned and could be the spring generated id (if a static id was not set)
     * @return new component instance or null if no such component definition was found
     */
    public static Component getNewComponentInstance(String componentDictionaryId) {
        if (componentDefinitions.containsKey(componentDictionaryId)) {
            Component component = componentDefinitions.get(componentDictionaryId);

            return ComponentUtils.copyObject(component);
        }

        return null;
    }

    protected static final String MESSAGE_FIELD = "MessageField";
    public static MessageField getMessageField() {
        return (MessageField) getNewComponentInstance(MESSAGE_FIELD);
    }

    protected static DataDictionaryService getDataDictionaryService() {
        return KNSServiceLocatorWeb.getDataDictionaryService();
    }
    
    public static Component getComponentById(UifFormBase form, String id){
        String origId = id;
        
        if(id.contains("_")){
            id = StringUtils.substringBefore(id, "_");
        }
        Component component = getNewComponentInstance(id);
        
        form.getView().getViewHelperService().performComponentLifecycle(form, component, origId);
        form.getView().getViewIndex().indexComponent(component);
        return component;
    }

}
