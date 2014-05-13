/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.rules.rule.event;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.AddCollectionLineRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Defines the add collection line event fired when a user adds a line in a collection in a document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddCollectionLineEvent extends DocumentEventBase {

    private String collectionName;
    private Object addLine;

    /**
     * Constructs an event for a document adding a line to the collection with the name {@code collectionName}.
     *
     * @param document the document containing the collection objects
     * @param collectionName the name of the collection object
     * @param addLine the object being added to the collection
     */
    public AddCollectionLineEvent(Document document, String collectionName, Object addLine) {
        this("", document, collectionName, addLine);
    }

    /**
     * Constructs an event for a document adding a line to the collection with the name {@code collectionName} with a
     * specific {@code errorPathPrefix}.
     *
     * @param errorPathPrefix the prefix to add to the error path for reporting messages
     * @param document the document containing the collection objects
     * @param collectionName the name of the collection object
     * @param addLine the object being added to the collection
     */
    public AddCollectionLineEvent(String errorPathPrefix, Document document, String collectionName, Object addLine) {
        this("approve", errorPathPrefix, document, collectionName, addLine);
    }

    /**
     * Constructs an event for a document adding a line to the collection with the name {@code collectionName} with a
     * specific {@code errorPathPrefix} and {@code eventType}.
     *
     * @param eventType the name of the type of event
     * @param errorPathPrefix the prefix to add to the error path for reporting messages
     * @param document the document containing the collection objects
     * @param collectionName the name of the collection object
     * @param addLine the object being added to the collection
     */
    protected AddCollectionLineEvent(String eventType, String errorPathPrefix, Document document, String collectionName, Object addLine) {
        super("creating " + eventType + " event for document " + DocumentEventBase.getDocumentId(document), errorPathPrefix, document);

        this.collectionName = collectionName;
        this.addLine = addLine;
    }

    /**
     * {@inheritDoc}
     *
     * Specifies that this class returns the {@link AddCollectionLineRule} class.
     */
    @Override
    public Class<AddCollectionLineRule> getRuleInterfaceClass() {
        return AddCollectionLineRule.class;
    }

    /**
     * {@inheritDoc}
     *
     * Invokes the specific rule in {@link AddCollectionLineRule}.
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCollectionLineRule) rule).processAddCollectionLine(this);
    }

    /**
     * The name of the collection being added to.
     *
     * @return the collection name
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * The object being added to the collection.
     *
     * @return the added object
     */
    public Object getAddLine() {
        return addLine;
    }

}
