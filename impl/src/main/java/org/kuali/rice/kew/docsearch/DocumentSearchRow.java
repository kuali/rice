/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.docsearch;

import java.util.List;

import org.kuali.rice.kew.docsearch.DocumentSearchField;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;



/**
 * A Row is a collection of {@link Field} objects that represents a row of results
 * from a {@link WorkflowLookupable}.
 *
 * @deprecated
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchRow extends Row {

    /**
     *
     */
    public DocumentSearchRow() {
        // TODO jjhanso - THIS CONSTRUCTOR NEEDS A JAVADOC
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     *
     * @param field
     */
    public DocumentSearchRow(DocumentSearchField field) {
        // TODO jjhanso - THIS CONSTRUCTOR NEEDS A JAVADOC
        super(field);
        // TODO Auto-generated constructor stub
    }

    /**
     *
     * @param fields
     */
    public DocumentSearchRow(List<Field> fields) {
        super(fields);
        super.setHidden(false);
    }



    public DocumentSearchField getField(int index) {
        while (super.getFields().size() <= index) {
            DocumentSearchField field = new DocumentSearchField();
            super.getFields().add(field);
        }
        return (DocumentSearchField) super.getFields().get(index);
    }

}
