/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.core.bo.Note;
import org.kuali.core.document.Document;
import org.kuali.core.rule.AddNoteRule;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.util.ObjectUtils;

/**
 * This class represents the add note event that is part of an eDoc in Kuali. This is triggered when a user presses the add button
 * for a given note or it could happen when another piece of code calls the create note method in the document service.
 * 
 * 
 */
public final class AddNoteEvent extends KualiDocumentEventBase {
    private Note note;

    /**
     * Constructs an AddNoteEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public AddNoteEvent(String errorPathPrefix, Document document, Note note) {
        super("creating add note event for document " + getDocumentId(document), errorPathPrefix, document);
        this.note = (Note) ObjectUtils.deepCopy(note);
    }

    /**
     * Constructs an AddNoteEvent with the given document
     * 
     * @param document
     */
    public AddNoteEvent(Document document, Note note) {
        this("", document, note);
    }

    /**
     * This method retrieves the note associated with this event.
     * 
     * @return
     */
    public Note getNote() {
        return note;
    }

    public void validate() {
        super.validate();
        if (getNote() == null) {
            throw new IllegalArgumentException("invalid (null) note");
        }
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddNoteRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddNoteRule) rule).processAddNote(getDocument(), getNote());
    }
}