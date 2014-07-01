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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Sets data bindings to read-only at the end of the apply model phase.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SetReadOnlyOnDataBindingTask extends ViewLifecycleTaskBase<DataBinding> {

    /**
     * Default constructor.
     */
    public SetReadOnlyOnDataBindingTask() {
        super(DataBinding.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        // implement readonly request overrides
        LifecycleElement element = getElementState().getElement();
        ViewModel viewModel = (ViewModel) ViewLifecycle.getModel();
        if ((element instanceof DataBinding)
                && ViewLifecycle.getView().isSupportsRequestOverrideOfReadOnlyFields()
                && !viewModel.getReadOnlyFieldsList().isEmpty()) {
            DataBinding dataBinding = (DataBinding) element;
            String propertyName = dataBinding.getPropertyName();
            if (viewModel.getReadOnlyFieldsList().contains(propertyName)) {
                ((Component) dataBinding).setReadOnly(true);
            }
        }
    }

}
