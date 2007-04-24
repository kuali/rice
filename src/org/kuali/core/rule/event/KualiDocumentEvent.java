/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.rule.event;

import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;

/**
 * Parent interface of all document-related events, which are used to drive the business rules evaluation process.
 * 
 * 
 */
public interface KualiDocumentEvent {
    /**
     * @return Document The document associated with this event
     */
    public Document getDocument();

    /**
     * The name of the event.
     * 
     * @return String
     */
    public String getName();

    /**
     * A description of the event.
     * 
     * @return String
     */
    public String getDescription();


    /**
     * @return errorPathPrefix for this event
     */
    public String getErrorPathPrefix();

    /**
     * Returns the interface that classes must implement to recieve this event.
     * 
     * @return
     */
    public Class getRuleInterfaceClass();

    /**
     * Validates the event has all the necessary properties.
     */
    public void validate();

    /**
     * Invokes the event handling method on the rule object.
     * 
     * @param rule
     * @return
     */
    public boolean invokeRuleMethod(BusinessRule rule);

    /**
     * This will return a list of events that are spawned from this event.
     * 
     * @return
     */
    public List generateEvents();
}