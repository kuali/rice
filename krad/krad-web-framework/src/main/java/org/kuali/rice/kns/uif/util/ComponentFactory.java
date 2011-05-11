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

import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.field.MessageField;

/**
 * Factory class for creating new UIF components
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactory {

    private static int nextId = 0;

    // TODO: need something better than this for id generation
    public static String getNextId() {
        String id = "id_" + nextId++;

        return id;
    }
    
    protected static final String MESSAGE_FIELD = "MessageField";

    public static MessageField getMessageField() {
        return (MessageField) getDataDictionaryService().getDictionaryObject(MESSAGE_FIELD);
    }

    protected static DataDictionaryService getDataDictionaryService() {
        return KNSServiceLocatorWeb.getDataDictionaryService();
    }

}
