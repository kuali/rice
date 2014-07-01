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
package org.kuali.rice.krad.uif.lifecycle.finalize;

import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * If the data field has a configured property editor registers the editor with the view
 * post metadata.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.field.DataField#getPropertyEditor()
 */
public class RegisterPropertyEditorTask extends ViewLifecycleTaskBase<DataField> {

    /**
     * Default constructor.
     */
    public RegisterPropertyEditorTask() {
        super(DataField.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        LifecycleElement element = getElementState().getElement();

        if (!(element instanceof DataField)) {
            return;
        }

        DataField dataField = (DataField) element;

        if ((dataField.getPropertyEditor() == null) || !dataField.isRender()) {
            return;
        }

        ViewPostMetadata viewPostMetadata = ViewLifecycle.getViewPostMetadata();
        if (dataField.hasSecureValue()) {
            viewPostMetadata.addSecureFieldPropertyEditor(dataField.getBindingInfo().getBindingPath(),
                    dataField.getPropertyEditor());
        } else {
            viewPostMetadata.addFieldPropertyEditor(dataField.getBindingInfo().getBindingPath(),
                    dataField.getPropertyEditor());
        }
    }
}
