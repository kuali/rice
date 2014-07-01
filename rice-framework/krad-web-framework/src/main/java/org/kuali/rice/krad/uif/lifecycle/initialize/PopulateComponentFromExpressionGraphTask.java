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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;

/**
 * Populate property values on the component from the expression graph.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PopulateComponentFromExpressionGraphTask extends ViewLifecycleTaskBase<UifDictionaryBean> {

    /**
     * Default constructor.
     */
    public PopulateComponentFromExpressionGraphTask() {
        super(UifDictionaryBean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        // the component can have an expression graph for which the expressions need pulled to
        // the list the expression service will evaluate
        ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(
                (UifDictionaryBean) getElementState().getElement(), true);
    }

}
